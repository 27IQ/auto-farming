package com.auto_farming.scoreboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum RegexPattern {
    PEST_REPELLENT_MAX("Repellent: MAX\\s\\(\\d+[\\w\\s]+\\)"),
    PEST_REPELLENT_REGULAR("Repellent: Regular\\s\\(\\d+[\\w\\s]+\\)"),
    PEST_REPELLENT_NONE("Repellent: None");

    private final Pattern pattern;

    private RegexPattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Matcher getMatcher(String toMatch) {
        return this.pattern.matcher(toMatch);
    }
}
