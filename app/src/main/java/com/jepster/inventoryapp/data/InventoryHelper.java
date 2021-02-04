package com.jepster.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.jepster.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryHelper extends SQLiteOpenHelper {

    private final static String LOG_TAG = InventoryHelper.class.getSimpleName();
    private final static String DATABASE_NAME = "warehouse.db";
    private final static int DATABASE_VERSION = 1;

    public InventoryHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        final String CREATE_INVENTORY_DB = "CREATE TABLE IF NOT EXISTS " +
                InventoryEntry.TABLE_NAME + " (" + InventoryEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + InventoryEntry.COLUMN_NAME +
                " TEXT NOT NULL, " + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                InventoryEntry.COLUMN_PRICE + " REAL DEFAULT 0.0);";
        db.execSQL(CREATE_INVENTORY_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if ((oldVersion == newVersion) && (oldVersion == 1)) {
            final String DROP_INVENTORY_DB = "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME +
                    ";";
            db.execSQL(DROP_INVENTORY_DB);
            onCreate(db);
        }
    }
}
