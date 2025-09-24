package com.auto_farming.skyblock;

public enum SkyBlockItem {
    PEST_REPELLENT_MAX("Pest Repellent MAX","PEST_REPELLENT_MAX");

    public final String NAME;
    public final String ID;

    private SkyBlockItem(String name, String id){
        this.NAME=name;
        this.ID=id;
    }
}
