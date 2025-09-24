package com.auto_farming.inventory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.event.annotations.Event;
import com.auto_farming.event.events.mainevents.ForcePauseHandleEvent;
import com.auto_farming.inventory.transactionhelper.InventoryTransactionHelper;

public abstract class InventoryTransaction extends InventoryTransactionHelper {
    private static final Queue<InventoryTransaction> transactionQueue = new ConcurrentLinkedQueue<>();
    private long lastSuccess;
    private final long cooldown;

    public InventoryTransaction(long maxCooldown) {
        lastSuccess = 0;
        this.cooldown = maxCooldown;
    }

    protected abstract void transaction();

    protected abstract void onSuccess();

    protected abstract void crashHandler(Exception e);

    public boolean queueIfAbsent() {

        if (transactionQueue.contains(this) || ((System.nanoTime() - lastSuccess) / 1_000_000) < cooldown)
            return false;

        AutofarmingClient.LOGGER.info("added " + this.getClass().getSimpleName() + " to the queue");
        return transactionQueue.offer(this);

    }

    public static boolean isQueueEmpty() {
        return transactionQueue.isEmpty();
    }

    public static int queueSize() {
        return transactionQueue.size();
    }

    @Event(ForcePauseHandleEvent.class)
    public static void runNextTransactions(ForcePauseHandleEvent event) {
        AutofarmingClient.LOGGER.info("running InventoryTransactions Queue ...");
        if (transactionQueue.isEmpty())
            return;

        int interations = transactionQueue.size();

        AutofarmingClient.LOGGER.info("InventoryTransactionQueue: handling " + interations + " Transactions ...");

        for (int i = 0; i < interations; i++) {
            InventoryTransaction transaction = transactionQueue.peek();

            try {
                transaction.transaction();
                AutofarmingClient.LOGGER.info(transaction.getClass().getSimpleName() + " has succeeded");
                transaction.lastSuccess = System.nanoTime();
                transaction.onSuccess();
            } catch (Exception e) {
                AutofarmingClient.LOGGER.error(e.getMessage(), e);
                transaction.crashHandler(e);
                transactionQueue.offer(transaction);
            }

            transactionQueue.poll();
        }

        selectHotbarSlot(0);
    }
}
