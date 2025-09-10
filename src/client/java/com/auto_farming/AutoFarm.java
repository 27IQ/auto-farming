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
import com.auto_farming.chat.WarpCommands;
import com.auto_farming.config.ModData;
import com.auto_farming.moods.Mood;

public class AutoFarm {

    private ModData currentModData;

    // state
    public boolean isActive = false;
    private boolean isPaused = false;
    private Directions currentDirection = NONE;
    // settings
    private long pollingInterval = 100L;
    // not yet implemented
    // private boolean pause_protection= true;
    // moods
    private Mood currentMood = getNextMood();
    private long currentMoodDuration = 0L;
    // debugging
    private boolean debugging = false;
    private long addedTime = 0L;
    private long walkedTime = 0L;
    private long pausedTime = 0L;
    private long startTime = 0L;
    private long interval_1 = 0L;

    public AutoFarm(ModData currentMoData) {
        this.currentModData = currentMoData;
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

    public void autoSetUp() {
        WarpCommands.warpGarden();
        preciseSleep(Random(150, 200));
        SNEAK.activate();
        preciseSleep(Random(500, 1000));
        SNEAK.deactivate();
    }

    public void runFarm(Directions direction) {

        if (isActive)
            return;

        if (debugging) {
            startTime = System.currentTimeMillis();
            addedTime = 0L;
            walkedTime = 0L;
            pausedTime = 0L;
        }

        isActive = true;
        isPaused = false;
        MouseLocker.lockMouse();

        while (isActive) {

            currentDirection = direction;

            for (int i = 0; i < currentModData.getCurrentProfile().LAYER_COUNT; i++) {
                clearRow();

                if (!isActive)
                    break;

                if (currentModData.getCurrentProfile().LAYER_SWAP_TIME != 0)
                    layer_swap();

                toggle_direction();
            }

            handleVoidDrop();

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

        MouseLocker.unlockMouse();
    }

    private void clearRow() {

        long moodOvershoot = getMoodOvershoot();
        long totalTime = getCurrentRowClearTime() + Random(0, 250) + moodOvershoot;

        activateCurrentActions();

        long elapsedTime = 0L;

        while (elapsedTime < totalTime && isActive) {

            long remainingTime = totalTime - elapsedTime;
            long sleepChunk = Math.min(pollingInterval, remainingTime);

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
            setHudMessage(currentModData.getCurrentProfile().NAME + "\nRow progress: " + Math.round(progress)
                    + "%\nCurrent mood: "
                    + currentMood.NAME + "\nRow time: " + getTimeStringFromMillis(totalTime) + "\nElapsed row time: "
                    + getTimeStringFromMillis(elapsedTime) + "\nMood Time: "
                    + getTimeStringFromMillis(currentMoodDuration) + " \nMood overshoot: "
                    + getTimeStringFromMillis(moodOvershoot));
        }

        deactivateCurrentActions();
        setHudMessage("");
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
            walkedTime += currentModData.getCurrentProfile().VOID_DROP_TIME;

        long elapsedVoid = 0L;

        while (elapsedVoid < currentModData.getCurrentProfile().VOID_DROP_TIME && isActive) {
            long remaining_void = currentModData.getCurrentProfile().VOID_DROP_TIME - elapsedVoid;
            long sleep_chunk = Math.min(pollingInterval, remaining_void);

            try {
                Thread.sleep(sleep_chunk);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            elapsedVoid += sleep_chunk;

            double progress = ((double) elapsedVoid / currentModData.getCurrentProfile().VOID_DROP_TIME) * 100;
            setHudMessage("Void drop: " + Math.round(progress) + "%");

            if (isPaused) {
                handlePauseState();
            }
        }
    }

    private void handlePauseState() {
        long pauseStart = System.currentTimeMillis();

        while (isActive && isPaused) {
            if (currentModData.isShowPauseMessage())
                setHudMessage("PAUSED - Press " + PAUSE_TOGGLE.toString() + " to resume");

            try {
                Thread.sleep(pollingInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (debugging)
                pausedTime += pollingInterval;
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

        preciseSleep(10L);
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

        preciseSleep(10L);

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
            result.add(Random(0L, 70L));
        }

        return result.toArray(new Long[0]);
    }

    private long getMoodClickDelay() {
        long delay = currentMood.CLICK_DELAY;

        if (currentModData.isForceAttentiveMood()) {
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

        long pull = Random(0L, 1L);

        return pull == 1 ? true : false;
    }

    private void layer_swap() {

        Actions[] actions = currentModData.getCurrentProfile().ACTIONS_LAYER_SWAP;

        Long[] deviation = getClickDeviation(actions.length * 2);

        for (int i = 0; i < actions.length; i++) {
            actions[i].activate();
            preciseSleep(deviation[i]);
        }

        if (debugging)
            walkedTime += currentModData.getCurrentProfile().LAYER_SWAP_TIME;

        preciseSleep(currentModData.getCurrentProfile().LAYER_SWAP_TIME);

        for (int i = 0; i < actions.length; i++) {
            deactivateCurrentActions();
            preciseSleep(deviation[actions.length + i]);
        }
    }

    private void toggle_direction() {
        currentDirection = currentDirection == LEFT ? RIGHT : LEFT;
    }

    private Actions[] getCurrentDirectionActions() {
        return currentDirection == LEFT ? currentModData.getCurrentProfile().ACTIONS_LEFT
                : currentModData.getCurrentProfile().ACTIONS_RIGHT;
    }

    private long getCurrentRowClearTime() {
        return currentDirection == LEFT ? currentModData.getCurrentProfile().LEFT_ROW_CLEAR_TIME
                : currentModData.getCurrentProfile().RIGHT_ROW_CLEAR_TIME;
    }

    private long getMoodOvershoot() {

        long overshoot = 0L;

        if (currentModData.isForceAttentiveMood() || currentMood.OVERSHOOT_DURATION == 0)
            return overshoot;

        long roll = Random(0L, 1L);

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
            chances.add(mood.MOOD_CHANCE);
        }

        double currentThreshold = 0.0;
        int roll = Random(0, 1);

        int selectedMoodIndex = 0;

        for (int i = 0; i < chances.size(); i++) {
            currentThreshold += chances.get(i);

            if (selectedMoodIndex == 0 && currentThreshold <= roll) {
                selectedMoodIndex = i;
            }
        }

        return Mood.values()[selectedMoodIndex];
    }

    private void preciseSleep(long ms) {
        long start = System.nanoTime();
        long end = start + ms * 1_000_000L;

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

    public long Random(long min, long max) {
        if (min > max)
            throw new IllegalArgumentException("min must be <= max");

        return min + (long) (Math.random() * ((max - min) + 1));
    }

    public int Random(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("min must be <= max");

        return min + (int) (Math.random() * ((max - min) + 1));
    }

}
