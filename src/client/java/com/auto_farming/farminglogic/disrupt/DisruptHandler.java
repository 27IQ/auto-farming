package com.auto_farming.farminglogic.disrupt;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.event.EventManager;
import com.auto_farming.event.events.mainevents.AfterDisruptEvent;
import com.auto_farming.event.events.mainevents.ForcePauseHandleEvent;
import com.auto_farming.farminglogic.waiter.Waiter;
import com.auto_farming.gui.StatusHUD;
import com.auto_farming.sounds.SoundAlert;

public abstract class DisruptHandler extends Waiter {

    protected final ConcurrentLinkedQueue<Disrupt> disruptQueue;
    private static boolean nextDisrupt = false;

    protected DisruptHandler() {
        this.disruptQueue = new ConcurrentLinkedQueue<>();
    }

    public void nextDisrupt() {
        nextDisrupt = true;
    }

    public void queueDisruptIfAbesent(Disrupt disrupt) {
        if (disruptQueue.contains(disrupt)
                || ((System.nanoTime() - disrupt.getLastsuccess()) / 1_000_000 < Disrupt.COOLDOWN))
            return;

        AutofarmingClient.LOGGER.info("Queuing disrupt: " + disrupt.getMessage());
        disruptQueue.offer(disrupt);
    }

    public void handleDisrupts() {

        if (Thread.interrupted() || disruptQueue.isEmpty())
            return;

        SoundAlert.MAMBO_ALERT.play();

        HashSet<Disrupt> executedDisrupts = new HashSet<>();

        while (!Thread.interrupted() && !disruptQueue.isEmpty()) {
            nextDisrupt = false;
            Disrupt currentDisrupt = disruptQueue.peek();

            StatusHUD.setMessage(currentDisrupt.getMessage());

            while (!Thread.interrupted() && !nextDisrupt) {
                waitFor(POLLING_INTERVAL);
            }

            currentDisrupt.setLastSuccess(System.nanoTime());
            executedDisrupts.add(disruptQueue.poll());
            EventManager.trigger(new ForcePauseHandleEvent());
        }
        SoundAlert.MAMBO_ALERT.stop();
        EventManager.trigger(new AfterDisruptEvent(executedDisrupts));
    }

}
