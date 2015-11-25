package com.theoddler.audiobookreader.utils;

import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pablo on 25/11/15.
 */
public final class FileFilters {

    private static final Set<String> AUDIO_EXTENSIONS;
    public static final FileFilter AUDIO = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.isFile()) {
                String fileName = file.getName();
                int extensionIndex = fileName.lastIndexOf('.');
                if (extensionIndex <= 0) return false;
                String extension = fileName.substring(extensionIndex).toLowerCase();
                return AUDIO_EXTENSIONS.contains(extension);
            }
            else return false;
        }
    };

    static {
        AUDIO_EXTENSIONS = new HashSet<>(16);

        AUDIO_EXTENSIONS.add(".3gp");
        AUDIO_EXTENSIONS.add(".mp4");
        AUDIO_EXTENSIONS.add(".m4a");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) { // 3.1
            AUDIO_EXTENSIONS.add(".aac");
            AUDIO_EXTENSIONS.add(".flac");
        }

        AUDIO_EXTENSIONS.add(".mp3");

        AUDIO_EXTENSIONS.add(".mid");
        AUDIO_EXTENSIONS.add(".xmf");
        AUDIO_EXTENSIONS.add(".mxmf");
        AUDIO_EXTENSIONS.add(".rtttl");
        AUDIO_EXTENSIONS.add(".rtx");
        AUDIO_EXTENSIONS.add(".ota");
        AUDIO_EXTENSIONS.add(".imy");

        AUDIO_EXTENSIONS.add(".ogg");

        AUDIO_EXTENSIONS.add(".wav");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AUDIO_EXTENSIONS.add(".mkv");
        }
    }

}
