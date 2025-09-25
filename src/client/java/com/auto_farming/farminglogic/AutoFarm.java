package com.auto_farming.farminglogic;

import static com.auto_farming.actionwrapper.Actions.LEFT_CLICK;
import static com.auto_farming.actionwrapper.Direction.LEFT;
import static com.auto_farming.actionwrapper.Direction.RIGHT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;
import static com.auto_farming.misc.ThreadHelper.preciseSleep;
import static com.auto_farming.misc.RandNumberHelper.Random;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.actionwrapper.Direction;
import com.auto_farming.actionwrapper.MouseLocker;
import com.auto_farming.data.ModData;
import com.auto_farming.data.ModDataHolder;
import com.auto_farming.event.EventManager;
import com.auto_farming.event.events.mainevents.ForcePauseHandleEvent;
import com.auto_farming.gui.StatusHUD;
import com.auto_farming.inventory.InventoryTransaction;
import com.auto_farming.moods.Mood;

public class AutoFarm {
    // profile
    private ModData currentSettings;
    private Direction startingDirection;
    // state
    private boolean isActive = false;
    private boolean isPaused = false;
    private boolean isForcePaused = false;
    private Direction currentDirection;
    private ConcurrentLinkedQueue<FarmingDisrupt> disruptQueue;
    private boolean nextDisrupt = false;
    // settings
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
        this.currentSettings = ModData.cloneOf(ModDataHolder.DATA);
        disruptQueue = new ConcurrentLinkedQueue<>();
        currentMood = getNextMood();
    }

    public void start(Direction startingDirection) {
        this.startingDirection = startingDirection;

        Thread.ofPlatform().start(() -> {
            runFarm();
        });
    }

    public void kill() {
        isActive = false;
    }

    private void onStart() {
        isActive = true;
        isPaused = false;
        isForcePaused = false;
        MouseLocker.lockMouse();
        profileSetUp();
    }

    private void onClose() {
        MouseLocker.unlockMouse();
        StatusHUD.setMessage("");
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isForcePaused() {
        return isForcePaused;
    }

    public void nextDisrupt() {
        nextDisrupt = true;
    }

    public void pauseToggle() {

        if (!isActive || isForcePaused)
            return;

        isPaused = !isPaused;
        AutofarmingClient.LOGGER.info("isPaused: " + isPaused);

        if (MouseLocker.isMouseLocked()) {
            MouseLocker.lockMouse();
        } else {
            MouseLocker.unlockMouse();
        }
    }

    public void queueDisrupt(FarmingDisrupt disrupt) {
        AutofarmingClient.LOGGER.info("Queuing disrupt: " + disrupt.getMessage());
        disruptQueue.offer(disrupt);
    }

    public void profileSetUp() {
        if (!isActive)
            return;

        Actions[] setupActions = currentSettings.getCurrentProfile().actionsStart;

        if (setupActions.length == 0)
            return;

        for (Actions action : setupActions) {
            preciseSleep(Random(150, 200));
            action.activate();
            preciseSleep(Random(450, 550));
            action.deactivate();
        }

        preciseSleep(Random(50, 100));
    }

    private void runFarm() {

        if (isActive)
            return;

        onStart();

        while (isActive) {

            currentDirection = startingDirection;

            for (int i = 0; i < currentSettings.getCurrentProfile().layerCount; i++) {
                clearRow();

                if (!isActive)
                    break;

                if (currentSettings.getCurrentProfile().layerSwapTime != 0)
                    layerSwap();

                toggleDirection();
            }

            handleVoidDrop();
            profileSetUp();
        }

        onClose();
    }

    private void clearRow() {
        resetDebug();

        long moodOvershoot = getMoodOvershoot();
        long totalTime = getCurrentRowClearTime() + Random(0, 250) + moodOvershoot;

        if (getCurrentRowClearTime() > totalTime) {
            AutofarmingClient.LOGGER.error("RowClearTime got reduced! " + getCurrentRowClearTime() + "->" + totalTime
                    + " overwriting totalTime");
            totalTime = getCurrentRowClearTime();
        }

        activateCurrentActions();

        long elapsedTime = 0;

        while (elapsedTime < totalTime && isActive) {

            long remainingTime = totalTime - elapsedTime;
            long sleepChunk = Math.min(POLLING_INTERVAL, remainingTime);

            long actualTimeWaited = rowWait(sleepChunk);

            elapsedTime += actualTimeWaited;
            walkedTime += actualTimeWaited;

            if (currentMoodDuration > 0) {
                currentMoodDuration -= actualTimeWaited;
            } else {
                switchMood();
            }

            double progress = ((double) elapsedTime / totalTime) * 100;
            StatusHUD.setMessage(currentSettings.getCurrentProfile().name + "\nRow progress: "
                    + Math.round(progress)
                    + "%\nCurrent mood: "
                    + currentMood.NAME + "\nRow time: " + getTimeStringFromMillis(totalTime) + "\nElapsed row time: "
                    + getTimeStringFromMillis(elapsedTime) + "\nMood Time: "
                    + getTimeStringFromMillis(currentMoodDuration) + " \nMood overshoot: "
                    + getTimeStringFromMillis(moodOvershoot));
        }

        deactivateCurrentActions();

        long duration = (System.nanoTime() - startTime) / 1_000_000;
        long sum = (walkedTime + pausedTime + otherDelays);

        AutofarmingClient.LOGGER.info(
                "projectedRowDuration: " + totalTime +
                        "\nwalkedTime: " + walkedTime +
                        "\nwalkedTimeDiff: " + (walkedTime - totalTime) +
                        "\npausedTime: " + pausedTime +
                        "\notherTimeWaited: " + otherDelays +
                        "\nsum: " + sum +
                        "\nprocessing%: "
                        + ((((double) duration / (double) sum) - 1) * 100) + "%" +
                        "\nintervalDuration: " + duration +
                        "\nintervalDiff: " + (duration - sum));

    }

    private long rowWait(long millis) {
        checkPause();
        long actualSleep = waitFor(millis);
        checkPause();
        return actualSleep;
    }

    private long waitFor(long millis) {
        long sleepStart = System.nanoTime();

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
        }

        return (System.nanoTime() - sleepStart) / 1_000_000;
    }

    private void checkPause() {
        checkForcePauseEligible();

        if (isPaused || isForcePaused) {
            AutofarmingClient.LOGGER.info("Pausing ...");
            MouseLocker.unlockMouse();
            deactivateCurrentActions();
            handlePauseState();
            AutofarmingClient.LOGGER.info("Unpausing ...");
            if (isActive) {
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

    private void handleVoidDrop() {

        walkedTime += currentSettings.getCurrentProfile().voidDropTime;

        long elapsedVoid = 0;

        while (elapsedVoid < currentSettings.getCurrentProfile().voidDropTime && isActive) {
            long remainingVoid = currentSettings.getCurrentProfile().voidDropTime - elapsedVoid;
            long sleepChunk = Math.min(POLLING_INTERVAL, remainingVoid);

            elapsedVoid += rowWait(sleepChunk);

            double progress = ((double) elapsedVoid / currentSettings.getCurrentProfile().voidDropTime) * 100;
            StatusHUD.setMessage("Void drop: " + Math.round(progress) + "%");
        }
    }

    private void handlePauseState() {
        long pauseStart = System.nanoTime();

        while (isActive && (isPaused || isForcePaused)) {
            if (isPaused && currentSettings.isShowPauseMessage()) {
                StatusHUD.setMessage("PAUSED - Press " + PAUSE_TOGGLE.toString() + " to resume");
            } else if (isForcePaused && currentSettings.isShowPauseMessage()) {
                EventManager.trigger(new ForcePauseHandleEvent());
            }

            while (!disruptQueue.isEmpty()) {
                nextDisrupt = false;
                StatusHUD.setMessage(disruptQueue.poll().getMessage());

                while (!nextDisrupt) {
                    waitFor(POLLING_INTERVAL);
                }

                EventManager.trigger(new ForcePauseHandleEvent());
            }

            isForcePaused = false;

            waitFor(POLLING_INTERVAL);
        }

        pausedTime += (System.nanoTime() - pauseStart) / 1_000_000;
    }

    private void activateCurrentActions() {
        Long[] deviation = getClickDeviation(getCurrentDirectionActions().length);
        boolean miss = getClickDelayMiss();
        Actions[] currentActionOrder = getCurrentActionOrder();

        preciseSleep(10);
        LEFT_CLICK.activate();
        preciseSleep((miss ? 0 : 500) + deviation[0]);

        for (int i = 0; i < deviation.length - 1; i++) {
            currentActionOrder[i].activate();
            preciseSleep(deviation[i + 1]);
        }
    }

    private void deactivateCurrentActions() {
        Long[] deviation = getClickDeviation(getCurrentDirectionActions().length);
        Actions[] currentActionOrder = getCurrentActionOrder();

        preciseSleep(10);

        for (int i = 0; i < deviation.length - 1; i++) {
            currentActionOrder[i].deactivate();
            preciseSleep(deviation[i]);
        }

        LEFT_CLICK.deactivate();
        preciseSleep(deviation[deviation.length - 1]);
    }

    private Long[] getClickDeviation(int count) {

        int rand = Random(50, 100);
        long moodClickDelay = getMoodClickDelay();

        List<Long> result = new ArrayList<>();
        result.add(rand + moodClickDelay);

        for (int i = 0; i < count; i++) {
            result.add((long) Random(0, 70));
        }

        return result.toArray(new Long[0]);
    }

    private long getMoodClickDelay() {
        long delay = currentMood.CLICK_DELAY;

        if (currentSettings.isForceAttentiveMood()) {
            delay = Mood.ATTENTIVE.CLICK_DELAY;
        }

        return delay;
    }

    private Actions[] getCurrentActionOrder() {

        int min = 0;
        Actions[] currentActions = getCurrentDirectionActions();
        int max = currentActions.length - 1;

        List<Actions> results = new ArrayList<>();

        while (results.size() < currentActions.length) {
            int pull = Random(min, max);

            if (results.size() == 0) {
                results.add(currentActions[pull]);
                continue;
            }

            boolean foundFlag = false;

            for (Actions action : results) {
                if (action == currentActions[pull])
                    foundFlag = true;
            }

            if (!foundFlag)
                results.add(currentActions[pull]);
        }

        return results.toArray(new Actions[0]);
    }

    private boolean getClickDelayMiss() {

        long pull = Random(0, 1);

        return pull == 1 ? true : false;
    }

    private void layerSwap() {

        Actions[] actions = currentSettings.getCurrentProfile().actionsLayerSwap;

        Long[] deviation = getClickDeviation(actions.length * 2);

        for (int i = 0; i < actions.length; i++) {
            actions[i].activate();
            preciseSleep(deviation[i]);
        }

        walkedTime += currentSettings.getCurrentProfile().layerSwapTime;

        preciseSleep(currentSettings.getCurrentProfile().layerSwapTime);

        for (int i = 0; i < actions.length; i++) {
            actions[i].deactivate();
            preciseSleep(deviation[actions.length + i]);
        }
    }

    private void toggleDirection() {
        currentDirection = currentDirection == LEFT ? RIGHT : LEFT;
    }

    private Actions[] getCurrentDirectionActions() {
        return currentDirection == LEFT ? currentSettings.getCurrentProfile().actionsLeft
                : currentSettings.getCurrentProfile().actionsRight;
    }

    private long getCurrentRowClearTime() {
        return currentDirection == LEFT ? currentSettings.getCurrentProfile().leftRowClearTime
                : currentSettings.getCurrentProfile().rightRowClearTime;
    }

    private long getMoodOvershoot() {

        long overshoot = 0;

        if (currentSettings.isForceAttentiveMood() || currentMood.OVERSHOOT_DURATION == 0)
            return overshoot;

        long roll = Random(0, 1);

        if (currentMood.OVERSHOOT_CHANCE <= roll) {
            long minDur = currentMood.OVERSHOOT_DURATION - currentMood.OVERSHOOT_DURATION_VARIABLE;
            long maxDur = currentMood.OVERSHOOT_DURATION + currentMood.OVERSHOOT_DURATION_VARIABLE;
            overshoot = Random(minDur, maxDur);
        }

        return overshoot;
    }

    private void switchMood() {
        Mood newMood = getNextMood();

        currentMood = newMood;
        currentMoodDuration = Random(currentMood.MOOD_MIN_DURATION, currentMood.MOOD_MAX_DURATION);
    }

    private Mood getNextMood() {
        List<Double> chances = new ArrayList<>();

        for (Mood mood : Mood.values()) {
            if (mood == Mood.DISTRACTED && !currentSettings.getEnableDistracted()) {
                double lastChance = chances.getLast();
                lastChance += Mood.DISTRACTED.MOOD_CHANCE;
                chances.set(chances.size() - 1, lastChance);
                continue;
            }

            chances.add(mood.MOOD_CHANCE);
        }

        double currentThreshold = 0;
        double roll = Math.random();

        int selectedMoodIndex = -1;

        for (int i = 0; i < chances.size(); i++) {
            currentThreshold += chances.get(i);

            if (selectedMoodIndex == -1 && currentThreshold >= roll) {
                selectedMoodIndex = i;
            }
        }

        return Mood.values()[selectedMoodIndex];
    }

    private String getTimeStringFromMillis(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long milliseconds = millis % 1000;

        // Format: m:ss,mmm
        return String.format("%d:%02d,%03d", minutes, seconds, milliseconds);
    }

    private void resetDebug() {
        startTime = System.nanoTime();
        otherDelays = 0;
        walkedTime = 0;
        pausedTime = 0;
    }

    public void reportDelay(long delayMillis) {
        otherDelays += delayMillis;
    }
}
