package com.auto_farming.inventory.transactions;

import static com.auto_farming.inventory.transactionhelper.InventoryTransactionHelper.*;

import com.auto_farming.inventory.InventoryTransaction;
import com.auto_farming.inventory.transactionhelper.ItemNotInInventoryException;

public final class AutoPestRepellentTransaction {

    public static final String PEST_REPELLENT = "PEST_REPELLENT", PEST_REPELLENT_MAX = "PEST_REPELLENT_MAX";

    public static final InventoryTransaction AUTO_PEST_REPELLENT_TRANSACTION = new InventoryTransaction(
            "AUTO_PEST_REPELLENT_TRANSACTION",
            () -> {
                int indexOfRepellent = slotOfSkyblockItem(PEST_REPELLENT);
                int indexOfRepellentMax = slotOfSkyblockItem(PEST_REPELLENT_MAX);

                int target = Math.max(indexOfRepellentMax, indexOfRepellent);

                if (target == -1)
                    throw new ItemNotInInventoryException(PEST_REPELLENT + "/" + PEST_REPELLENT_MAX);

                int dest = moveItemToHotbar(target);
                selectHotbarSlot(dest);
                useItem();
            },
            (exception) -> {
                switch (exception.getClass().getSimpleName()) {
                    case "ItemNotInInventoryException":
                            
                        break;
                }
            });
}