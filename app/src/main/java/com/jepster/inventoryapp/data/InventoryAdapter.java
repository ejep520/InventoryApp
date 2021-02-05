package com.jepster.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.jepster.inventoryapp.R;

import java.text.NumberFormat;

import static com.jepster.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryAdapter extends CursorAdapter {
    public InventoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags | CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent,
                false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView itemName = view.findViewById(R.id.inventory_item_name);
        TextView itemQuantity = view.findViewById(R.id.inventory_item_quantity);
        TextView itemPrice = view.findViewById(R.id.inventory_item_price);


        String itemNameString = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_NAME));
        String itemQuantString = String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY)));
        String itemPriceString = NumberFormat
                .getCurrencyInstance(context
                        .getResources()
                        .getConfiguration()
                        .getLocales()
                        .get(0))
                .format(cursor
                        .getDouble(cursor
                                .getColumnIndex(InventoryEntry.COLUMN_PRICE)));

        itemName.setText(itemNameString);
        itemQuantity.setText(itemQuantString);
        itemPrice.setText(itemPriceString);
    }
}
