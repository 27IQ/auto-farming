package com.auto_farming.farminglogic;

import static com.auto_farming.actionwrapper.Actions.LEFT_CLICK;
import static com.auto_farming.actionwrapper.Direction.LEFT;
import static com.auto_farming.actionwrapper.Direction.RIGHT;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;
import static com.auto_farming.misc.RandNumberHelper.Random;
import static com.auto_farming.misc.ThreadHelper.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.actionwrapper.Direction;
import com.auto_farming.actionwrapper.MouseLocker;
import com.auto_farming.alerts.SoundAlert;
import com.auto_farming.data.ModData;
import com.auto_farming.data.ModDataHolder;
import com.auto_farming.event.EventManager;
import com.auto_farming.event.events.mainevents.ForcePauseHandleEvent;
import com.auto_farming.gui.StatusHUD;
import com.auto_farming.inventory.HotbarSlot;
import com.auto_farming.inventory.InventoryTransaction;
import com.auto_farming.misc.MiscHelper;
import com.auto_farming.moods.Mood;

public class AutoFarm extends Waiter {
    // Thread
    private Thread farmingThread;
    // profile
    private ModData settings;
    private Direction startingDirection;
    // state
    private long currentlayer;
    private long elapsedRowTime = 0;
    private boolean isPaused = false;
    private boolean isForcePaused = false;
    private Direction currentDirection;
    private ConcurrentLinkedQueue<FarmingDisrupt> disruptQueue;
    private boolean nextDisrupt = false;
    // polling
    private final long POLLING_INTERVAL = 100;
    // moods
    private Mood currentMood;
    private long currentMoodDuration = 0;
    // debugging
    private long otherDelays = 0;
    private long walkedTime = 0;
    private long pausedTime = 0;
    private long startTime = 0;

    public AutoFarm() {
        super();
        settings = ModData.cloneOf(ModDataHolder.DATA);
        disruptQueue = new ConcurrentLinkedQueue<>();
        switchMood();
    }

    public void start(Direction startingDirection) {
        this.startingDirection = startingDirection;

        farmingThread = Thread.ofPlatform().start(() -> {
            checkPause();
            runFarm();
        });
    }

    public void kill() {
        farmingThread.interrupt();
    }

    private void onStart() {
        isPaused = false;
        isForcePaused = false;
        MouseLocker.lockMouse();
        settings.getCurrentProfile().profileSetUp();
    }

    private void onClose() {
        MouseLocker.unlockMouse();
        StatusHUD.setMessage("");
        InventoryTransaction.clearQueue();
    }

    public boolean isForcePaused() {
        return isForcePaused;
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

    /*
     * 
     * Main Loop
     * 
     */

    private void runFarm() {

        onStart();

        while (true) {

            currentDirection = startingDirection;

            for (currentlayer = 0; currentlayer < settings.getCurrentProfile().layerCount; currentlayer++) {
                clearRow();

                if (farmingThread.isInterrupted())
                    break;

                if (settings.getCurrentProfile().layerSwapTime != 0)
                    layerSwap();

                toggleDirection();
            }

            if (farmingThread.isInterrupted())
                break;

            handleVoidDrop();
            settings.getCurrentProfile().profileSetUp();
        }

        onClose();
    }

    private void clearRow() {
        resetDebug();

        long moodOvershoot = currentMood.getRandomMoodOvershoot(settings.isForceAttentiveMood());
        long totalTime = getCurrentRowClearTime() + Random(0, 250) + moodOvershoot;

        if (getCurrentRowClearTime() > totalTime) {
            AutofarmingClient.LOGGER.error("RowClearTime got reduced! " + getCurrentRowClearTime() + "->" + totalTime
                    + " overwriting totalTime");
            totalTime = getCurrentRowClearTime();
        }

        activateCurrentActions();

        elapsedRowTime = 0;

        plannedWait(totalTime, POLLING_INTERVAL, (chunkTime, waitingDuration) -> {
            elapsedRowTime += chunkTime;
            walkedTime += chunkTime;

            if (currentMoodDuration > 0) {
                currentMoodDuration -= chunkTime;
            } else {
                switchMood();
            }

            double progress = ((double) elapsedRowTime / waitingDuration) * 100;
            StatusHUD.setMessage(settings.getCurrentProfile().name
                    + "\nLayer " + (currentlayer + 1) + "/" + settings.getCurrentProfile().layerCount
                    + "\nRow progress: " + Math.round(progress) + "%"
                    + "\nCurrent mood: " + currentMood.NAME
                    + "\nRow time: " + MiscHelper.getTimeStringFromMillis(waitingDuration)
                    + "\nElapsed row time: " + MiscHelper.getTimeStringFromMillis(elapsedRowTime)
                    + "\nMood Time: " + MiscHelper.getTimeStringFromMillis(currentMoodDuration)
                    + "\nMood overshoot: " + MiscHelper.getTimeStringFromMillis(moodOvershoot));
        });

        deactivateCurrentActions();

        long duration = (System.nanoTime() - startTime) / 1_000_000;
        long sum = (walkedTime + pausedTime + otherDelays);

        AutofarmingClient.LOGGER.info("projectedRowDuration: " + totalTime);
        AutofarmingClient.LOGGER.info("walkedTime: " + walkedTime);
        AutofarmingClient.LOGGER.info("walkedTimeDiff: " + (walkedTime - totalTime));
        AutofarmingClient.LOGGER.info("pausedTime: " + pausedTime);
        AutofarmingClient.LOGGER.info("otherTimeWaited: " + otherDelays);
        AutofarmingClient.LOGGER.info("sum: " + sum);
        AutofarmingClient.LOGGER.info("processing%: " + ((((double) duration / (double) sum) - 1) * 100) + "%");
        AutofarmingClient.LOGGER.info("intervalDuration: " + duration);
        AutofarmingClient.LOGGER.info("intervalDiff: " + (duration - sum));

    }

    private void layerSwap() {

        Actions[] actions = settings.getCurrentProfile().actionsLayerSwap;

        randomSleep(VERY_SHORT_DURATION);

        for (int i = 0; i < actions.length; i++) {
            actions[i].activate();
            randomSleep(VERY_SHORT_DURATION + getMoodClickDelay());
        }

        plannedWait(settings.getCurrentProfile().layerSwapTime, POLLING_INTERVAL, (chunkTime, waitingDuration) -> {
            StatusHUD.setMessage("Layer swap in progress ...");
        });

        for (int i = 0; i < actions.length; i++) {
            actions[i].deactivate();
            randomSleep(VERY_SHORT_DURATION + getMoodClickDelay());
        }
    }

    private void handleVoidDrop() {
        plannedWait(settings.getCurrentProfile().voidDropTime, POLLING_INTERVAL, (elapsedVoid, plannedDropDuration) -> {
            StatusHUD.setMessage("Void drop in progress ...");
        });
    }

    /*
     * 
     * Timing
     * 
     */

    @Override
    protected void beforeChunk() {
        checkPause();
    }

    @Override
    protected void afterChunk() {
        checkPause();
    }

    /*
     * 
     * Pausing
     * 
     */

    private void checkPause() {
        checkForcePauseEligible();

        if (isPaused || isForcePaused) {
            AutofarmingClient.LOGGER.info("Pausing ...");
            MouseLocker.unlockMouse();
            deactivateCurrentActions();
            handlePauseState();
            AutofarmingClient.LOGGER.info("Unpausing ...");
            if (!farmingThread.isInterrupted()) {
                activateCurrentActions();
                MouseLocker.lockMouse();
            }
        }
    }

    private void checkForcePauseEligible() {
        if (!InventoryTransaction.isQueueEmpty() || !disruptQueue.isEmpty()) {
            isForcePaused = true;
            AutofarmingClient.LOGGER
                    .info("InventoryTransactionQueue: " + InventoryTransaction.queueSize() + " Objects in queue");
            AutofarmingClient.LOGGER.info("DisruptQueue: " + disruptQueue.size() + " Objects in queue");
        }
    }

    private void handlePauseState() {
        long pauseStart = System.nanoTime();

        while (!farmingThread.isInterrupted() && (isPaused || isForcePaused)) {
            if (isPaused && settings.isShowPauseMessage()) {
                StatusHUD.setMessage("PAUSED - Press " + PAUSE_TOGGLE.toString() + " to resume");
            } else if (isForcePaused && settings.isShowPauseMessage()) {
                EventManager.trigger(new ForcePauseHandleEvent());
            }

            handleDisrupts();

            isForcePaused = false;

            waitFor(POLLING_INTERVAL);
        }

        pausedTime += (System.nanoTime() - pauseStart) / 1_000_000;
    }

    /*
     * 
     * Disrupts
     * 
     */

    public void nextDisrupt() {
        nextDisrupt = true;
    }

    public void queueDisrupt(FarmingDisrupt disrupt) {
        AutofarmingClient.LOGGER.info("Queuing disrupt: " + disrupt.getMessage());
        disruptQueue.offer(disrupt);
    }

    public void handleDisrupts() {
        if (farmingThread.isInterrupted() || disruptQueue.isEmpty())
            return;

        SoundAlert.MAMBO_ALERT.play();
        while (!farmingThread.isInterrupted() && !disruptQueue.isEmpty()) {
            nextDisrupt = false;
            StatusHUD.setMessage(disruptQueue.poll().getMessage());

            while (!farmingThread.isInterrupted() && !nextDisrupt) {
                waitFor(POLLING_INTERVAL);
            }

            EventManager.trigger(new ForcePauseHandleEvent());
        }
        SoundAlert.MAMBO_ALERT.stop();
    }

    /*
     * 
     * Actions
     * 
     */

    private void activateCurrentActions() {
        Actions[] currentActionOrder = Actions.randomiseActionOrder(getCurrentDirectionActions());

        randomSteapSleep(VERY_SHORT_DURATION);
        LEFT_CLICK.activate();
        randomSteapSleep(
                (currentMood.getRandomClickDelayMiss() ? VERY_SHORT_DURATION : MEDIUM_DURATION) + getMoodClickDelay());

        for (int i = 0; i < currentActionOrder.length; i++) {
            currentActionOrder[i].activate();
            randomSteapSleep(SHORT_DURATION + getMoodClickDelay());
        }
    }

    private void deactivateCurrentActions() {
        Actions[] currentActionOrder = Actions.randomiseActionOrder(getCurrentDirectionActions());

        randomSteapSleep(VERY_SHORT_DURATION);

        for (int i = 0; i < currentActionOrder.length; i++) {
            currentActionOrder[i].deactivate();
            randomSteapSleep(VERY_SHORT_DURATION + getMoodClickDelay());
        }

        LEFT_CLICK.deactivate();
        randomSteapSleep(SHORT_DURATION);
    }

    /*
     * 
     * Profile
     * 
     */

    public HotbarSlot getFarmingToolSlot() {
        return settings.getFarmingToolSlot();
    }

    public HotbarSlot getFallbackSlot() {
        return settings.getFallbackSlot();
    }

    /*
     * 
     * Helper
     * 
     */

    private long getCurrentRowClearTime() {
        return currentDirection == LEFT ? settings.getCurrentProfile().leftRowClearTime
                : settings.getCurrentProfile().rightRowClearTime;
    }

    private Actions[] getCurrentDirectionActions() {
        return currentDirection == LEFT ? settings.getCurrentProfile().actionsLeft
                : settings.getCurrentProfile().actionsRight;
    }

    private long getMoodClickDelay() {
        return settings.isForceAttentiveMood() ? Mood.ATTENTIVE.CLICK_DELAY : currentMood.CLICK_DELAY;
    }

    private void switchMood() {
        currentMood = Mood.getRandomMood(settings.getEnableDistracted());
        currentMoodDuration = currentMood.getRandomMoodDuration();
    }

    private void toggleDirection() {
        currentDirection = currentDirection == LEFT ? RIGHT : LEFT;
    }

    /*
     * 
     * Debugging
     * 
     */

    private void resetDebug() {
        startTime = System.nanoTime();
        otherDelays = 0;
        walkedTime = 0;
        pausedTime = 0;
    }

    public void reportDelay(long millis) {
        otherDelays += millis;
    }
}
