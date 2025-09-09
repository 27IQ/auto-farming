package com.auto_farming;

import static com.auto_farming.actionwrapper.Actions.LEFT_CLICK;
import static com.auto_farming.actionwrapper.Directions.LEFT;
import static com.auto_farming.actionwrapper.Directions.NONE;
import static com.auto_farming.actionwrapper.Directions.RIGHT;

import java.util.ArrayList;
import java.util.List;

import static com.auto_farming.gui.BasicHUD.setHudMessage;
import static com.auto_farming.gui.Alert.setAlertMessage;

import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.actionwrapper.Directions;
import com.auto_farming.farmprofiles.Profile;
import com.auto_farming.moods.Mood;

public class AutoFarm {
    //profile
    private static Profile current_profile=Profile.values()[0];
    //state
    public static boolean is_active= false;
    private static boolean is_paused= false;
    private static Directions current_direction= NONE;
    //settings
    private static long polling_interval= 100L;
    public static boolean show_pause_Message= true;
    //not yet implemented
    //private static boolean pause_protection= true;
    //moods
    private static Mood current_mood= get_next_mood(); 
    private static long current_mood_duration= 0L;
    public static boolean force_attentive_mood= false;
    // debugging
    private static boolean debugging= false;
    private static long added_time= 0L;
    private static long walked_time= 0L;
    private static long paused_time= 0L;
    private static long start_time= 0L;
    private static long interval_1= 0L;

    public static void setCurrent_profile(Profile current_profile) {
        if(is_active){
            setAlertMessage("please deactivate the running profile first");
            return;
        }
    
        AutoFarm.current_profile = current_profile;
    }

    public static Profile getCurrent_profile() {
        return current_profile;
    }

    public static void pause_toggle(){

        if (!is_active)
            return;

        is_paused = !is_paused;
    }

    public static void run_farm(Directions direction) {

        if(is_active)
            return;

        if (debugging) {
            start_time = System.currentTimeMillis();
            added_time = 0L;
            walked_time = 0L;
            paused_time = 0L;
        }

        current_direction=direction;
        is_active = true;
        is_paused = false;

        while (is_active) {

            for (int i=0;i<current_profile.layer_count;i++) {
                clear_row();

                if (!is_active)
                    return;

                if (current_profile.layer_swap_time != 0)
                    layer_swap();

                toggle_direction();
            }

            handle_void_drop();

            if (debugging) {
                interval_1 = System.currentTimeMillis();
                long interval_duration = interval_1 - start_time;

                AutofarmingClient.LOGGER.info(
                    "added_time: " +added_time +
                    "\nwalked_time: "+ walked_time +
                    "\npaused_time: " +paused_time+
                    "\nstart_time: "+start_time+
                    "\ninterval_time: "+ interval_1+
                    "\ninterval_duration: "+ interval_duration);
            }
        }
    }

    private static void clear_row() {

        long mood_overshoot=get_mood_overshoot();
        long total_time = get_current_row_clear_time() + Random(0, 250) + mood_overshoot;

        activate_current_Actions();

        long elapsed_time = 0L;

        while (elapsed_time < total_time && is_active) {

            long remaining_time = total_time - elapsed_time;
            long sleep_chunk = Math.min(polling_interval, remaining_time);

            row_pause();

            long sleep_start = System.currentTimeMillis();

            try {
                Thread.sleep(sleep_chunk);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long actual_sleep = System.currentTimeMillis() - sleep_start;

            row_pause();

            elapsed_time += actual_sleep;

            if (debugging) {
                walked_time += actual_sleep;
            }

            if (current_mood_duration > 0) {
                current_mood_duration -= actual_sleep;
            }else{
                switch_mood();
            }

            setHudMessage(current_profile.name+"\nRow progress: " + Math.round((elapsed_time / total_time) * 100) + "%\nCurrent mood: " +current_mood.name+ "\nRow time: "+ getTimeStringFromMillis(total_time) + "\nElapsed row time: " +getTimeStringFromMillis(elapsed_time)+ "\nMood Time: " +getTimeStringFromMillis(current_mood_duration) +" \nMood overshoot: "+ getTimeStringFromMillis(mood_overshoot));
        }

        deactivate_current_Actions();
        setHudMessage("");
    }

    private static void row_pause(){

        if (is_paused) {
            deactivate_current_Actions();
            handle_pause_state();
            if (is_active)
                activate_current_Actions();
        }
    }

    private static void handle_void_drop() {

        if (debugging)
            walked_time += current_profile.void_drop_time;

        long elapsed_void = 0L;

        while (elapsed_void < current_profile.void_drop_time && is_active) {
            long remaining_void = current_profile.void_drop_time - elapsed_void;
            long sleep_chunk = Math.min(polling_interval, remaining_void);

            try {
                Thread.sleep( sleep_chunk);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            elapsed_void += sleep_chunk;

            if (debugging)
                setHudMessage("Void drop: " + Math.round((elapsed_void / current_profile.void_drop_time) * 100) + "%");

            if (is_paused) {
                handle_pause_state();
            }
        }

        setHudMessage("");
    }

    private static void handle_pause_state() {
        long pause_start = System.currentTimeMillis();

        while (is_active && is_paused) {
            if (show_pause_Message)
                setHudMessage("PAUSED - Press F8 to resume");

                try {
                    Thread.sleep(polling_interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            if (debugging)
                paused_time += polling_interval;
        }

        if (debugging) {
            long actual_pause = System.currentTimeMillis() - pause_start;
            paused_time += actual_pause;
        }
    }

    private static void activate_current_Actions(){
        Long[] deviation = get_click_deviation(get_current_direction_actions().length);
        boolean miss = get_click_delay_miss();
        Actions[] current_action_order = get_current_action_order();

        preciseSleep(10L);
        LEFT_CLICK.activate();
        preciseSleep((miss ? 0 : 500) + deviation[0]);

        for (int i=0; i<deviation.length - 1;i++) {
            current_action_order[i].activate();
            preciseSleep(deviation[i + 1]);
        }
    }

    private static void deactivate_current_Actions(){
        Long[] deviation = get_click_deviation(get_current_direction_actions().length);
        Actions[] current_action_order = get_current_action_order();

        preciseSleep(10L);

        for (int i=0;i<deviation.length - 1;i++) {
            current_action_order[i].deactivate();
            preciseSleep(deviation[i]);
        }

        LEFT_CLICK.deactivate();
        preciseSleep(deviation[deviation.length - 1]);
    }

    private static Long[] get_click_deviation(int count) {

        int rand = Random(50, 100);
        long mood_click_delay=getMoodClickDelay();

        List<Long> result = new ArrayList<>();
        result.add(rand + mood_click_delay);

        for(int i=0;i<count;i++) {
            result.add(Random(0L, 70L));
        }

        return result.toArray(new Long[0]);
    }

    private static long getMoodClickDelay(){
        long delay=current_mood.click_delay;

        if(force_attentive_mood){
            delay=Mood.ATTENTIVE.click_delay;
        }

        return delay;
    }

    private static Actions[] get_current_action_order() {

        int min = 0;
        Actions[] current_actions = get_current_direction_actions();
        int max = current_actions.length-1;

        List<Actions> results = new ArrayList<>();

        while (results.size() < current_actions.length) {
            int  pull = Random(min, max);

            if (results.size() == 0) {
                results.add(current_actions[pull]);
                continue;
            }

            boolean found_flag=false;

            for (Actions action : results) {
                if (action == current_actions[pull]) 
                    found_flag=true;
            }

            if(!found_flag)
                results.add(current_actions[pull]);
        }

        return results.toArray(new Actions[0]);
    }

    private static boolean get_click_delay_miss() {

        long pull = Random(0L, 1L);

        return pull == 1 ? true : false;
    }

    private static void layer_swap() {

        Actions[] actions = current_profile.actions_layer_swap;

        Long[] deviation = get_click_deviation(actions.length * 2);

        for (int i=0;i<actions.length;i++) {
            actions[i].activate();
            preciseSleep(deviation[i]);
        }

        if (debugging)
            walked_time += current_profile.layer_swap_time;

        preciseSleep(current_profile.layer_swap_time);

        for (int i=0;i<actions.length;i++) {
            deactivate_current_Actions();
            preciseSleep(deviation[actions.length + i]);
        }
    }

    private static void toggle_direction() {
        current_direction = current_direction == LEFT ? RIGHT : LEFT;
    }

    private static Actions[] get_current_direction_actions() {
        return current_direction == LEFT ? current_profile.actions_left : current_profile.actions_right;
    }

    private static long get_current_row_clear_time() {
        return current_direction == LEFT ? current_profile.left_row_clear_time : current_profile.right_row_clear_time;
    }

    private static long get_mood_overshoot() {

        long overshoot = 0L;

        if (force_attentive_mood || current_mood.overshoot_duration == 0)
            return overshoot;

        long roll = Random(0L, 1L);

        if (current_mood.overshoot_chance <= roll) {
            long min_dur = current_mood.overshoot_duration - current_mood.overshoot_duration_variable;
            long max_dur = current_mood.overshoot_duration + current_mood.overshoot_duration_variable;
            overshoot = Random(min_dur, max_dur);
        }

        return overshoot;
    }

    private static void switch_mood() {
        Mood new_mood = get_next_mood();

        current_mood = new_mood;
        current_mood_duration = Random(current_mood.mood_min_duration, current_mood.mood_max_duration);
    }

    private static Mood get_next_mood() {
        List<Double> chances=new ArrayList<>();

        for (Mood mood : Mood.values()) {
            chances.add(mood.mood_chance);
        }

        double current_threshold = 0.0;
        int roll = Random(0, 1);

        int selected_mood_index = 0;

        for (int i=0;i<chances.size();i++) {
            current_threshold += chances.get(i);

            if (selected_mood_index == 0 && current_threshold <= roll) {
                selected_mood_index = i;
            }
        }

        return Mood.values()[selected_mood_index];
    }

    private static void preciseSleep(long ms) {
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

    private static String getTimeStringFromMillis(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long milliseconds = millis % 1000;

        // Format: m:ss,mmm
        return String.format("%d:%02d,%03d", minutes, seconds, milliseconds);
    }

    public static long Random(long min, long max){
        if (min > max) 
            throw new IllegalArgumentException("min must be <= max");

        return min + (long)(Math.random() * ((max - min) + 1));
    }

    public static int Random(int min, int max){
        if (min > max) 
            throw new IllegalArgumentException("min must be <= max");
            
        return min + (int)(Math.random() * ((max - min) + 1));
    }
        
}
