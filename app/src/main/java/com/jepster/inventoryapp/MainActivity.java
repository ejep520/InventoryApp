package com.jepster.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.jepster.inventoryapp.data.InventoryAdapter;
import com.jepster.inventoryapp.data.InventoryContract.InventoryEntry;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    private final int INVENTORY_LOADER = 0;
    private ListView mListView;
    private Locale mLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);
        mLocale = getResources().getConfiguration().getLocales().get(0);

        mListView.setAdapter(new InventoryAdapter(this, null, 0));
        LoaderManager.getInstance(this).initLoader(INVENTORY_LOADER, null, this);

        FloatingActionButton fab = findViewById(R.id.inventory_fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            startActivity(intent);
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_PRICE
        };

        if (id == INVENTORY_LOADER) {
            return new CursorLoader(
                    this,
                    InventoryEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
        }
        throw new IllegalArgumentException("Unrecognized ID argument.");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ((InventoryAdapter) mListView.getAdapter()).changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ((InventoryAdapter) mListView.getAdapter()).changeCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        final int actionDeleteAll = R.id.action_delete_all_things;
        if (menuItem.getItemId() == actionDeleteAll) {
            int deletedRows = getContentResolver()
                    .delete(
                            InventoryEntry.CONTENT_URI,
                            null,
                            null
                    );
            Toast.makeText(
                    this,
                    String.format(
                            mLocale,
                            "%d row(s) deleted.",
                            deletedRows),
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoaderManager.getInstance(this).destroyLoader(INVENTORY_LOADER);
    }
}
