package com.auto_farming.scoreboard;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum RegexPattern {
    PEST_REPELLENT_MAX("Repellent: MAX\\s\\(\\d+[\\w\\s]+\\)"),
    PEST_REPELLENT_REGULAR("Repellent: Regular\\s\\(\\d+[\\w\\s]+\\)"),
    PEST_REPELLENT_NONE("Repellent: None");

    private final Pattern pattern;

    private RegexPattern(String regex){
        this.pattern = Pattern.compile(regex);
    }

    public Matcher getMatcher(String toMatch){
        return this.pattern.matcher(toMatch);
    }

    public Optional<RegexResult> getResult(List<RegexResult> results){
        List<RegexResult> matchingResult=results.stream().filter((result)->result.pattern==this).collect(Collectors.toList());

        if(matchingResult.size()!=1)
            return Optional.empty();

        return Optional.of(matchingResult.get(0));
    }
}
