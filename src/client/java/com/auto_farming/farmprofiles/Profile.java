package com.auto_farming.farmprofiles;

import static com.auto_farming.actionwrapper.Actions.WALK_FORWARD;
import static com.auto_farming.actionwrapper.Actions.WALK_LEFT;
import static com.auto_farming.actionwrapper.Actions.WALK_RIGHT;
import static com.auto_farming.actionwrapper.Actions.WALK_BACK;

import com.auto_farming.actionwrapper.Actions;

@SuppressWarnings("unused")
public enum Profile {

    NETHERWART("5x5 Nether Warts @ 116 (0|0)", 96000, 96000, 3500, 0, 5, new Actions[]{WALK_LEFT},new Actions[]{WALK_RIGHT}, new Actions[]{}),
    MUSHROOM("5x4 Mushroom @ 126 (25L|0)", 92000,97000, 3500, 0, 4, new Actions[]{WALK_FORWARD,WALK_LEFT},new Actions[]{WALK_RIGHT}, new Actions[]{});


    private Profile(String name,long left_row_clear_time,long right_row_clear_time,long void_drop_time,long layer_swap_time, int layer_count, Actions[] actions_left, Actions[] actions_right, Actions[] actions_layer_swap){
        this.name=name;
        this.left_row_clear_time=left_row_clear_time; 
        this.right_row_clear_time=right_row_clear_time;
        this.void_drop_time=void_drop_time;
        this.layer_swap_time=layer_swap_time;
        this.layer_count=layer_count;
        this.actions_left=actions_left; 
        this.actions_right=actions_right;
        this.actions_layer_swap=actions_layer_swap;
    }

    public final String name;
    public final long left_row_clear_time; 
    public final long right_row_clear_time;
    public final long void_drop_time;
    public final long layer_swap_time;
    public final int layer_count;
    public final Actions[] actions_left; 
    public final Actions[] actions_right;
    public final Actions[] actions_layer_swap;
}