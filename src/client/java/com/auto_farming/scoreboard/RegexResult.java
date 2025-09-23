package com.auto_farming.scoreboard;

import java.util.regex.Matcher;

public class RegexResult {
    public final RegexPattern pattern;
    public final Matcher matcher;
    public final String result;
    public final boolean found;

    public RegexResult(RegexPattern pattern, String input) {
        this.pattern = pattern;
        matcher = pattern.getMatcher(input);
        found = matcher.find();
        if (found) {
            result = matcher.group();
        } else {
            result = null;
        }
    }
}
