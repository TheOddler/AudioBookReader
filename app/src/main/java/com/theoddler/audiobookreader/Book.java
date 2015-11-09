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

    // TODO, put reader, library and book in their own package
    int getFileCount() {
        return files.size();
    }
    BookFile getFile(int index) {
        return files.get(index);
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

}
