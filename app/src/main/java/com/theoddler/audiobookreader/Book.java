package com.theoddler.audiobookreader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 05/11/15.
 */
public class Book {

    private final String name;

    private final List<Long> fileIds;

    public Book(String name, List<Long> fileIds) {
        this.name = name;
        this.fileIds = fileIds; //use new List... here?
    }

    @Override
    public String toString() {
        return "[ (Book) Title: " + name + ", #Chapters: " + fileIds.size() + "]";
    }



    static Book parseToBook(File dir, Context context) {
        if (!dir.isDirectory()) return null;

        ContentResolver cr = context.getContentResolver();
        Uri audioUri = MediaStore.Audio.Media.getContentUriForPath(dir.getPath());
        //Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor audioCursor = cr.query(audioUri, null, null, null, null);

        if (audioCursor != null && audioCursor.moveToFirst()) {
            //get columns
            int idColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            //int titleColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            //int artistColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            List<Long> fileIds = new ArrayList<>();
            do {
                long id = audioCursor.getLong(idColumn);
                //String thisTitle = audioCursor.getString(titleColumn);
                //String thisArtist = audioCursor.getString(artistColumn);
                fileIds.add(id);
            }
            while (audioCursor.moveToNext());

            return new Book(dir.getName(), fileIds);
        }
        else return null;
    }

}
