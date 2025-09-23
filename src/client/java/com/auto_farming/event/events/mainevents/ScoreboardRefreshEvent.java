package com.auto_farming.event.events.mainevents;

import java.util.concurrent.ConcurrentHashMap;

import com.auto_farming.event.events.basicevents.BasicScoreboardEvent;
import com.auto_farming.scoreboard.RegexPattern;
import com.auto_farming.scoreboard.RegexResult;

public class ScoreboardRefreshEvent extends BasicScoreboardEvent {

    protected ConcurrentHashMap<RegexPattern, RegexResult> result;

    public ScoreboardRefreshEvent(ConcurrentHashMap<RegexPattern, RegexResult> result) {
        super(result);
        setName(this.getClass().getName());
        this.result = result;
    }

    public ConcurrentHashMap<RegexPattern, RegexResult> getResult() {
        return result;
    }
}