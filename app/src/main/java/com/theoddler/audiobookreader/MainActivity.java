package com.theoddler.audiobookreader;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final String PREF_ROOT = "audio_books_root";
    private final int REQUEST_ROOT = 31415;

    private final Library library = new Library();

    private ReaderService readerService;
    private Intent readerBindingIntent;
    private boolean readerBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ListView booksView = (ListView) findViewById(R.id.books_list);
        booksView.setAdapter(new BookAdapter(library, this));

        getOrAskForRoot();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (readerBindingIntent == null) {
            readerBindingIntent = new Intent(this, ReaderService.class);
            bindService(readerBindingIntent, readerConnection, Context.BIND_AUTO_CREATE);
            startService(readerBindingIntent);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(readerConnection);
        stopService(readerBindingIntent);
        readerService = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_select_root:
                startRootSelection();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getOrAskForRoot() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String root = prefs.getString(PREF_ROOT, null);

        if (root == null) {
            // No root set yet
            // Show an alert about this
            // When the user click "ok" start the root selection
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("No books found :(");
            alert.setMessage("Please select the folder with all your audio-books.");
            alert.setNeutralButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            // Start the root selection...
                            startRootSelection();
                        }
                    });
            alert.create().show();
        }
        else {
            onRootFound(root);
        }
    }

    private void startRootSelection() {
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);
        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("Audio Books")
                .allowReadOnlyDirectory(false)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

        startActivityForResult(chooserIntent, REQUEST_ROOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ROOT) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                String chosenDir = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                onRootSelected(chosenDir);
            }
            else {
                // Nothing selected
                // TODO
            }
        }
    }

    private void onRootSelected(String root) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_ROOT, root);
        editor.commit();

        onRootFound(root);
    }

    private void onRootFound(String rootPath) {
        // also called when re-selecting root
        Log.println(Log.DEBUG, "MAIN", "Found root: " + rootPath);

        library.removeAllBook();
        library.findAllBooksIn(new File(rootPath), this);
    }

    public void onBookPicked(View view) {
        readerService.setBook((Book)view.getTag());
        readerService.startReading();
    }



    private ServiceConnection readerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReaderService.Binder binder = (ReaderService.Binder)service;
            readerService = binder.getService();
            readerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            readerBound = false;
        }
    };
}
