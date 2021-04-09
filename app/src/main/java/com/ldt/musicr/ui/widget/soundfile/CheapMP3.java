package com.ldt.musicr.ui.widget.soundfile;

/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;

import com.ldt.musicr.App;

import java.io.InputStream;

/**
 * CheapMP3 represents an MP3 file by doing a "cheap" scan of the file,
 * parsing the frame headers only and getting an extremely rough estimate
 * of the volume level of each frame.
 * <p>
 * Modified by Anna Stępień <anna.stepien@semantive.com>
 */
public class CheapMP3 extends SoundFile {
    private static final String TAG = "CheapMP3";

    public static Factory getFactory() {
        return new Factory() {
            public SoundFile create() {
                return new CheapMP3();
            }

            public String[] getSupportedExtensions() {
                return new String[]{"mp3"};
            }
        };
    }

    // Member variables representing frame data
    private int mNumFrames;
    private int[] mFrameGains;
    private int mFileSize;
    private int mAvgBitRate;
    private int mGlobalSampleRate;
    private int mGlobalChannels;

    // Member variables used during initialization
    private int mMaxFrames;
    private int mBitrateSum;
    private int mMinGain;
    private int mMaxGain;

    public CheapMP3() {
    }

    public int getNumFrames() {
        return mNumFrames;
    }

    public int getSamplesPerFrame() {
        return 1152;
    }

    public int[] getFrameGains() {
        return mFrameGains;
    }

    public int getFileSizeBytes() {
        return mFileSize;
    }

    public int getAvgBitrateKbps() {
        return mAvgBitRate;
    }

    public int getSampleRate() {
        return mGlobalSampleRate;
    }

    public int getChannels() {
        return mGlobalChannels;
    }

    public String getFiletype() {
        return "MP3";
    }

    public void readFile(Uri uri)
            throws
            java.io.IOException, NullPointerException {
        super.readFile(uri);
        mNumFrames = 0;
        mMaxFrames = 64;  // This will grow as needed
        mFrameGains = new int[mMaxFrames];
        mBitrateSum = 0;
        mMinGain = 255;
        mMaxGain = 0;

        Log.d(TAG, "ReadFile: will open a input stream soon");
        InputStream stream = null;
        AssetFileDescriptor file;
        file = App.getInstance().getContentResolver().openAssetFileDescriptor(uri, "r");

        if(file == null) throw  new NullPointerException("File is null");

        stream = file.createInputStream();
        if(stream == null) throw new NullPointerException("Input stream is null");

        else Log.d("audioSeekbar", "ReadFile: input stream is not null");

        // No need to handle filesizes larger than can fit in a 32-bit int
        mFileSize = (int) file.getLength();

        int pos = 0;
        int offset = 0;
        byte[] buffer = new byte[12];
        while (pos < mFileSize - 12) {
            // Read 12 bytes at a time and look for a updateTouchRuntime code (0xFF)
            while (offset < 12) {
                offset += stream.read(buffer, offset, 12 - offset);
            }
            int bufferOffset = 0;
            while (bufferOffset < 12 &&
                    buffer[bufferOffset] != -1)
                bufferOffset++;

            if (mProgressListener != null) {
                boolean keepGoing = mProgressListener.reportProgress(
                        pos * 1.0 / mFileSize);
                if (!keepGoing) {
                    break;
                }
            }

            if (bufferOffset > 0) {
                // We didn't find a updateTouchRuntime code (0xFF) at posTop 0;
                // shift the buffer over and try again
                if (12 - bufferOffset >= 0) {
                    System.arraycopy(buffer, bufferOffset, buffer, 0, 12 - bufferOffset);
                }
                pos += bufferOffset;
                offset = 12 - bufferOffset;
                continue;
            }

            // Check for MPEG 1 EnumLayer III or MPEG 2 EnumLayer III codes
            int mpgVersion = 0;
            if (buffer[1] == -6 || buffer[1] == -5) {
                mpgVersion = 1;
            } else if (buffer[1] == -14 || buffer[1] == -13) {
                mpgVersion = 2;
            } else {
                bufferOffset = 1;
                System.arraycopy(buffer, bufferOffset + 0, buffer, 0, 12 - bufferOffset);
                pos += bufferOffset;
                offset = 12 - bufferOffset;
                continue;
            }

            // The third byte has the bitrate and samplerate
            int bitRate;
            int sampleRate;
            if (mpgVersion == 1) {
                // MPEG 1 EnumLayer III
                bitRate = BITRATES_MPEG1_L3[(buffer[2] & 0xF0) >> 4];
                sampleRate = SAMPLERATES_MPEG1_L3[(buffer[2] & 0x0C) >> 2];
            } else {
                // MPEG 2 EnumLayer III
                bitRate = BITRATES_MPEG2_L3[(buffer[2] & 0xF0) >> 4];
                sampleRate = SAMPLERATES_MPEG2_L3[(buffer[2] & 0x0C) >> 2];
            }

            if (bitRate == 0 || sampleRate == 0) {
                bufferOffset = 2;
                System.arraycopy(buffer, bufferOffset + 0, buffer, 0, 12 - bufferOffset);
                pos += bufferOffset;
                offset = 12 - bufferOffset;
                continue;
            }

            // From here on we assume the frame is good
            mGlobalSampleRate = sampleRate;
            int padding = (buffer[2] & 2) >> 1;
            int frameLen = 144 * bitRate * 1000 / sampleRate + padding;

            int gain;
            if ((buffer[3] & 0xC0) == 0xC0) {
                // 1 channel
                mGlobalChannels = 1;
                if (mpgVersion == 1) {
                    gain = ((buffer[10] & 0x01) << 7) +
                            ((buffer[11] & 0xFE) >> 1);
                } else {
                    gain = ((buffer[9] & 0x03) << 6) +
                            ((buffer[10] & 0xFC) >> 2);
                }
            } else {
                // 2 channels
                mGlobalChannels = 2;
                if (mpgVersion == 1) {
                    gain = ((buffer[9] & 0x7F) << 1) +
                            ((buffer[10] & 0x80) >> 7);
                } else {
                    gain = 0;  // ???
                }
            }

            mBitrateSum += bitRate;

            mFrameGains[mNumFrames] = gain;
            if (gain < mMinGain)
                mMinGain = gain;
            if (gain > mMaxGain)
                mMaxGain = gain;

            mNumFrames++;
            if (mNumFrames == mMaxFrames) {
                // We need to grow our arrays.  Rather than naively
                // doubling the array each time, we estimate the exact
                // number of frames we need and add 10% padding.  In
                // practice this seems to work quite well, only one
                // onTap is ever needed, however to avoid pathological
                // cases we make sure to always double the size at a minimum.

                mAvgBitRate = mBitrateSum / mNumFrames;
                int totalFramesGuess =
                        ((mFileSize / mAvgBitRate) * sampleRate) / 144000;
                int newMaxFrames = totalFramesGuess * 11 / 10;
                if (newMaxFrames < mMaxFrames * 2)
                    newMaxFrames = mMaxFrames * 2;

                int[] newOffsets = new int[newMaxFrames];
                int[] newLens = new int[newMaxFrames];
                int[] newGains = new int[newMaxFrames];
                if (mNumFrames >= 0) System.arraycopy(mFrameGains, 0, newGains, 0, mNumFrames);
                mFrameGains = newGains;
                mMaxFrames = newMaxFrames;
            }

            stream.skip(frameLen - 12);
            pos += frameLen;
            offset = 0;
        }

        // We're done reading the file, do some postprocessing
        if (mNumFrames > 0)
            mAvgBitRate = mBitrateSum / mNumFrames;
        else
            mAvgBitRate = 0;
    }

    static private int[] BITRATES_MPEG1_L3 = {
            0, 32, 40, 48, 56, 64, 80, 96,
            112, 128, 160, 192, 224, 256, 320, 0};
    static private int[] BITRATES_MPEG2_L3 = {
            0, 8, 16, 24, 32, 40, 48, 56,
            64, 80, 96, 112, 128, 144, 160, 0};
    static private int[] SAMPLERATES_MPEG1_L3 = {
            44100, 48000, 32000, 0};
    static private int[] SAMPLERATES_MPEG2_L3 = {
            22050, 24000, 16000, 0};
}