package com.auto_farming.event.events.basicevents;

import java.util.ArrayList;
import java.util.List;

import com.auto_farming.scoreboard.RegexResult;

public class BasicScoreboardEvent extends BasicEvent{
    private List<RegexResult> result;

    public BasicScoreboardEvent(ArrayList<RegexResult> result) {
        super();
        this.result = result;
    }

    public List<RegexResult> getResult() {
        return result;
    }
}
