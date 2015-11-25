package com.theoddler.audiobookreader.books;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Pablo on 07/11/15.
 */
public class ReaderService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private Progress progress;

    private final IBinder binder = new Binder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Nullable
    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();

        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(progress.startNextFile()) {
            read();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }



    public void readFromStart(Book book) {
        this.progress = new Progress(book, 0, 0);
        read();
    }

    public void readStartingAt(long time, Book book) {
        Progress prog = null;
        for (int i = 0; i < book.getFileCount(); ++i) {
            long fileDur = book.getFile(i).getDuration();
            if (time >= fileDur) {
                time -= fileDur;
            }
            else {
                prog = new Progress(book, i, time);
            }
        }

        if (prog == null) {
            Log.println(Log.INFO, "BOOK PROGRESS", "Trying to create `progress` for a book longer than the book itself.");
            prog = new Progress(book, 0, 0);
        }

        this.progress = prog;
        read();
    }

    public void readStartingAtBookmark(String bookmark, Book book) {
        // TODO
    }

    private void read() {
        player.reset();

        try {
            player.setDataSource(progress.getFile().getPath());
            player.prepareAsync();
        }
        catch (IOException e) {
            Log.e("READER SERVICE", "Error setting data source", e);
            // TODO
        }
    }

    /**
     * The binder for this reader.
     */
    public class Binder extends android.os.Binder {
        public ReaderService getService() {
            return ReaderService.this;
        }
    }

    /**
     * Progress in a book
     */
    public class Progress {
        private Book book;
        private int fileNumber;
        private long fileProgress;

        protected Progress(Book book, int fileNumber, long fileProgress) {
            this.book = book;
            this.fileNumber = fileNumber % book.getFileCount();
            this.fileProgress = fileProgress;
        }

        public BookFile getFile() {
            return book.getFile(fileNumber);
        }

        public long getFileProgress() {
            return fileProgress;
        }

        public long getTotalProgress() {
            long totalProgress = fileProgress;
            for (int i = 0; i < fileNumber; ++i) {
                totalProgress += book.getFile(i).getDuration();
            }
            return totalProgress;
        }

        /**
         * Start the next file of the book.
         * This will loop around the files if called when in the last file.
         * @return Whether or not there was a next file
         */
        public boolean startNextFile() {
            fileNumber = (fileNumber + 1) % book.getFileCount();
            fileProgress = 0;
            return fileNumber > 0; //If it's 0 we looped, so there was no next file.
        }
    }
}
