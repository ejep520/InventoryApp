package com.jepster.inventoryapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.jepster.inventoryapp.data.InventoryAdapter;
import com.jepster.inventoryapp.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    private final int INVENTORY_LOADER = 0;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(new InventoryAdapter(this, null, 0));
        LoaderManager.getInstance(this).initLoader(INVENTORY_LOADER, null, this);
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
        ((InventoryAdapter)mListView.getAdapter()).changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ((InventoryAdapter)mListView.getAdapter()).changeCursor(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoaderManager.getInstance(this).destroyLoader(INVENTORY_LOADER);
    }
}