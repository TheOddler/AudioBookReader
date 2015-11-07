package com.theoddler.audiobookreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
    private ListView booksView;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        booksView = (ListView)findViewById(R.id.books_list);
        booksView.setAdapter(new BookAdapter(library, this));

        getOrAskForRoot();
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
            onRootSet(root);
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
                onSelectedRoot(chosenDir);
            }
            else {
                // Nothing selected
                // TODO
            }
        }
    }

    private void onSelectedRoot(String root) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_ROOT, root);
        editor.commit();

        onRootSet(root);
    }

    private void onRootSet(String rootPath) {
        // also called when re-selecting root
        Log.println(Log.DEBUG, "MAIN", "Found root: " + rootPath);

        File root = new File(rootPath);

        if (!root.isDirectory()) {
            throw new IllegalArgumentException("RootPath doesn't point to a directory.");
        }

        library.removeAllBook();
        // TODO
        // Allow for books in sub folders
        // for simplicity first implement getting book directly in the root
        File[] possibleBooks = root.listFiles();

        for (File dir : possibleBooks) {
            Log.println(Log.INFO, "BOOKS", "Looking for book in " + dir.getPath());
            Book book = Book.tryParseToBook(dir, this);
            if (book != null) {
                library.addBook(book);
                Log.println(Log.INFO, "BOOKS", "Found book: " + book.toString());
            }
        }
    }
}
