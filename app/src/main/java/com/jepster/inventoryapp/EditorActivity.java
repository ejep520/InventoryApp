package com.jepster.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import static com.jepster.inventoryapp.data.InventoryContract.InventoryEntry;

import java.util.Locale;

@SuppressLint("SetTextI18n")
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText, mPriceEditText, mQuantityEditText;
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
        Button quantityDecButton, quantityIncButton;
        mLocale = getResources().getConfiguration().getLocales().get(0);
        mNameEditText = findViewById(R.id.editor_name_text_edit);
        mPriceEditText = findViewById(R.id.editor_price_text_edit);
        mQuantityEditText = findViewById(R.id.editor_quantity_text_editor);
        quantityDecButton = findViewById(R.id.editor_quantity_dec_button);
        quantityIncButton = findViewById(R.id.editor_quantity_inc_button);
        mPassedUri = getIntent().getData();
        if (mPassedUri == null) {
            setTitle(R.string.add_item_title);
        } else {
            setTitle(R.string.edit_item_title);
            LoaderManager.getInstance(this).initLoader(ITEM_GETTER, null, this);
        }
        mNameEditText.setOnTouchListener(mOnTouchListener);
        mNameEditText.setOnKeyListener(mOnKeyListener);
        mPriceEditText.setOnTouchListener(mOnTouchListener);
        mPriceEditText.setOnKeyListener(mOnKeyListener);
        mQuantityEditText.setOnTouchListener(mOnTouchListener);
        mQuantityEditText.setOnKeyListener(mOnKeyListener);
        quantityIncButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            mQuantityEditText.setText(String.format(mLocale, "%d", ++quantity));
            mItemHasChanged = true;
        });
        quantityDecButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            if (quantity < 1){
                return;
            }
            mQuantityEditText.setText(String.format(mLocale, "%d", --quantity));
            mItemHasChanged = true;
        });
        LoaderManager.enableDebugLogging(true);
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
        return new CursorLoader(this,
                mPassedUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if ((data == null) || (data.getCount() < 1) || (!data.moveToFirst())) { return; }
        final int nameColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_NAME);
        final int quantityColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        final int priceColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRICE);

        mNameEditText.setText(data.getString(nameColumnIndex));
        mQuantityEditText.setText(Integer.toString(data.getInt(quantityColumnIndex)));
        mPriceEditText.setText(Double.toString(data.getDouble(priceColumnIndex)));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText(Integer.valueOf(0).toString());
        mPriceEditText.setText(Float.valueOf(0.0F).toString());
    }

    private void showUnsavedDialog(DialogInterface.OnClickListener l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard, l);
        builder.setNegativeButton(R.string.keep_editing, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void insertThing() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, mNameEditText.getText().toString().trim());
        values.put(InventoryEntry.COLUMN_QUANTITY, Integer.parseInt(mQuantityEditText.getText()
                .toString().trim()));
        values.put(InventoryEntry.COLUMN_PRICE, Double.parseDouble(mPriceEditText.getText()
                .toString().trim()));
        Uri addedUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        long addedRow = ContentUris.parseId(addedUri);
        if (addedRow < 1) {
            Toast.makeText(this, "There was a problem inserting the new item.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, String.format(mLocale, "Item added at row %d",
                    addedRow), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateThing() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, mNameEditText.getText().toString().trim());
        values.put(InventoryEntry.COLUMN_QUANTITY, Integer.parseInt(mQuantityEditText.getText()
                .toString().trim()));
        values.put(InventoryEntry.COLUMN_PRICE, Double.parseDouble(mPriceEditText.getText()
                .toString().trim()));
        int updatedRows = getContentResolver().update(mPassedUri, values, null, null);
        if (updatedRows < 1) {
            Toast.makeText(this, "There was a problem updating the data.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, String.format(mLocale, "Updated %d row(s)",
                    updatedRows), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteThing() {
        if (ContentUris.parseId(mPassedUri) < 1) {
            return;
        }
        int deletedThings = getContentResolver().delete(mPassedUri, null, null);
        Toast.makeText(this, String.format(mLocale, "%d things deleted.", deletedThings),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int actionSave = R.id.action_save;
        final int actionDelete = R.id.action_delete;
        switch (item.getItemId()) {
            case actionSave:
                if (mPassedUri == null) {
                    insertThing();
                } else {
                    updateThing();
                }
                finish();
                return true;
            case actionDelete:
                deleteThing();
                return true;
            case android.R.id.home:
                if (mItemHasChanged) {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            (dialogInterface, i) -> NavUtils
                                    .navigateUpFromSameTask(EditorActivity.this);
                    showUnsavedDialog(discardButtonClickListener);
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoaderManager.getInstance(this).destroyLoader(ITEM_GETTER);
    }
}