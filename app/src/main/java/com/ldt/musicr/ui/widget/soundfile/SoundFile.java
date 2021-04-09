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

        import android.net.Uri;
        import android.provider.MediaStore;
        import android.util.Log;

        import com.ldt.musicr.model.Song;
        import com.ldt.musicr.ui.widget.avsb.ProgressListener;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.HashMap;

/**
 * CheapSoundFile is the parent class of several subclasses that each
 * do a "cheap" scan of various sound file formats, parsing as little
 * as possible in order to understand the high-level frame structure
 * and get a rough estimate of the volume level of each frame.  Each
 * subclass is able to:
 *  - open a sound file
 *  - return the sample rate and number of frames
 *  - return an approximation of the volume level of each frame
 *
 * A frame should represent no less than 1 ms and no more than 100 ms of
 * audio.  This is compatible with the native frame sizes of most audio
 * file formats already, but if not, this class should expose virtual
 * frames in that size range.
 *
 * Modified by Anna Stępień <anna.stepien@semantive.com>
 */
public class SoundFile {
    public interface Factory {
        SoundFile create();
        String[] getSupportedExtensions();
    }

    static Factory[] sSubclassFactories = new Factory[] {
            CheapAAC.getFactory(),
            CheapAMR.getFactory(),
            CheapMP3.getFactory(),
            CheapWAV.getFactory(),
    };

    static ArrayList<String> sSupportedExtensions = new ArrayList<String>();
    static HashMap<String, Factory> sExtensionMap =
            new HashMap<String, Factory>();

    static {
        for (Factory f : sSubclassFactories) {
            for (String extension : f.getSupportedExtensions()) {
                sSupportedExtensions.add(extension);
                sExtensionMap.put(extension, f);
            }
        }
    }

    /**
     * Static method to create the appropriate CheapSoundFile subclass
     * given a filename.
     *
     * TODO: make this more modular rather than hardcoding the logic
     */
    public static SoundFile create(Song song,
                                   ProgressListener progressListener)
            throws
            java.io.IOException {

        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(song.id));
        //AssetFileDescriptor file;
        //file = App.getInstance().getContentResolver().openAssetFileDescriptor(uri, "r");

        String name = song.data.toLowerCase();
        Log.d("audioSeekBar", "create: opening file uri = "+name);
        String[] components = name.split("\\.");
        if (components.length < 2) {
            return null;
        }
        Factory factory = sExtensionMap.get(components[components.length - 1]);
        if (factory == null) {
            return null;
        }
        SoundFile soundFile = factory.create();
        soundFile.setProgressListener(progressListener);
        soundFile.readFile(uri);
        return soundFile;
    }

    public static boolean isFilenameSupported(String filename) {
        String[] components = filename.toLowerCase().split("\\.");
        if (components.length < 2) {
            return false;
        }
        return sExtensionMap.containsKey(components[components.length - 1]);
    }

    /**
     * Return the filename extensions that are recognized by one of
     * our subclasses.
     */
    public static String[] getSupportedExtensions() {
        return sSupportedExtensions.toArray(
                new String[0]);
    }

    protected ProgressListener mProgressListener = null;
    protected Uri mInputFile = null;

    protected SoundFile() {}

    public void readFile(Uri inputFile)
            throws
            java.io.IOException {
        mInputFile = inputFile;
    }

    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public int getNumFrames() {
        return 0;
    }

    public int getSamplesPerFrame() {
        return 0;
    }

    public int[] getFrameGains() {
        return null;
    }

    public int getFileSizeBytes() {
        return 0;
    }

    public int getAvgBitrateKbps() {
        return 0;
    }

    public int getSampleRate() {
        return 0;
    }

    public int getChannels() {
        return 0;
    }

    public String getFiletype() {
        return "Unknown";
    }

    /**
     * If and only if this particular file format supports seeking
     * directly into the middle of the file without reading the rest of
     * the header, this returns the byte offset of the given frame,
     * otherwise returns -1.
     */
    public int getSeekableFrameOffset(int frame) {
        return -1;
    }

    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static String bytesToHex (byte[] hash) {
        char[] buf = new char[hash.length * 2];
        for (int i = 0, x = 0; i < hash.length; i++) {
            buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
            buf[x++] = HEX_CHARS[hash[i] & 0xf];
        }
        return new String(buf);
    }

    public void WriteFile(File outputFile, int startFrame, int numFrames)
            throws java.io.IOException {
    }
}