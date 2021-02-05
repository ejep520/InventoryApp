package com.jepster.inventoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.jepster.inventoryapp.data.InventoryContract.InventoryEntry;

import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText, mPriceEditText, mQuantityEditText;
    private Button mQuantityDecButton, mQuantityIncButton;
    private Uri mPassedUri;
    private boolean mItemHasChanged = false;
    private Locale mLocale;

    private final int ITEM_GETTER = 0;

    private final View.OnTouchListener mOnTouchListener = (v, event) -> {
        v.performClick();
        mItemHasChanged = true;
        return false;
    };

    private final View.OnKeyListener mOnKeyListener = (v, code, event) -> {
        mItemHasChanged = true;
        return false;
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        mLocale = getResources().getConfiguration().getLocales().get(0);
        mNameEditText = findViewById(R.id.editor_name_text_edit);
        mPriceEditText = findViewById(R.id.editor_price_text_edit);
        mQuantityEditText = findViewById(R.id.editor_quantity_text_editor);
        mQuantityDecButton = findViewById(R.id.editor_quantity_dec_button);
        mQuantityIncButton = findViewById(R.id.editor_quantity_inc_button);
        mPassedUri = getIntent().getData();
        if (mPassedUri == null) {
            setTitle(R.string.add_item_title);
        } else {
            setTitle(R.string.edit_item_title);
        }
        mNameEditText.setOnTouchListener(mOnTouchListener);
        mNameEditText.setOnKeyListener(mOnKeyListener);
        mPriceEditText.setOnTouchListener(mOnTouchListener);
        mPriceEditText.setOnKeyListener(mOnKeyListener);
        mQuantityEditText.setOnTouchListener(mOnTouchListener);
        mQuantityEditText.setOnKeyListener(mOnKeyListener);
        mQuantityIncButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            mQuantityEditText.setText(String.format(mLocale, "%d", ++quantity));
            mItemHasChanged = true;
        });
        mQuantityDecButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            if (quantity < 1){
                return;
            }
            mQuantityEditText.setText(String.format(mLocale, "%d", --quantity));
            mItemHasChanged = true;
        });
        LoaderManager.getInstance(this).initLoader(ITEM_GETTER, null, this);
        LoaderManager.enableDebugLogging(true);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == ITEM_GETTER) {
            String[] projection = {
                    InventoryEntry._ID,
                    InventoryEntry.COLUMN_NAME,
                    InventoryEntry.COLUMN_QUANTITY,
                    InventoryEntry.COLUMN_PRICE
            };
            return new CursorLoader(this,
                    mPassedUri,
                    projection,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @SuppressLint("SetTextIl8n")
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if ((data == null) || (data.getCount() < 1) || (!data.moveToFirst())) { return; }
        final int nameColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_NAME);
        final int quantityColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        final int priceColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRICE);

        // TODO Continue from here.
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoaderManager.getInstance(this).destroyLoader(ITEM_GETTER);
    }
}