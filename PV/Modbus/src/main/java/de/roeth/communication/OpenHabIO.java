package de.roeth.communication;

import de.roeth.model.EVTracker;
import de.roeth.model.Entity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenHabIO {

    public static void pushToOpenhab(Entity entity) throws IOException {
        for (int i = 0; i < entity.getPropertyLength(); i++) {
            curl(entity.getPropertyName(i), entity.getPropertyPrettyValue(i));
        }
    }

    public static void pushToOpenhab(EVTracker evTracker) throws IOException {
        curl("ev_station", evTracker.evStatus ? "ON" : "OFF");
    }

    private static void curl(String name, String data) throws IOException {
        String url = "http://192.168.178.22:8080/rest/items/" + name;
        HttpURLConnection con = getHttpURLConnection(data, new URL(url));
        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("POST Request failed. Tried to send name: " + name + " and data: " + data);
        }
    }

    @NotNull
    private static HttpURLConnection getHttpURLConnection(String data, URL obj) throws IOException {
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/plain");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return con;
    }
}
