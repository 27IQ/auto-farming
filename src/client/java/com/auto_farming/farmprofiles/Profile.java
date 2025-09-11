package com.auto_farming.farmprofiles;

import com.auto_farming.actionwrapper.Actions;

public class Profile {
    public Profile() {}

    public Profile(String name, long leftRowClearTime, long rightRowClearTime, long voidDropTime, long layerSwapTime,
            int layerCount, Actions[] actionsLeft, Actions[] actionsRight, Actions[] actionsLayerSwap) {
        this.name = name;
        this.leftRowClearTime = leftRowClearTime;
        this.rightRowClearTime = rightRowClearTime;
        this.voidDropTime = voidDropTime;
        this.layerSwapTime = layerSwapTime;
        this.layerCount = layerCount;
        this.actionsLeft = actionsLeft;
        this.actionsRight = actionsRight;
        this.actionsLayerSwap = actionsLayerSwap;
    }

    public String name;
    public long leftRowClearTime;
    public long rightRowClearTime;
    public long voidDropTime;
    public long layerSwapTime;
    public int layerCount;
    public Actions[] actionsLeft;
    public Actions[] actionsRight;
    public Actions[] actionsLayerSwap;

    @Override
    public String toString() {
        return this.name;
    }
}