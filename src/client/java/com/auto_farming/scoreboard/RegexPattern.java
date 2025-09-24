package com.auto_farming.scoreboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum RegexPattern {
    PEST_REPELLENT_MAX_PATTERN("Repellent: MAX\\s\\(\\d+[\\w\\s]+\\)"),
    PEST_REPELLENT_REGULAR_PATTERN("Repellent: Regular\\s\\(\\d+[\\w\\s]+\\)"),
    PEST_REPELLENT_NONE_PATTERN("Repellent: None");

    private final Pattern pattern;

    private RegexPattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Matcher getMatcher(String toMatch) {
        return this.pattern.matcher(toMatch);
    }
}
