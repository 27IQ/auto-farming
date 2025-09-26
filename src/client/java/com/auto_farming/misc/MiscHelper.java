package com.auto_farming.misc;

public class MiscHelper {
    public static String getTimeStringFromMillis(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long milliseconds = millis % 1000;

        // Format: m:ss,mmm
        return String.format("%d:%02d,%03d", minutes, seconds, milliseconds);
    }
}
