package com.auto_farming.farmprofiles;

import static com.auto_farming.actionwrapper.Actions.WALK_FORWARD;
import static com.auto_farming.actionwrapper.Actions.WALK_LEFT;
import static com.auto_farming.actionwrapper.Actions.WALK_RIGHT;

import java.util.function.Function;

import static com.auto_farming.actionwrapper.Actions.WALK_BACK;

import com.auto_farming.actionwrapper.Actions;

@SuppressWarnings("unused")
public enum Profile {

    NETHERWART("5x5 Nether Warts @ 116 (0|0)", 96000, 96000, 3500, 0, 5, new Actions[] { WALK_LEFT },
            new Actions[] { WALK_RIGHT }, new Actions[] {}),
    MUSHROOM("5x4 Mushroom @ 126 (25L|0)", 92000, 97000, 3500, 0, 4, new Actions[] { WALK_FORWARD, WALK_LEFT },
            new Actions[] { WALK_RIGHT }, new Actions[] {});

    private Profile(String name, long leftRowClearTime, long rightRowClearTime, long voidDropTime, long layerSwapTime,
            int layerCount, Actions[] actionsLeft, Actions[] actionsRight, Actions[] actionsLayerSwap) {
        this.NAME = name;
        this.LEFT_ROW_CLEAR_TIME = leftRowClearTime;
        this.RIGHT_ROW_CLEAR_TIME = rightRowClearTime;
        this.VOID_DROP_TIME = voidDropTime;
        this.LAYER_SWAP_TIME = layerSwapTime;
        this.LAYER_COUNT = layerCount;
        this.ACTIONS_LEFT = actionsLeft;
        this.ACTIONS_RIGHT = actionsRight;
        this.ACTIONS_LAYER_SWAP = actionsLayerSwap;
    }

    public final String NAME;
    public final long LEFT_ROW_CLEAR_TIME;
    public final long RIGHT_ROW_CLEAR_TIME;
    public final long VOID_DROP_TIME;
    public final long LAYER_SWAP_TIME;
    public final int LAYER_COUNT;
    public final Actions[] ACTIONS_LEFT;
    public final Actions[] ACTIONS_RIGHT;
    public final Actions[] ACTIONS_LAYER_SWAP;

    @Override
    public String toString() {
        return this.NAME;
    }
}