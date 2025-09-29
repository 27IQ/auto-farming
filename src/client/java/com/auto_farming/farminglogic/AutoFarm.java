package com.auto_farming.farminglogic;

import static com.auto_farming.actionwrapper.Actions.LEFT_CLICK;
import static com.auto_farming.actionwrapper.Direction.LEFT;
import static com.auto_farming.actionwrapper.Direction.RIGHT;
import static com.auto_farming.misc.RandNumberHelper.Random;
import static com.auto_farming.misc.ThreadHelper.*;
import static com.auto_farming.farminglogic.AutoFarmState.*;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.actionwrapper.Direction;
import com.auto_farming.actionwrapper.MouseLocker;
import com.auto_farming.data.ModData;
import com.auto_farming.data.ModDataHolder;
import com.auto_farming.farminglogic.autofarmextensions.BlockBreakDetection;
import com.auto_farming.farminglogic.waiter.PauseableDisruptWaiter;
import com.auto_farming.gui.StatusHUD;
import com.auto_farming.inventory.HotbarSlot;
import com.auto_farming.inventory.InventoryTransaction;
import com.auto_farming.misc.MiscHelper;
import com.auto_farming.moods.Mood;
import com.auto_farming.sounds.AutoSoundMuter;

public class AutoFarm extends PauseableDisruptWaiter {

    // profile
    private ModData settings;
    private Direction startingDirection;
    // state
    private AutoFarmState currentState;
    private int currentLayer;
    private long elapsedRowTime = 0;
    private Direction currentDirection;
    // polling
    // moods
    private Mood currentMood;
    private long currentMoodDuration = 0;
    // debugging
    private long otherDelays = 0;
    private long walkedTime = 0;
    private long startTime = 0;

    public AutoFarm() {
        super();
        currentState = DISABLED;
        settings = ModData.cloneOf(ModDataHolder.DATA);
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
        BlockBreakDetection.startDetection(settings.getBufferSize(), settings.getMinimumAverageBps());
        AutoSoundMuter.activate();
        setPaused(false);
        setForcePaused(false);
        MouseLocker.lockMouse();
        settings.getCurrentProfile().profileSetUp();
    }

    private void onClose() {
        currentState = DISABLED;
        AutoSoundMuter.deactivate();
        MouseLocker.unlockMouse();
        StatusHUD.setMessage("");
        InventoryTransaction.clearQueue();
        BlockBreakDetection.stopDetection();
        Actions.deactivateAll();
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

            for (currentLayer = 0; currentLayer < settings.getCurrentProfile().layerCount; currentLayer++) {
                currentState = ROW;
                clearRow();

                if (farmingThread.isInterrupted())
                    break;

                if (settings.getCurrentProfile().layerSwapTime != 0) {
                    currentState = LAYER_SWAP;
                    layerSwap();
                }

                toggleDirection();
            }

            if (farmingThread.isInterrupted())
                break;

            currentState = VOID_DROP;
            handleVoidDrop();

            if (farmingThread.isInterrupted())
                break;

            currentState = DISABLED;
            settings.getCurrentProfile().profileSetUp();
        }

        onClose();
    }

    private void clearRow() {
        resetDebug();

        long moodOvershoot = settings.getCurrentProfile().layerCount == currentLayer + 1 ? 0
                : Random(200, 500) + currentMood.getRandomMoodOvershoot(settings.isForceAttentiveMood());
        long rowTime = getCurrentRowClearTime();
        long totalTime = rowTime + moodOvershoot;

        activateCurrentActions();

        elapsedRowTime = 0;

        BlockBreakDetection.unpause();

        plannedWait(rowTime, POLLING_INTERVAL, (chunkTime, waitingDuration) -> {
            elapsedRowTime += chunkTime;
            walkedTime += chunkTime;

            if (currentMoodDuration > 0) {
                currentMoodDuration -= chunkTime;
            } else {
                switchMood();
            }

            double progress = ((double) elapsedRowTime / waitingDuration) * 100;
            StatusHUD.setMessage(settings.getCurrentProfile().name
                    + "\nLayer " + (currentLayer + 1) + "/" + settings.getCurrentProfile().layerCount
                    + "\nRow progress: " + Math.round(progress) + "%"
                    + "\nCurrent mood: " + currentMood.NAME
                    + "\nRow time: " + MiscHelper.getTimeStringFromMillis(waitingDuration)
                    + "\nElapsed row time: " + MiscHelper.getTimeStringFromMillis(elapsedRowTime));
        });

        BlockBreakDetection.pause();

        if (moodOvershoot != 0) {
            plannedWait(moodOvershoot, POLLING_INTERVAL, (chunkTime, waitingDuration) -> {
                elapsedRowTime += chunkTime;
                walkedTime += chunkTime;

                if (currentMoodDuration > 0) {
                    currentMoodDuration -= chunkTime;
                } else {
                    switchMood();
                }

                StatusHUD.setMessage(settings.getCurrentProfile().name
                        + "\nLayer " + (currentLayer + 1) + "/" + settings.getCurrentProfile().layerCount
                        + "\nCurrent mood: " + currentMood.NAME
                        + "\nOvershoot time: " + MiscHelper.getTimeStringFromMillis(waitingDuration)
                        + "\nElapsed Overshoot time: " + MiscHelper.getTimeStringFromMillis(elapsedRowTime));
            });
        } else {
            randomSteapSleep(MEDIUM_DURATION);
        }

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
     * Pausing
     * 
     */

    @Override
    protected void onPause() {
        deactivateCurrentActions();
        MouseLocker.unlockMouse();
    }

    @Override
    protected void onUnpause() {
        activateCurrentActions();
        MouseLocker.lockMouse();
    }

    /*
     * 
     * Actions
     * 
     */

    private void activateCurrentActions() {
        Actions[] currentActionOrder = Actions.randomiseActionOrder(getCurrentActions());

        randomSteapSleep(VERY_SHORT_DURATION);
        LEFT_CLICK.activate();
        randomSteapSleep(
                (currentMood.getRandomClickDelayMiss() ? VERY_SHORT_DURATION : MEDIUM_DURATION) + getMoodClickDelay());

        for (int i = 0; i < currentActionOrder.length; i++) {
            if (currentActionOrder[i].isActive())
                continue;

            currentActionOrder[i].activate();
            randomSteapSleep(SHORT_DURATION + getMoodClickDelay());
        }
    }

    private void deactivateCurrentActions() {

        Actions[] currentActionOrder = Actions.randomiseActionOrder(getCurrentActions());

        randomSteapSleep(VERY_SHORT_DURATION);

        for (int i = 0; i < currentActionOrder.length; i++) {
            if (!currentActionOrder[i].isActive())
                continue;

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

    public boolean getAutoMuteSounds() {
        return settings.getAutoMuteSounds();
    }

    public int getMaximumPestNumber() {
        return settings.getMaximumPestNumber();
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

    private Actions[] getCurrentActions() {

        Actions[] currentActions;

        switch (currentState) {
            case ROW:
                currentActions = currentDirection == LEFT ? settings.getCurrentProfile().actionsLeft
                        : settings.getCurrentProfile().actionsRight;
                break;
            case LAYER_SWAP:
                currentActions = settings.getCurrentProfile().actionsLayerSwap;
                break;
            default:
                currentActions = new Actions[0];
                AutofarmingClient.LOGGER.error("No current actions to return. THIS SHOULD NOT HAPPEN!");
                break;
        }

        return currentActions;
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
