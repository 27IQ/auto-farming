package com.auto_farming.inventory;

public enum HotbarSlot {
    SLOT_1(0, "Slot 1"), SLOT_2(1, "Slot 2"), SLOT_3(2, "Slot 3"), SLOT_4(3, "Slot 4"), SLOT_5(4, "Slot 5"),
    SLOT_6(5, "Slot 6"), SLOT_7(6, "Slot 7"), SLOT_8(7, "Slot 8");

    public final int ID;
    public final String NAME;

    private HotbarSlot(int id, String name) {
        this.ID = id;
        this.NAME = name;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
