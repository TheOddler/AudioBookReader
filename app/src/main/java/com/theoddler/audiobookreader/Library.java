package com.theoddler.audiobookreader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 07/11/15.
 */
public class Library {

    private final List<Book> books = new ArrayList<>();

    public Library() { }

    public boolean addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
            return true;
        }
        else return false;
    }

    public Book getBook(int location) {
        return books.get(location);
    }

    public int bookCount() {
        return books.size();
    }

    public void removeAllBook() {
        books.clear();
    }

}
