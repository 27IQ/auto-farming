package com.auto_farming.farminglogic;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.gui.AlertHUD;
import com.auto_farming.gui.TopStatusHUD;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class BlockBreakDetection {
    private static int blockCounter = 0;
    private static int xpSoundCounter = 0;
    private static int tickCounterDetect;
    private static Queue<Integer> bpsBlockQueue = new ConcurrentLinkedQueue<>();
    private static Queue<Integer> bpsSoundQueue = new ConcurrentLinkedQueue<>();
    private static boolean isActive;
    private static int bufferSize;
    private static int minimumBps;
    private static boolean isRampedUp;

    public static void incrementBlockCounter() {
        blockCounter++;
    }

    public static void incrementXpSoundCounter() {
        xpSoundCounter++;
    }

    public static void startDetection(int bufferSize, int minimumBps) {
        blockCounter = 0;
        xpSoundCounter = 0;
        tickCounterDetect = 0;
        BlockBreakDetection.minimumBps = minimumBps;
        BlockBreakDetection.bufferSize = bufferSize;
        isActive = false;
        TopStatusHUD.setEnabled(true);
        isRampedUp = false;
        clearQueue();
    }

    public static void stopDetection() {
        isActive = false;
        TopStatusHUD.setEnabled(false);
    }

    public static void pause() {
        isActive = false;
    }

    public static void unpause() {
        isActive = true;
    }

    private static void registerDetectBlockBreakingLoss() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounterDetect++;

            if (tickCounterDetect < 20)
                return;

            tickCounterDetect = 0;

            Optional<AutoFarm> autoFarm = AutoFarmHolder.get();

            if (!isActive || autoFarm.isEmpty() || autoFarm.get().isPaused())
                return;

            bpsBlockQueue.offer(blockCounter);
            bpsSoundQueue.offer(xpSoundCounter);
            blockCounter = 0;
            xpSoundCounter = 0;

            if (bpsBlockQueue.size() <= bufferSize && bpsSoundQueue.size() <= bufferSize)
                return;

            bpsBlockQueue.poll();
            bpsSoundQueue.poll();

            double blockSum = 0;
            double xpSum = 0;

            for (Integer bps : bpsBlockQueue) {
                blockSum += bps;
            }

            for (Integer bps : bpsSoundQueue) {
                xpSum += bps;
            }

            double blockAvg = blockSum / bufferSize;
            double xpAvg = xpSum / bufferSize;

            if (!isRampedUp && (blockAvg >= minimumBps && xpAvg >= minimumBps)) {
                isRampedUp = true;
                AutofarmingClient.LOGGER.info("Detection has ramped up! " + blockAvg + "/" + xpAvg);
                AlertHUD.setMessage("Detection has ramped up!", 2000);
            } else if (!isRampedUp) {
                TopStatusHUD.setMessage("Ramping ... " + blockAvg + "/" + xpAvg);
                return;
            }

            if (isRampedUp && (blockAvg < minimumBps || xpAvg < minimumBps)) {
                isRampedUp = false;
                AutoFarmHolder.get().ifPresent((farm) -> {
                    AutofarmingClient.LOGGER
                            .error("bps error :" + blockAvg + "/" + xpAvg);
                    farm.queueDisrupt(new FarmingDisrupt(
                            "You are breaking no Blocks!\nResume with " + PAUSE_TOGGLE.toString()));
                });
            }

            TopStatusHUD.setMessage("Blocks/s: " + blockAvg + "/" + xpAvg);
        });
    }

    public static void clearQueue() {
        bpsBlockQueue.clear();
        bpsSoundQueue.clear();
    }

    public static void register() {
        registerDetectBlockBreakingLoss();
    }
}
