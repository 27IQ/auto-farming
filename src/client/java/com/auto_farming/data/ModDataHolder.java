package com.auto_farming.data;

public final class ModDataHolder {

    private ModDataHolder() {
    }

    public static final ModData DATA = new ModData();

    static {
        SaveDataLoader.load();
    }
}
