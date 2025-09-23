package com.auto_farming.event;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;

import com.auto_farming.event.events.basicevents.BasicEvent;

public class EventGrouper {

    private static final Map<String, Set<Class<? extends BasicEvent>>> CACHE = new ConcurrentHashMap<>();

    public static Set<Class<? extends BasicEvent>> getMainEvents() {
        return getClasses("com.auto_farming.event.events.mainevents");
    }

    public static Set<Class<? extends BasicEvent>> getScoreboardEvents() {
        return getClasses("com.auto_farming.event.events.scoreboardevents");
    }

    private static Set<Class<? extends BasicEvent>> getClasses(String packageName) {
        return CACHE.computeIfAbsent(packageName, pkg -> {
            Reflections reflections = new Reflections(pkg);
            return Collections.unmodifiableSet(
                    reflections.getSubTypesOf(BasicEvent.class).stream()
                            .filter(c -> !c.getPackageName().startsWith(
                                    "com.auto_farming.event.events.basicevents"))
                            .collect(java.util.stream.Collectors.toSet()));
        });
    }
}
