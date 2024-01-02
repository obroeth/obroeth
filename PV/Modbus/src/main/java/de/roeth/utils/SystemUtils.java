package de.roeth.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SystemUtils {

    public static boolean VERBOSE = true;

    public static void debug(Object clazz, String msg) {
        if (VERBOSE)
            System.out.println(clazz.getClass().getName() + ": " + msg);
    }

    public static HttpURLConnection createGetConnection(URL obj) throws IOException {
        return createConnection(obj, "GET");
    }

    public static HttpURLConnection createPutConnection(URL obj) throws IOException {
        return createConnection(obj, "PUT");
    }

    public static HttpURLConnection createPostConnection(URL obj) throws IOException {
        return createConnection(obj, "POST");
    }

    private static HttpURLConnection createConnection(URL obj, String method) throws IOException {
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "text/plain");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        return con;
    }

    public static boolean isTimeToday(long time) {
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar cachedDate = new GregorianCalendar();
        cachedDate.setTime(new Date(time));
        return today.get(Calendar.DAY_OF_MONTH) == cachedDate.get(Calendar.DAY_OF_MONTH);
    }

}
