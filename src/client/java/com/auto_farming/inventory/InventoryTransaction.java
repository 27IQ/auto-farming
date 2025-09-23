package com.auto_farming.inventory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.inventory.transactionhelper.InventoryTransactionHelper;
import com.auto_farming.inventory.transactionhelper.ItemNotInInventoryException;

public class InventoryTransaction extends InventoryTransactionHelper {
    private static final Queue<InventoryTransaction> transactionQueue = new ConcurrentLinkedQueue<>();
    private final String name;
    private final Runnable transaction;
    private final Runnable onSuccess;
    private final Consumer<Exception> crashHandler;
    private long lastSuccess;
    private final long maxCooldown;

    public InventoryTransaction(String name, Runnable transaction, Consumer<Exception> crashHandler,
            Runnable onSuccess, long maxCooldown) {
        this.name = name;
        this.transaction = transaction;
        this.crashHandler = crashHandler;
        this.onSuccess = onSuccess;
        lastSuccess = 0;
        this.maxCooldown = maxCooldown;
    }

    public boolean queueIfAbsent() {

        if (transactionQueue.contains(this) || System.currentTimeMillis() - this.lastSuccess < maxCooldown)
            return false;

        AutofarmingClient.LOGGER.info("added " + this.name + " to the queue");
        return transactionQueue.offer(this);

    }

    public static boolean isQueueEmpty() {
        return transactionQueue.isEmpty();
    }

    public static void runNextTransactions() {
        int interations = transactionQueue.size();

        for (int i = 0; i < interations; i++) {
            InventoryTransaction transaction = transactionQueue.peek();

            try {
                transaction.transaction.run();
                AutofarmingClient.LOGGER.info(transaction.name + "has succeeded");
                transaction.lastSuccess = System.currentTimeMillis();
                transaction.onSuccess.run();
            } catch (ItemNotInInventoryException e) {
                AutofarmingClient.LOGGER.error(e.getMessage(), e);
                transaction.crashHandler.accept(e);
                transactionQueue.offer(transaction);
            }

            transactionQueue.poll();
        }

        selectHotbarSlot(0);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof InventoryTransaction))
            return false;

        InventoryTransaction otherInventoryTransaction = (InventoryTransaction) other;

        return this.name.equals(otherInventoryTransaction.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, transaction, crashHandler);
    }
}
