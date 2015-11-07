package com.theoddler.audiobookreader;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
    private Book book;
    private Book.Progress progress;

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
            startReading();
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



    public void setBook(Book book) {
        this.book = book;
        this.progress = book.getStartProgress();
    }

    public void startReading() {
        player.reset();

        Uri uri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                progress.getFile().getId());

        try {
            player.setDataSource(getApplicationContext(), uri);
        }
        catch (IOException e) {
            Log.e("READER SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    /**
     * The binder for this reader.
     */
    public class Binder extends android.os.Binder {
        ReaderService getService() {
            return ReaderService.this;
        }
    }
}
