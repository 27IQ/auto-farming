package com.auto_farming.inventory.transactions;

import com.auto_farming.farminglogic.AutoFarmHolder;
import com.auto_farming.farminglogic.disrupt.Disrupt;
import com.auto_farming.inventory.InventoryTransaction;
import com.auto_farming.inventory.transactionhelper.ItemNotInInventoryException;
import static com.auto_farming.skyblock.SkyBlockItem.PEST_REPELLENT_MAX;

public final class AutoPestRepellentTransaction extends InventoryTransaction {

    private AutoPestRepellentTransaction(long cooldown) {
        super(cooldown);
    }

    public static final AutoPestRepellentTransaction AUTO_PEST_REPELLENT_TRANSACTION = new AutoPestRepellentTransaction(
            5000);

    @Override
    protected void transaction() {
        int indexOfRepellentMax;
        if ((indexOfRepellentMax = slotOfSkyblockItem(PEST_REPELLENT_MAX.ID)) == -1)
            throw new ItemNotInInventoryException(PEST_REPELLENT_MAX.NAME);

        int dest = moveItemToHotbar(indexOfRepellentMax);
        selectHotbarSlot(dest);
        useItem();
    }

    @Override
    protected void crashHandler(Exception e) {
        if (e instanceof ItemNotInInventoryException) {
            AutoFarmHolder.get().ifPresent((farm) -> {
                farm.queueDisruptIfAbesent(Disrupt.NO_PEST_REPELLENT_IN_INVENTORY);
            });
        }
    }

    @Override
    protected void onSuccess() {
    }
}