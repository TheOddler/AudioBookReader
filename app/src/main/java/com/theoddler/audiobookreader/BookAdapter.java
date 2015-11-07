package com.theoddler.audiobookreader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pablo on 07/11/15.
 */
public class BookAdapter extends BaseAdapter {

    private final Library library;
    private final Context context;

    public BookAdapter(Library library, Context context) {
        this.library = library;
        this.context = context;
    }

    @Override
    public int getCount() {
        return library.bookCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the list layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book, parent, false);
        }

        // Get the book info views
        ImageView imageView = (ImageView)convertView.findViewById(R.id.book_cover);
        TextView titleView = (TextView)convertView.findViewById(R.id.book_title);
        ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.book_progress);

        // Get book using the position
        Book book = library.getBook(position);
        // Set the info
        imageView.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play));
        titleView.setText(book.getTitle());
        progressBar.setMax(book.getRoughDurationInSeconds());
        progressBar.setProgress(book.getRoughProgressInSeconds());

        // Set book as tag
        convertView.setTag(book);

        return convertView;
    }
}
