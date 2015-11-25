package com.theoddler.audiobookreader.books;

import android.media.MediaMetadataRetriever;

import com.theoddler.audiobookreader.utils.FileFilters;

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



    static Book tryParseToBook(File dir) {
        if (!dir.isDirectory()) return null;

        try {
            File[] files = dir.listFiles(FileFilters.AUDIO);
            if (files.length > 0) {
                List<BookFile> bookFiles = new ArrayList<>(files.length);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                for (File file : files) {
                    bookFiles.add(new BookFile(file, mmr));
                }
                return new Book(dir.getName(), bookFiles);
            }
            else return null;
        }
        catch (Exception e) {
            return null;
        }
    }

}
