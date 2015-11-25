package com.theoddler.audiobookreader.books;

import android.media.MediaMetadataRetriever;

import java.io.File;

/**
 * Created by Pablo on 07/11/15.
 */
public class BookFile {

    private File file;

    private String name;
    private long duration;

    public BookFile(File file, MediaMetadataRetriever mmr) throws IllegalArgumentException {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Couldn't create BookFile: 'file' isn't a file.");
        }

        this.file = file;

        this.name = file.getName();

        try {
            mmr.setDataSource(file.getAbsolutePath());
            this.duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Couldn't create BookFile: Couldn't retrieve metadata.");
        }
    }

    public String getPath() {
        return file.getAbsolutePath();
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }
}
