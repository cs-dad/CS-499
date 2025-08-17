package com.example.warehouseapp.Activities.Adapters;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouseapp.R;

public class InventoryViewHolder extends RecyclerView.ViewHolder {

    // necessary components to bind from the viewholder
    public EditText etSku, etDesc, etQuantity;
    public Button btnSave, btnDelete;

    public InventoryViewHolder(View itemView) {
        super(itemView);

        etSku = itemView.findViewById(R.id.tvSku);
        etDesc = itemView.findViewById(R.id.tvDescription);
        etQuantity = itemView.findViewById(R.id.tvQuantity);
        btnSave = itemView.findViewById(R.id.btnSave);
        btnDelete = itemView.findViewById(R.id.btnDelete);


    }
}
