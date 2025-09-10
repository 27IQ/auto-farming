package com.auto_farming.moods;

public enum Mood {
    ATTENTIVE("attentive", 0L, 0.0, 0L, 0L, 600000L, 720000L, 0.30, 0.0),
    INATTENTIVE("inattentive", 100L, 0.2, 5000L, 3000L, 600000L, 1080000L, 0.45, 0.0),
    DISTRACTED("distracted", 250L, 0.4, 10000L, 5000L, 300000L, 540000L, 0.25, 0.5);

    private Mood(String name, long clickDelay, double overshootChance, long overshootDuration,
            long overshootDurationVariable, long moodMinDuration, long moodMaxDuration, double moodChance,
            double clickDelayMiss) {
        this.NAME = name;
        this.CLICK_DELAY = clickDelay;
        this.OVERSHOOT_CHANCE = overshootChance;
        this.OVERSHOOT_DURATION = overshootDuration;
        this.OVERSHOOT_DURATION_VARIABLE = overshootDurationVariable;
        this.MOOD_MIN_DURATION = moodMinDuration;
        this.MOOD_MAX_DURATION = moodMaxDuration;
        this.MOOD_CHANCE = moodChance;
        this.CLICK_DELAY_MISS = clickDelayMiss;
    }

    public final String NAME;
    public final long CLICK_DELAY;
    public final double OVERSHOOT_CHANCE;
    public final long OVERSHOOT_DURATION;
    public final long OVERSHOOT_DURATION_VARIABLE;
    public final long MOOD_MIN_DURATION;
    public final long MOOD_MAX_DURATION;
    public final double MOOD_CHANCE;
    public final double CLICK_DELAY_MISS;
}