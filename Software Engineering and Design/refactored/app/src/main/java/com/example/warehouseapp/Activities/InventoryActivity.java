package com.example.warehouseapp.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouseapp.Activities.Adapters.InventoryAdapter;
import com.example.warehouseapp.Database.InventoryHelper;
import com.example.warehouseapp.R;
import com.example.warehouseapp.Types.InventoryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    // necessary private vars
    private RecyclerView rvInventory;
    private FloatingActionButton btnAddItem;
    private InventoryHelper dbHelper;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide the action bar
        if(getSupportActionBar() != null) { getSupportActionBar().hide(); }
        // set the view
        setContentView(R.layout.activity_inventory);

        // get the necessary elements from the view
        rvInventory = findViewById(R.id.rvInventory);
        btnAddItem = findViewById(R.id.btnAddItem);

        // get the search view
        searchView = findViewById(R.id.searchView);
        bindSearchView(searchView); // bind the search view to our adapters filter method

        // create our db helper and get all items from the db
        dbHelper = new InventoryHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // if this isn't the first run, force execute as android will not recall oncreate
        // once the db exists unless you upgrade versions
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory (sku TEXT PRIMARY KEY, description TEXT, quantity INTEGER)");

        inventoryList = dbHelper.getAllItems();

        // create our adapter
        adapter = new InventoryAdapter(this, inventoryList, dbHelper);
        rvInventory.setLayoutManager(new GridLayoutManager(this, 1)); // set the manager for the rowview
        rvInventory.setAdapter(adapter); // set the adapter

        // bind an event to the add item button
        btnAddItem.setOnClickListener(v -> {

            // wrap adapter.addItem() inside of post to only call after RV finishes layout pass
            rvInventory.post(() -> {
                // Create a dummy item to populate the view with, the user will edit it
                InventoryItem newItem = new InventoryItem("SKU" + System.currentTimeMillis(), "New Item", 0);
                adapter.addItem(newItem);
            });
        });
    }

    /**
     * Method to bind a search view to our filter method in InventoryAdapter.java
     * @param sv The searchview to bind
     */
    private void bindSearchView(SearchView sv) {

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }

        });

    }

}
