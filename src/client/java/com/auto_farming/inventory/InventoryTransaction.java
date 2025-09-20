package com.auto_farming.inventory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.inventory.transactionhelper.ItemNotInInventoryException;

public class InventoryTransaction {
    private static final Queue<InventoryTransaction> transactionQueue = new ConcurrentLinkedQueue<>();
    private final String name;
    private final Runnable transaction;
    private final Consumer<Exception> crashHandler;

    public InventoryTransaction(String name, Runnable transaction, Consumer<Exception> crashHandler) {
        this.name = name;
        this.transaction = transaction;
        this.crashHandler = crashHandler;
    }

    public void queue() {
        transactionQueue.add(this);
    }

    public static void runNextTransactions() {
        while (!transactionQueue.isEmpty()) {
            InventoryTransaction transaction = transactionQueue.poll();

            try {
                transaction.transaction.run();
                AutofarmingClient.LOGGER.info(transaction.name + "has succeeded");
            } catch (ItemNotInInventoryException e) {
                AutofarmingClient.LOGGER.error(e.getMessage());
                transaction.crashHandler.accept(e);
            }

        }
    }
}
