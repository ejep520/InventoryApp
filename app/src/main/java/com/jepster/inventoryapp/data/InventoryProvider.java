package com.jepster.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.jepster.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {

    // private final static String LOG_TAG = InventoryProvider.class.getSimpleName();
    private InventoryHelper mDbHelper;
    private final static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final static  int THINGS = 100;
    private final static int THING_ID = 101;

    static {
        matcher.addURI(InventoryEntry.CONTENT_AUTHORITY, InventoryEntry.PATH_INVENTORY, THINGS);
        matcher.addURI(InventoryEntry.CONTENT_AUTHORITY, InventoryEntry.PATH_INVENTORY + "/#",
                THING_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (matcher.match(uri)) {
            case THINGS:
                cursor = db
                        .query(
                                InventoryEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case THING_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db
                        .query(
                                InventoryEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)) {
            case THINGS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case THING_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return insertThing(uri, values);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (matcher.match(uri)) {
            case THINGS:
                return deleteThings(uri, selection, selectionArgs);
            case THING_ID:
                return deleteThingId(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unable to parse URI " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch(matcher.match(uri)) {
            case THINGS:
                return updateThings(values, selection, selectionArgs);
            case THING_ID:
                return updateThingId(uri, values);
            default:
                throw new IllegalArgumentException("Unable to parse URI " + uri);
        }
    }

    private Uri insertThing(Uri uri, ContentValues values) {
        if (matcher.match(uri) != THINGS) {
            return ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, -1);
        }
        if ((values == null) || (values.keySet().isEmpty())) {
            return ContentUris.withAppendedId(uri, -1);
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long addedRow = db.insert(InventoryEntry.TABLE_NAME, null, values);
        db.close();
        return ContentUris.withAppendedId(uri, addedRow);
    }

    private int deleteThings(Uri uri, String selection, String[] selectionArgs) {
        if ((selection == null) ^ (selectionArgs == null)) {
            throw new IllegalArgumentException(
                    "selection and selectionArgs must both be filled or both be null with " +
                            "this URI: " + uri);
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnValue = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return returnValue;
    }

    private int deleteThingId(Uri uri, String selection, String[] selectionArgs) {
        if ((selection != null) || (selectionArgs != null)) {
            throw new IllegalArgumentException("selection and selectionArgs must both be null with this URI " + uri);
        }
        selection = InventoryEntry._ID + "=?";
        selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnValue = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return returnValue;
    }

    private int updateThings(ContentValues values, String selection, String[] selectionArgs) {
        if ((values == null) || (selection == null) || (selectionArgs == null)) {
            throw new IllegalArgumentException("If updating all records, all other fields must be included.");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnValue = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return returnValue;
    }

    private int updateThingId(Uri uri, ContentValues values) {
        if (values == null) { throw new IllegalArgumentException("values may not be null."); }
        if (values.keySet().contains(InventoryEntry._ID)) {
            values.remove(InventoryEntry._ID);
        }
        String selection = InventoryEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(ContentUris.parseId(uri)) };
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnValue = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return returnValue;
    }

    @Override
    public void shutdown() {
        mDbHelper.close();
    }
}
