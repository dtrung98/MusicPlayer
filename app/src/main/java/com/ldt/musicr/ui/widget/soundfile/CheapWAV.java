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


        import android.util.Log;

        import java.io.File;

/**
 * CheapWAV represents a standard 16-bit WAV file, splitting it into
 * artificial frames to get an approximation of the waveform contour.
 *
 * Modified by Anna Stępień <anna.stepien@semantive.com>
 */
public class CheapWAV extends CheapSoundFile {
    public static final String TAG = "CheapWAV";

    public static Factory getFactory() {
        return new Factory() {
            public CheapSoundFile create() {
                return new CheapWAV();
            }
            public String[] getSupportedExtensions() {
                return new String[]{"wav"};
            }
        };
    }

    // Member variables containing frame info
    private int mNumFrames;
    private int[] mFrameGains;
    private int mFileSize;
    private int mSampleRate;
    private int mChannels;

    public CheapWAV() {
    }

    public int getNumFrames() {
        return mNumFrames;
    }

    public int getSamplesPerFrame() {
        return 1024;
    }

    public int[] getFrameGains() {
        return mFrameGains;
    }

    public int getFileSizeBytes() {
        return mFileSize;
    }

    public int getAvgBitrateKbps() {
        return mSampleRate * mChannels * 2 / 1024;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public int getChannels() {
        return mChannels;
    }

    public String getFiletype() {
        return "WAV";
    }

    public void ReadFile(File inputFile) throws java.io.IOException {
        super.ReadFile(inputFile);
        mFileSize = (int) mInputFile.length();

        if (mFileSize < 128) {
            throw new java.io.IOException("File too small to parse");
        }
        try {
            WavFile wavFile = WavFile.openWavFile(inputFile);
            mNumFrames = (int) (wavFile.getNumFrames() / getSamplesPerFrame());
            mFrameGains = new int[mNumFrames];
            mSampleRate = (int) wavFile.getSampleRate();
            mChannels = wavFile.getNumChannels();

            int gain, value;
            int[] buffer = new int[getSamplesPerFrame()];
            for (int i = 0; i < mNumFrames; i++) {
                gain = -1;
                wavFile.readFrames(buffer, getSamplesPerFrame());
                for (int j = 0; j < getSamplesPerFrame(); j++) {
                    value = buffer[j];
                    if (gain < value) {
                        gain = value;
                    }
                }
                mFrameGains[i] = (int) Math.sqrt(gain);
                if (mProgressListener != null) {
                    boolean keepGoing = mProgressListener.reportProgress(i * 1.0 / mFrameGains.length);
                    if (!keepGoing) {
                        break;
                    }
                }
            }
            if (wavFile != null) {
                wavFile.close();
            }
        } catch (WavFileException e) {
            Log.e(TAG, "Exception while reading wav file", e);
        }
    }
}