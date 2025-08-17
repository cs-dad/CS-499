package com.example.warehouseapp.Activities.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouseapp.Database.InventoryHelper;
import com.example.warehouseapp.R;
import com.example.warehouseapp.Types.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {

    // necessary local variables
    private List<InventoryItem> itemList;
    private InventoryHelper dbHelper;
    private Context context;

    private List<InventoryItem> fullItemList; // store a full list for search functionality
    public InventoryAdapter(Context context, List<InventoryItem> items, InventoryHelper dbHelper) {
        this.context = context;
        this.itemList = new ArrayList<>(items);
        this.fullItemList = new ArrayList<>(items); // as we're using the same list, use a deep copy to prevent reference problems
        this.dbHelper = dbHelper;
    }

    /**
     * Method to filter the displayted inventory list based on a query
     * @param query The search query
     */
    public void filter(String query) {

        // clear the list of visible items
        itemList.clear();

        // if the query is empty, restore the full list
        if (query.isEmpty()) {
            itemList.addAll(fullItemList);
        } else {

            // force to lowercase for comparison
            String lcQuery = query.toLowerCase();

            // iterate over all items
            for(InventoryItem item : fullItemList) {

                // check if the sku or desc matches search query
                if(item.getSku().toLowerCase().contains(lcQuery) || item.getDesc().toLowerCase().contains(lcQuery)) {
                    // add to the visible list
                    itemList.add(item);
                }
            }

        }

        // signal to the adapter that the data set changed
        notifyDataSetChanged();

    }

    /**
     * Called when the RV needs a new VH of the type
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return new view holder
     */
    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_inventory_item, parent, false);
        return new InventoryViewHolder(view);
    }

    /**
     * Binds data to the views in each row of the RecyclerView.
     */
    @Override
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        InventoryItem item = itemList.get(position);

        // Populate fields with current item values
        holder.etSku.setText(item.getSku());
        holder.etDesc.setText(item.getDesc());
        holder.etQuantity.setText(String.valueOf(item.getQuantity()));

        // Save button listener to update or insert item
        holder.btnSave.setOnClickListener(v -> {
            String newSku = holder.etSku.getText().toString().trim();
            String newDesc = holder.etDesc.getText().toString().trim();
            int newQty;

            // Validate that quantity is a number
            try {
                newQty = Integer.parseInt(holder.etQuantity.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update item in database
            InventoryItem updatedItem = new InventoryItem(newSku, newDesc, newQty);
            dbHelper.insertOrUpdateItem(updatedItem);

            // Update both lists
            itemList.set(position, updatedItem);
            int fullIndex = fullItemList.indexOf(item);

            if (fullIndex != -1) {
                fullItemList.set(fullIndex, updatedItem);
            } else {
                fullItemList.add(updatedItem);
            }
            // notify the adapter
            notifyItemChanged(position);
            Toast.makeText(context, "Item saved", Toast.LENGTH_SHORT).show();

            // check if the quantity is 0 or less, and notify the user
            if(newQty <= 0 && ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendLowStockNotification(newSku, newDesc);
            }
        });

        // Delete button listener to remove item from list and database
        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.deleteItem(item.getSku());
            itemList.remove(item);
            fullItemList.remove(item);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Returns number of items in the list
     * @return the list size
     */
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Adds a new item to the list and notifies the dapater
     * @param item The item to add
     */
    public void addItem(InventoryItem item) {
        itemList.add(item);
        fullItemList.add(item);
        notifyItemInserted(itemList.size() - 1);
    }

    /**
     * Method to send a low stock notification to the emulator's number
     * @param sku The sku of the item
     * @param desc The desc of the item
     */
    private void sendLowStockNotification(String sku, String desc) {
        String number = "15551234567"; // the emulated number for my android phone.
        String message = String.format("Alert!!! SKU %s - %s has reached zero items.", sku, desc);

        // get the sms manager
        SmsManager manager = SmsManager.getDefault();

        // send the text
        manager.sendTextMessage(number, null, message, null, null);

    }



}
