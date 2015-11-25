package com.theoddler.audiobookreader.books;

import android.util.Log;

import java.io.File;
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

    public void findAllBooksIn(File rootDir) {
        if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException("RootPath doesn't point to a directory.");
        }

        // TODO
        // Allow for books in sub folders
        // for simplicity first implement getting book directly in the root
        File[] possibleBooks = rootDir.listFiles();

        for (File dir : possibleBooks) {
            Book book = Book.tryParseToBook(dir);
            if (book != null) {
                addBook(book);
                Log.println(Log.INFO, "LIBRARY", "Found book: " + book.toString());
            }
            else {
                Log.println(Log.INFO, "LIBRARY", "No book found in: " + dir.getPath());
            }
        }
    }

}
