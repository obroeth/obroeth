package de.roeth.communication;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

    public static JSONObject createJsonObject(String file) throws IOException {
        try (InputStream stream = new FileInputStream(file)) {
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(json);
        }
    }

    public static Date getTodayMidnight() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        return today.getTime();
    }

    public static Date getStartOfToday() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 5);
        today.set(Calendar.HOUR_OF_DAY, 0);
        return today.getTime();
    }

    public static Date getEndOfToday() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 55);
        today.set(Calendar.HOUR_OF_DAY, 23);
        return today.getTime();
    }

    public static Date getEndOfTomorrow() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.HOUR_OF_DAY, 23);
        GregorianCalendar tomorrow = new GregorianCalendar();
        tomorrow.setTime(new Date(today.getTime().getTime() + 120000));
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MINUTE, 55);
        tomorrow.set(Calendar.HOUR_OF_DAY, 23);
        return tomorrow.getTime();
    }

    public static Date getStartOfThisMonth() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 5);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.DAY_OF_MONTH, 1);
        return today.getTime();
    }

    public static Date getEndOfThisMonth() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 55);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DAY_OF_MONTH));
        return today.getTime();
    }

    public static Date getStartOfThisYear() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 5);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.MONTH, 0);
        return today.getTime();
    }

    public static Date getEndOfThisYear() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 55);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MONTH, 11);
        today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DAY_OF_MONTH));
        return today.getTime();
    }

}
