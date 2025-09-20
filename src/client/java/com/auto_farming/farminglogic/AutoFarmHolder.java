package com.auto_farming.farminglogic;

import java.util.Optional;

import com.auto_farming.actionwrapper.Direction;

public final class AutoFarmHolder {

    private AutoFarmHolder() {
    }

    private static AutoFarm farm;

    public static final void startNewFarm(Direction startDirection) {
        farm = new AutoFarm();
        farm.start(startDirection);
    }

    public static final void removeInstance() {
        farm.kill();
        farm = null;
    }

    public static final Optional<AutoFarm> get() {
        return farm == null ? Optional.empty() : Optional.of(farm);
    }
}
