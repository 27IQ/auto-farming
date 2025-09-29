package com.auto_farming.farminglogic.waiter;

import java.util.function.BiConsumer;

import com.auto_farming.AutofarmingClient;

public abstract class Waiter {

    protected static final long POLLING_INTERVAL = 100;
    protected boolean isActive;

    protected long pausedTime = 0;

    public Waiter() {
    }

    public void kill() {
        isActive = false;
    }

    protected long plannedWait(long durationMillis, long chunkSize, BiConsumer<Long, Long> afterIntervall) {
        long elapsedTime = 0;

        while (isActive && elapsedTime < durationMillis) {

            long remainingTime = durationMillis - elapsedTime;
            long sleepChunk = Math.min(chunkSize, remainingTime);

            long actualTimeWaited = chunkWait(sleepChunk);

            elapsedTime += actualTimeWaited;
            afterIntervall.accept(actualTimeWaited, durationMillis);
        }

        return elapsedTime;
    }

    private long chunkWait(long durationMillis) {
        beforeChunk();
        long actualSleep = waitFor(durationMillis);
        afterChunk();
        return actualSleep;
    }

    public abstract void beforeChunk();

    public abstract void afterChunk();

    protected static long waitFor(long durationMillis) {
        long sleepStart = System.nanoTime();

        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
        }

        return (System.nanoTime() - sleepStart) / 1_000_000;
    }
}
