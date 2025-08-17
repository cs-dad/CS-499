package com.example.warehouseapp.Types;

public class InventoryItem {

    // necessary variables for an inventory item
    private String sku;
    private String description;
    private int quantity;

    public InventoryItem(String sku, String description, int quantity) {
        this.sku = sku;
        this.description = description;
        this.quantity = quantity;
    }

    public String getSku() { return this.sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getDesc() { return this.description; }
    public void setDesc(String desc) { this.description = desc; }

    public int getQuantity() { return this.quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

}
