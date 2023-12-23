package de.roeth.communication;

import de.roeth.PVSystem;
import de.roeth.model.Entity;
import de.roeth.model.EntityInfo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class OpenHabIO {

    public static void pushToOpenhab(PVSystem pvSystem) throws IOException {
        pushToOpenhab(pvSystem.deye);
        pushToOpenhab(pvSystem.solax);
        pushToOpenhab(pvSystem.sum);
        pushToOpenhab(pvSystem.evTracker);
//        pushToOpenhab(pvSystem.sm);
    }

    public static void pushToOpenhab(Entity entity) throws IOException {
        ArrayList<EntityInfo> infos = entity.snapshotInfo();
        for (EntityInfo info : infos) {
            curl(entity.name, info);
        }
    }

//    public static void pushToOpenhab(EVTracker evTracker) throws IOException {
//        curl("ev_station", evTracker.evStatus ? "ON" : "OFF");
//    }

    private static void curl(String prefix, EntityInfo info) throws IOException {
        String url = "http://192.168.178.22:8080/rest/items/" + prefix + "_" + info.name;
        HttpURLConnection con = getHttpURLConnection(info.pretty, new URL(url));
        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("POST Request failed. Tried to send name: " + info.name + " and data: " + info.pretty);
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
