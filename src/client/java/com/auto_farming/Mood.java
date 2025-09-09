package com.auto_farming;

public enum Mood {
    ATTENTIVE("attentive", 0L, 0.0, 0L, 0L, 600000L, 720000L, 0.30, 0.0),
    INATTENTIVE("inattentive", 100L, 0.2, 5000L, 3000L, 600000L, 1080000L, 0.45, 0.0),
    DISTRACTED("distracted", 250L, 0.4, 10000L, 5000L, 300000L,540000L, 0.25, 0.5);
    

    private Mood(String name,long click_delay,double overshoot_chance,long overshoot_duration,long overshoot_duration_variable,long mood_min_duration,long mood_max_duration,double mood_chance,double click_delay_miss){
        this.name=name;
        this.click_delay=click_delay;
        this.overshoot_chance=overshoot_chance;
        this.overshoot_duration=overshoot_duration;
        this.overshoot_duration_variable=overshoot_duration_variable;
        this.mood_min_duration=mood_min_duration;
        this.mood_max_duration=mood_max_duration;
        this.mood_chance=mood_chance;
        this.click_delay_miss=click_delay_miss;
    }

    public final String name;
    public final long click_delay;
    public final double overshoot_chance;
    public final long overshoot_duration;
    public final long overshoot_duration_variable;
    public final long mood_min_duration;
    public final long mood_max_duration;
    public final double mood_chance;
    public final double click_delay_miss;
}