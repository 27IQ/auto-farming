package com.auto_farming.inventory.transactionhelper;

public class ItemNotInInventoryException extends RuntimeException{
    public ItemNotInInventoryException(String itemId){
        super(itemId +" was not found in your Inventory");   
    }
}
