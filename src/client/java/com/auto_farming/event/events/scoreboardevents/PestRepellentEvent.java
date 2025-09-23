package com.auto_farming.event.events.scoreboardevents;

import java.util.ArrayList;

import com.auto_farming.event.events.basicevents.BasicScoreboardEvent;
import com.auto_farming.scoreboard.RegexResult;

public class PestRepellentEvent extends BasicScoreboardEvent{

	public PestRepellentEvent(ArrayList<RegexResult> result) {
		super(result);
        setName(this.getClass().getName());
	}
}
