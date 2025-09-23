package com.auto_farming.inventory.transactions;

import java.util.function.Consumer;

import com.auto_farming.farminglogic.AutoFarmHolder;
import com.auto_farming.farminglogic.FarmingDisrupt;
import com.auto_farming.inventory.InventoryTransaction;
import com.auto_farming.inventory.transactionhelper.ItemNotInInventoryException;

public final class AutoPestRepellentTransaction extends InventoryTransaction {

    private AutoPestRepellentTransaction(String name, Runnable transaction, Consumer<Exception> crashHandler,
            Runnable onSuccess, long maxCooldown) {
        super(name, transaction, crashHandler, onSuccess, maxCooldown);
    }

    public static final String PEST_REPELLENT_MAX = "PEST_REPELLENT_MAX";

    public static final InventoryTransaction AUTO_PEST_REPELLENT_TRANSACTION = new InventoryTransaction(
            "AUTO_PEST_REPELLENT_TRANSACTION",
            () -> {
                int indexOfRepellentMax;
                if ((indexOfRepellentMax = slotOfSkyblockItem(PEST_REPELLENT_MAX)) == -1)
                    throw new ItemNotInInventoryException(PEST_REPELLENT_MAX);

                int dest = moveItemToHotbar(indexOfRepellentMax);
                selectHotbarSlot(dest);
                useItem();
            },
            (exception) -> {
                if (exception instanceof ItemNotInInventoryException) {
                    AutoFarmHolder.get().get().queueDisrupt(
                            new FarmingDisrupt("Please add a " + PEST_REPELLENT_MAX + " to your inventory."));
                }
            },
            () -> {
            },
            15000);
}