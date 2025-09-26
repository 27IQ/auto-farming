package com.auto_farming.moods;

import static com.auto_farming.misc.RandNumberHelper.Random;

import java.util.ArrayList;
import java.util.List;

public enum Mood {
    ATTENTIVE("attentive", 0L, 0.0, 0L, 0L, 600000L, 720000L, 0.30, 0.0),
    INATTENTIVE("inattentive", 100L, 0.2, 5000L, 3000L, 600000L, 1080000L, 0.45, 0.0),
    DISTRACTED("distracted", 250L, 0.4, 10000L, 5000L, 300000L, 540000L, 0.25, 0.5);

    private Mood(String name, long clickDelay, double overshootChance, long overshootDuration,
            long overshootDurationVariable, long moodMinDuration, long moodMaxDuration, double moodChance,
            double clickDelayMissChance) {
        this.NAME = name;
        this.CLICK_DELAY = clickDelay;
        this.OVERSHOOT_CHANCE = overshootChance;
        this.OVERSHOOT_DURATION = overshootDuration;
        this.OVERSHOOT_DURATION_VARIABLE = overshootDurationVariable;
        this.MOOD_MIN_DURATION = moodMinDuration;
        this.MOOD_MAX_DURATION = moodMaxDuration;
        this.MOOD_CHANCE = moodChance;
        this.CLICK_DELAY_MISS_CHANCE = clickDelayMissChance;
    }

    public final String NAME;
    public final long CLICK_DELAY;
    public final double OVERSHOOT_CHANCE;
    public final long OVERSHOOT_DURATION;
    public final long OVERSHOOT_DURATION_VARIABLE;
    public final long MOOD_MIN_DURATION;
    public final long MOOD_MAX_DURATION;
    public final double MOOD_CHANCE;
    public final double CLICK_DELAY_MISS_CHANCE;

    public static Mood getRandomMood(boolean enableDistracted) {
        List<Double> chances = new ArrayList<>();

        for (Mood mood : Mood.values()) {
            if (mood == Mood.DISTRACTED && !enableDistracted) {
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

    public long getRandomMoodOvershoot(boolean forceAttentive) {

        long overshoot = 0;

        if (forceAttentive || OVERSHOOT_DURATION == 0)
            return overshoot;

        long roll = Random(0, 1);

        if (OVERSHOOT_CHANCE <= roll) {
            long minDur = OVERSHOOT_DURATION - OVERSHOOT_DURATION_VARIABLE;
            long maxDur = OVERSHOOT_DURATION + OVERSHOOT_DURATION_VARIABLE;
            overshoot = Random(minDur, maxDur);
        }

        return overshoot;
    }

    public long getRandomMoodDuration() {
        return Random(MOOD_MIN_DURATION, MOOD_MAX_DURATION);
    }

    public boolean getRandomClickDelayMiss() {
        return Math.random() >= CLICK_DELAY_MISS_CHANCE;
    }
}