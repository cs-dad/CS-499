package com.example.warehouseapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.warehouseapp.Types.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryHelper extends SQLiteOpenHelper {

    // database name
    private static final String databaseName = "warehouse_inventory.db";
    private static final int databaseVersion = 2;

    public static final String table = "inventory"; // table
    public static final String skuColumn = "sku"; // sku col
    public static final String descColumn = "description"; // desc col
    public static final String quantColumn = "quantity"; // quant col

    public InventoryHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create inventory table if not exists
        String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT, %s INTEGER)", table, skuColumn, descColumn, quantColumn);

        db.execSQL(query);

        // had some issues with this table not wanting to be creating, so add logging
        android.util.Log.d("DB", "onCreate called: " + query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);
    }

    /**
     * Method to insert or update item.
     * @param item The item to insert or update
     * @return boolean indicating s/f
     */
    public boolean insertOrUpdateItem(InventoryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        // serialize values and package into a CV object
        ContentValues values = new ContentValues();
        values.put(skuColumn, item.getSku());
        values.put(descColumn, item.getDesc());
        values.put(quantColumn, item.getQuantity());

        // execute query, storing result
        // the enum tells the inserting method to replace any item that already exists based on the primary key (sku)
        long result = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // close the conn
        db.close();

        return result != -1;

    }

    /**
     * Method to delete an item, given its sku key
     * @param sku The key of the item in the database
     * @return boolean indicating s/f
     */
    public boolean deleteItem(String sku) {
        SQLiteDatabase db = this.getWritableDatabase();

        // all error codes for this method are < 1
        return db.delete(table, skuColumn + "=?", new String[]{sku}) > 0;
    }

    /**
     * Method to get all items in the inventory table
     * @return A list of all items found
     */
    public List<InventoryItem> getAllItems() {

        // establish a new list to populate
        List<InventoryItem> items = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // dump the entire table
        Cursor cursor = db.query(table, null, null, null, null, null, null);

        // if there's values
        if(cursor.moveToFirst()) {

            // loop over the entire list of values retrieved from db
            do {

                // deserialize and create a new InventoryItem object
                String sku = cursor.getString(cursor.getColumnIndexOrThrow(skuColumn)); // throws err if there is no col index
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(descColumn));
                int quant = cursor.getInt(cursor.getColumnIndexOrThrow(quantColumn));
                InventoryItem item = new InventoryItem(sku, desc, quant);

                // add the item to return list
                items.add(item);
            } while(cursor.moveToNext());

        }

        // close the cursor
        cursor.close();

        // return the list
        return items;


    }



}
