package com.auto_farming.farminglogic.waiter;

import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.MouseLocker;
import com.auto_farming.event.EventManager;
import com.auto_farming.event.events.mainevents.ForcePauseHandleEvent;
import com.auto_farming.farminglogic.disrupt.DisruptHandler;
import com.auto_farming.gui.StatusHUD;
import com.auto_farming.inventory.InventoryTransaction;

public abstract class PauseableDisruptWaiter extends DisruptHandler {

    private boolean isPaused = false;
    private boolean isForcePaused = false;

    protected PauseableDisruptWaiter() {
        super();
    }

    public void setPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public void setForcePaused(boolean isForcePaused) {
        this.isForcePaused = isForcePaused;
    }

    public boolean isPaused() {
        return isPaused || isForcePaused;
    }

    public boolean isForcePaused() {
        return isForcePaused;
    }

    @Override
    public void beforeChunk() {
        checkPause();
    }

    @Override
    public void afterChunk() {
        checkPause();
    }

    protected void checkPause() {
        checkForcePauseEligible();

        if (isPaused || isForcePaused) {
            AutofarmingClient.LOGGER.info("Pausing ...");
            onPause();
            handlePauseState();
            AutofarmingClient.LOGGER.info("Unpausing ...");
            if (!farmingThread.isInterrupted()) {
                onUnpause();
            }
        }
    }

    protected abstract void onPause();

    protected abstract void onUnpause();

    protected void checkForcePauseEligible() {
        if (!InventoryTransaction.isQueueEmpty() || !disruptQueue.isEmpty()) {
            isForcePaused = true;
            AutofarmingClient.LOGGER
                    .info("InventoryTransactionQueue: " + InventoryTransaction.queueSize() + " Objects in queue");
            AutofarmingClient.LOGGER.info("DisruptQueue: " + disruptQueue.size() + " Objects in queue");
        }
    }

    protected void handlePauseState() {
        long pauseStart = System.nanoTime();

        while (!farmingThread.isInterrupted() && (isPaused || isForcePaused)) {
            if (isPaused) {
                StatusHUD.setMessage("PAUSED - Press " + PAUSE_TOGGLE.toString() + " to resume");
            } else if (isForcePaused) {
                EventManager.trigger(new ForcePauseHandleEvent());
            }

            handleDisrupts();

            isForcePaused = false;

            waitFor(POLLING_INTERVAL);
        }

        pausedTime += (System.nanoTime() - pauseStart) / 1_000_000;
    }

    public void pauseToggle() {

        if (farmingThread.isInterrupted() || isForcePaused)
            return;

        isPaused = !isPaused;
        AutofarmingClient.LOGGER.info("isPaused: " + isPaused);

        if (MouseLocker.isMouseLocked()) {
            MouseLocker.lockMouse();
        } else {
            MouseLocker.unlockMouse();
        }
    }

}
