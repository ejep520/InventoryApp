package com.jepster.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static class InventoryEntry implements BaseColumns {

        private InventoryEntry() {}

        public final static String TABLE_NAME = "inventory";
        /**
         * Type: INTEGER
         * Constraints: Primary Key, Non-Null, Autoincrement
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Type: TEXT
         * Constraints: Non-Null
         */
        public final static String COLUMN_NAME = "name";
        /**
         * Type INTEGER
         * Constraints: Non-Null
         */
        public final static String COLUMN_QUANTITY = "quantity";
        /**
         * Type: REAL
         * Constraints: Default 0.0
         */
        public final static String COLUMN_PRICE = "price";

        public final static String CONTENT_AUTHORITY = "com.jepster.inventory_app";
        public static final Uri BASE_CONTENT_PATH = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_INVENTORY = TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_PATH,
                PATH_INVENTORY);

        public final static String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public final static String CONTENT_ITEM_TYPE =  ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

    }
}
