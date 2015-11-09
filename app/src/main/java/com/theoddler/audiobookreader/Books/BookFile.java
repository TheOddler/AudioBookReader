package com.theoddler.audiobookreader.Books;

/**
 * Created by Pablo on 07/11/15.
 */
public class BookFile {

    private long id;

    private String name;
    private long duration;

    public BookFile(long id, String name, long duration) {
        this.id = id;

        this.name = name;
        this.duration = duration;
    }

    long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }
}
