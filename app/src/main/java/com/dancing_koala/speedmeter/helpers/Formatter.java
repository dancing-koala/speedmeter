package com.dancing_koala.speedmeter.helpers;

import java.util.Locale;
import java.util.Map;

/**
 * This class contains methods simplifying data formatting
 */
public class Formatter {

    /**
     * Formats a speed in meters per second into a speed in kilometers per hours.
     *
     * @param metersPerSecond The speed to format
     * @return The speed formatted in kilometers per hour
     */
    public static String getKilometersPerHour(float metersPerSecond) {
        return String.format(Locale.FRANCE, "%.01f km/h", metersPerSecond * 3600f / 1000);
    }

    /**
     * Formats a distance in meters into a distance in kilometers if the distance is
     * bigger than 999 meters, else it is formatted in meters.
     *
     * @param meters The distance to format
     * @return The distance formatted into meters or kilometers
     */
    public static String getFormattedDistance(float meters) {
        String formattedDistance;

        if (meters > 999) {
            formattedDistance = String.format(Locale.FRANCE, "%.01f km", Math.ceil(meters) / 1000f);
        } else {
            formattedDistance = String.format(Locale.FRANCE, "%d m", Math.round(meters));
        }

        return formattedDistance;
    }


    /**
     * Formats a time in milliseconds into a time in hours, minutes and seconds (hh:mm:ss)
     *
     * @param millis The time in milliseconds to format
     * @return The formatted time in hours, minutes and seconds (hh:mm:ss)
     */
    public static String getFormattedTime(long millis) {
        // Milliseconds to seconds
        int seconds = (int) (millis / 1000);

        int hours = (seconds < 3600) ? 0 : (seconds - (seconds % 60)) / 60;

        seconds -= hours * 3600;

        int minutes = (seconds < 60) ? 0 : (seconds - (seconds % 60)) / 60;

        seconds -= minutes * 60;

        return String.format(Locale.FRANCE, "%02d:%02d:%02d", hours, minutes, seconds);
    }

}
