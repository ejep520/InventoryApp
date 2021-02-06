package com.jepster.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;

import com.jepster.inventoryapp.EditorActivity;
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
    public void bindView(@NonNull View view, @NonNull Context context, @NonNull Cursor cursor) {
        TextView itemName = view.findViewById(R.id.inventory_item_name);
        TextView itemQuantity = view.findViewById(R.id.inventory_item_quantity);
        TextView itemPrice = view.findViewById(R.id.inventory_item_price);
        Button itemSaleButton = view.findViewById(R.id.inventory_item_sale);

        int quantityInt = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
        int idInt = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));

        String itemNameString = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_NAME));
        String itemQuantString = String.valueOf(quantityInt);
        String itemPriceString = NumberFormat
                .getCurrencyInstance(context
                        .getResources()
                        .getConfiguration()
                        .getLocales()
                        .get(0))
                .format(cursor
                        .getDouble(cursor
                                .getColumnIndex(InventoryEntry.COLUMN_PRICE)));
        String itemIdString = Integer
                .valueOf(idInt)
                .toString();

        itemName.setText(itemNameString);
        itemQuantity.setText(itemQuantString);
        itemPrice.setText(itemPriceString);
        itemSaleButton.setOnClickListener((v) -> {
            int saleQuantity = quantityInt;
            if (saleQuantity < 1) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_QUANTITY, --saleQuantity);
            int updatedRows = context.getContentResolver()
                    .update(ContentUris
                                    .withAppendedId(InventoryEntry
                                                    .CONTENT_URI,
                                            Long
                                                    .parseLong(itemIdString)),
                            values,
                            null,
                            null);
            Toast.makeText(context, String
                            .format(context.getResources()
                                    .getConfiguration()
                                    .getLocales()
                                    .get(0), "%d row(s) updated.", updatedRows),
                    Toast.LENGTH_SHORT).show();
        });
        view.setOnClickListener((view1) -> {
            Log.d(context.getClass().getSimpleName(), "I'm not quite dead.");
            Intent intent = new Intent(context, EditorActivity.class);
            Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, idInt);
            intent.setData(uri);
            context.startActivity(intent);
        });

    }
}
