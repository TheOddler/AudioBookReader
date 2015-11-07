package com.theoddler.audiobookreader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 05/11/15.
 */
public class Book {

    private final String title;

    private final List<BookFile> files;

    public Book(String title, List<BookFile> files) {
        this.title = title;
        this.files = files; //use new List... here?
    }

    public String getTitle() {
        return title;
    }

    /**
     * Get the duration of this book
     * @return the duration in milliseconds
     */
    public long getDuration() {
        long dur = 0;
        for(BookFile file : files) {
            dur += file.getDuration();
        }
        return dur;
    }

    public int getRoughDurationInSeconds() {
        return (int)(getDuration() / 1000);
    }

    // TODO
    public int getRoughProgressInSeconds() {
        return getRoughDurationInSeconds() / 3;
    }

    public Progress getStartProgress() {
        return new Progress(0, 0);
    }


    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", files=" + files +
                '}';
    }



    static Book tryParseToBook(File dir, Context context) {
        if (!dir.isDirectory()) return null;

        String path = dir.getPath();
        if (!path.endsWith("/")) {
            path += "/";
        }

        Cursor audioCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ?", //DATA for file path
                new String[]{path + "%", path + "%/%"},
                MediaStore.Audio.Media.DISPLAY_NAME + " ASC"); //DISPLAY_NAME for file title

        if (audioCursor != null && audioCursor.moveToFirst()) {
            //get columns
            int idCol = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int fileNameCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationCol = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            List<BookFile> files = new ArrayList<>();
            do {
                long id = audioCursor.getLong(idCol);
                String fileName = audioCursor.getString(fileNameCol);
                long duration = audioCursor.getLong(durationCol);
                files.add(new BookFile(id, fileName, duration));
            }
            while (audioCursor.moveToNext());

            return new Book(dir.getName(), files);
        }
        else return null;
    }

    public class Progress {
        private int fileNumber;
        private long fileProgress;

        protected Progress(int fileNumber, long fileProgress) {
            this.fileNumber = fileNumber % files.size();
            this.fileProgress = fileProgress;
        }

        public BookFile getFile() {
            return files.get(fileNumber);
        }

        public long getFileProgress() {
            return fileProgress;
        }

        public long getTotalProgress() {
            long totalProgress = fileProgress;
            for (int i = 0; i < fileNumber; ++i) {
                totalProgress += files.get(i).getDuration();
            }
            return totalProgress;
        }

        /**
         * Start the next file of the book.
         * This will loop around the files if called when in the last file.
         * @return Whether or not there was a next file
         */
        public boolean startNextFile() {
            fileNumber = (fileNumber + 1) % files.size();
            fileProgress = 0;
            return fileNumber > 0; //If it's 0 we looped, so there was no next file.
        }
    }

}
