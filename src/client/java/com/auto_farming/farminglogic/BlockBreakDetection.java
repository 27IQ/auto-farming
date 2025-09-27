package com.auto_farming.farminglogic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;

import com.auto_farming.gui.TopStatusHUD;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class BlockBreakDetection {
    private static int counter = 0;
    private static int tickCounterBps = 0;
    private static int tickCounterDetect = 0;
    private static Queue<Integer> bpsQueue = new ConcurrentLinkedQueue<>();
    private static boolean isActive = false;

    public static void incrementCounter() {
        counter++;
    }

    private static void registerCalculateBlockPerSecond() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounterBps++;

            if (tickCounterBps < 20)
                return;

            tickCounterBps = 0;
            TopStatusHUD.setMessage("Block/s: " + counter);
        });
    }

    public static final int MAXIMUM_LOSS_DURATION = 7; // seconds
    public static final int MINIMUM_BPS = 10;

    public static void startDetection() {
        counter = 0;
        isActive = true;
    }

    public static void stopDetection() {
        isActive = false;
    }

    private static void registerDetectBlockBreakingLoss() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounterDetect++;

            if (tickCounterDetect < 20)
                return;

            tickCounterDetect = 0;

            if (!isActive)
                return;

            bpsQueue.offer(counter);
            counter = 0;

            if (bpsQueue.size() <= MAXIMUM_LOSS_DURATION)
                return;

            bpsQueue.poll();

            double sum = 0;

            for (Integer integer : bpsQueue) {
                sum += integer;
            }

            if ((sum / MAXIMUM_LOSS_DURATION) < MINIMUM_BPS) {
                AutoFarmHolder.get().ifPresent((farm) -> {
                    farm.queueDisrupt(new FarmingDisrupt(
                            "You are breaking no Blocks!\nResume with " + PAUSE_TOGGLE.toString()));
                });
            }
        });
    }

    public static void clearBpsQueue() {
        bpsQueue.clear();
    }

    public static void register() {
        registerCalculateBlockPerSecond();
        registerDetectBlockBreakingLoss();
    }
}
