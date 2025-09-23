package com.auto_farming.scoreboard;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.auto_farming.event.EventManager;
import com.auto_farming.event.events.mainevents.ScoreboardRefreshEvent;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class Scoreboard {

    private static int counter = 0;

    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (counter < 100) {
                counter++;
                return;
            }

            counter = 0;
            EventManager.trigger(new ScoreboardRefreshEvent(parseScoreboard()));
        });
    }

    public static ConcurrentHashMap<RegexPattern, RegexResult> parseScoreboard() {
        String currentBoard = getScoreboardSnapshot();
        ConcurrentHashMap<RegexPattern, RegexResult> results = new ConcurrentHashMap<>();

        for (RegexPattern pattern : RegexPattern.values()) {
            results.put(pattern, new RegexResult(pattern, currentBoard));
        }

        return results;
    }

    public static String getScoreboardSnapshot() {

        String result = "";
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.getNetworkHandler() == null)
            return result;

        Collection<PlayerListEntry> entries = client.getNetworkHandler().getPlayerList();

        for (PlayerListEntry entry : entries) {
            Text currentText;
            if (entry == null || (currentText = entry.getDisplayName()) == null)
                continue;

            result += currentText.getString();
        }

        return result;
    }
}
