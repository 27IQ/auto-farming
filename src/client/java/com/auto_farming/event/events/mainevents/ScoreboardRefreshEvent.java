package com.auto_farming.event.events.mainevents;

import java.util.List;

import com.auto_farming.event.events.basicevents.BasicEvent;
import com.auto_farming.scoreboard.RegexResult;

public class ScoreboardRefreshEvent extends BasicEvent {

    protected List<RegexResult> result;

    public ScoreboardRefreshEvent(List<RegexResult> result) {
        super();
        setName(this.getClass().getName());
        this.result = result;
    }

    public List<RegexResult> getResult() {
        return result;
    }
}