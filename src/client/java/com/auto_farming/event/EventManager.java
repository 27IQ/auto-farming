package com.auto_farming.event;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.event.annotations.Event;
import com.auto_farming.event.events.basicevents.BasicEvent;

import java.lang.reflect.Method;
import java.util.*;

public class EventManager {
    private static final Map<String, List<Method>> listeners = new HashMap<>();
    private static final Map<Method, Object> instances = new HashMap<>();

    public static void scanAndRegister(String basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.MethodsAnnotated);

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Event.class);

        for (Method method : methods) {
            Event ann = method.getAnnotation(Event.class);
            String event = ann.value().getName();

            try {
                Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(method);
                instances.put(method, instance);
            } catch (Exception e) {
                AutofarmingClient.LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static void trigger(BasicEvent event) {
        List<Method> methods = listeners.get(event.getName());
        if (methods != null) {
            for (Method method : methods) {
                try {
                    if (method.getParameterCount() == 1
                            && method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
                        method.invoke(instances.get(method), event);

                } catch (Exception e) {
                    AutofarmingClient.LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }
}
