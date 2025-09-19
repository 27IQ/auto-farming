package com.auto_farming;

import static com.auto_farming.actionwrapper.Actions.LEFT_CLICK;
import static com.auto_farming.actionwrapper.Actions.SNEAK;
import static com.auto_farming.actionwrapper.Directions.LEFT;
import static com.auto_farming.actionwrapper.Directions.NONE;
import static com.auto_farming.actionwrapper.Directions.RIGHT;

import java.util.ArrayList;
import java.util.List;

import static com.auto_farming.gui.BasicHUD.setHudMessage;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;

import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.actionwrapper.Directions;
import com.auto_farming.actionwrapper.MouseLocker;
import com.auto_farming.chat.Commands;
import com.auto_farming.moods.Mood;

public class AutoFarm {

    // state
    public boolean isActive = false;
    private boolean isPaused = false;
    private Directions currentDirection = NONE;
    // settings
    private final long POLLING_INTERVAL = 100;
    // moods
    private Mood currentMood = getNextMood();
    private long currentMoodDuration = 0;
    // debugging
    private boolean debugging = false;
    private long addedTime = 0;
    private long walkedTime = 0;
    private long pausedTime = 0;
    private long startTime = 0;
    private long interval_1 = 0;

    public void onStart() {
        isActive = true;
        isPaused = false;
        MouseLocker.lockMouse();
        profileSetUp();
    }

    public void onClose() {
        MouseLocker.unlockMouse();
        setHudMessage("");
    }

    public boolean isActive() {
        return isActive;
    }

    public void pause_toggle() {

        if (!isActive)
            return;

        isPaused = !isPaused;
        AutofarmingClient.LOGGER.info("isPaused: " + isPaused);

        if (MouseLocker.isMouseLocked()) {
            MouseLocker.lockMouse();
        } else {
            MouseLocker.unlockMouse();
        }
    }

    public static void autoSetUp() {
        Commands.warpGarden();
        preciseSleep(Random(150, 200));
        SNEAK.activate();
        preciseSleep(Random(500, 1000));
        SNEAK.deactivate();
        profileSetUp();
    }

    public static void profileSetUp() {
        Actions[] setupActions = AutofarmingClient.modData.getCurrentProfile().actionsStart;

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

    public void runFarm(Directions direction) {

        if (isActive)
            return;

        if (debugging) {
            startTime = System.currentTimeMillis();
            addedTime = 0;
            walkedTime = 0;
            pausedTime = 0;
        }

        onStart();

        while (isActive) {

            currentDirection = direction;

            for (int i = 0; i < AutofarmingClient.modData.getCurrentProfile().layerCount; i++) {
                clearRow();

                if (!isActive)
                    break;

                if (AutofarmingClient.modData.getCurrentProfile().layerSwapTime != 0)
                    layer_swap();

                toggle_direction();
            }

            handleVoidDrop();
            profileSetUp();

            if (debugging) {
                interval_1 = System.currentTimeMillis();
                long intervalDuration = interval_1 - startTime;

                AutofarmingClient.LOGGER.info(
                        "added_time: " + addedTime +
                                "\nwalked_time: " + walkedTime +
                                "\npaused_time: " + pausedTime +
                                "\nstart_time: " + startTime +
                                "\ninterval_time: " + interval_1 +
                                "\ninterval_duration: " + intervalDuration);
            }
        }

        onClose();
    }

    private void clearRow() {

        long moodOvershoot = getMoodOvershoot();
        long totalTime = getCurrentRowClearTime() + Random(0, 250) + moodOvershoot;

        activateCurrentActions();

        long elapsedTime = 0;

        while (elapsedTime < totalTime && isActive) {

            long remainingTime = totalTime - elapsedTime;
            long sleepChunk = Math.min(POLLING_INTERVAL, remainingTime);

            rowPause();

            long sleepStart = System.currentTimeMillis();

            try {
                Thread.sleep(sleepChunk);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long actualSleep = System.currentTimeMillis() - sleepStart;

            rowPause();

            elapsedTime += actualSleep;

            if (debugging) {
                walkedTime += actualSleep;
            }

            if (currentMoodDuration > 0) {
                currentMoodDuration -= actualSleep;
            } else {
                switchMood();
            }

            double progress = ((double) elapsedTime / totalTime) * 100;
            setHudMessage(AutofarmingClient.modData.getCurrentProfile().name + "\nRow progress: " + Math.round(progress)
                    + "%\nCurrent mood: "
                    + currentMood.NAME + "\nRow time: " + getTimeStringFromMillis(totalTime) + "\nElapsed row time: "
                    + getTimeStringFromMillis(elapsedTime) + "\nMood Time: "
                    + getTimeStringFromMillis(currentMoodDuration) + " \nMood overshoot: "
                    + getTimeStringFromMillis(moodOvershoot));
        }

        deactivateCurrentActions();
    }

    private void rowPause() {

        if (isPaused) {
            MouseLocker.unlockMouse();
            deactivateCurrentActions();
            handlePauseState();
            if (isActive) {
                activateCurrentActions();
                MouseLocker.lockMouse();
            }
        }
    }

    private void handleVoidDrop() {

        if (debugging)
            walkedTime += AutofarmingClient.modData.getCurrentProfile().voidDropTime;

        long elapsedVoid = 0;

        while (elapsedVoid < AutofarmingClient.modData.getCurrentProfile().voidDropTime && isActive) {
            long remaining_void = AutofarmingClient.modData.getCurrentProfile().voidDropTime - elapsedVoid;
            long sleep_chunk = Math.min(POLLING_INTERVAL, remaining_void);

            try {
                Thread.sleep(sleep_chunk);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            elapsedVoid += sleep_chunk;

            double progress = ((double) elapsedVoid / AutofarmingClient.modData.getCurrentProfile().voidDropTime) * 100;
            setHudMessage("Void drop: " + Math.round(progress) + "%");

            if (isPaused) {
                handlePauseState();
            }
        }
    }

    private void handlePauseState() {
        long pauseStart = System.currentTimeMillis();

        while (isActive && isPaused) {
            if (AutofarmingClient.modData.isShowPauseMessage())
                setHudMessage("PAUSED - Press " + PAUSE_TOGGLE.toString() + " to resume");

            try {
                Thread.sleep(POLLING_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (debugging)
                pausedTime += POLLING_INTERVAL;
        }

        if (debugging) {
            long actualPause = System.currentTimeMillis() - pauseStart;
            pausedTime += actualPause;
        }
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

        if (AutofarmingClient.modData.isForceAttentiveMood()) {
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

    private void layer_swap() {

        Actions[] actions = AutofarmingClient.modData.getCurrentProfile().actionsLayerSwap;

        Long[] deviation = getClickDeviation(actions.length * 2);

        for (int i = 0; i < actions.length; i++) {
            actions[i].activate();
            preciseSleep(deviation[i]);
        }

        if (debugging)
            walkedTime += AutofarmingClient.modData.getCurrentProfile().layerSwapTime;

        preciseSleep(AutofarmingClient.modData.getCurrentProfile().layerSwapTime);

        for (int i = 0; i < actions.length; i++) {
            actions[i].deactivate();
            preciseSleep(deviation[actions.length + i]);
        }
    }

    private void toggle_direction() {
        currentDirection = currentDirection == LEFT ? RIGHT : LEFT;
    }

    private Actions[] getCurrentDirectionActions() {
        return currentDirection == LEFT ? AutofarmingClient.modData.getCurrentProfile().actionsLeft
                : AutofarmingClient.modData.getCurrentProfile().actionsRight;
    }

    private long getCurrentRowClearTime() {
        return currentDirection == LEFT ? AutofarmingClient.modData.getCurrentProfile().leftRowClearTime
                : AutofarmingClient.modData.getCurrentProfile().rightRowClearTime;
    }

    private long getMoodOvershoot() {

        long overshoot = 0;

        if (AutofarmingClient.modData.isForceAttentiveMood() || currentMood.OVERSHOOT_DURATION == 0)
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
            if (mood == Mood.DISTRACTED && !AutofarmingClient.modData.getEnableDistracted())
                continue;

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

    private static void preciseSleep(long ms) {
        long start = System.nanoTime();
        long end = start + ms * 1_000_000;

        if (ms > 20) {
            try {
                Thread.sleep(ms - 15);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        while (System.nanoTime() < end) {
            Thread.onSpinWait();
        }
    }

    private String getTimeStringFromMillis(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long milliseconds = millis % 1000;

        // Format: m:ss,mmm
        return String.format("%d:%02d,%03d", minutes, seconds, milliseconds);
    }

    public static long Random(long min, long max) {
        if (min > max)
            throw new IllegalArgumentException("min must be <= max");

        return min + (long) (Math.random() * ((max - min) + 1));
    }

    public static int Random(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("min must be <= max");

        return min + (int) (Math.random() * ((max - min) + 1));
    }

}
