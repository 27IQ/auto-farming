package com.auto_farming.misc;

import static com.auto_farming.actionwrapper.Actions.SNEAK;

import com.auto_farming.chat.Commands;

public class AutoFarmSetup {

    public static void autoSetup() {
        Commands.warpGarden();
        ThreadHelper.preciseSleep(RandNumberHelper.Random(150, 200));
        SNEAK.activate();
        ThreadHelper.preciseSleep(RandNumberHelper.Random(500, 1000));
        SNEAK.deactivate();
    }
}
