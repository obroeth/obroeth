package de.roeth.model.devices;

import de.roeth.model.Entity;
import de.roeth.model.EntityInfo;
import de.roeth.model.inverter.Deye;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class EVTracker extends Entity {

    private static final DecimalFormat df = new DecimalFormat("0.0");
    private final Deye deye;
    private final int powerThreshold = -2300;
    private final long necessaryTimeOverThreshold = 60000;
    private boolean evStatus = false;
    private double currentPlugPower = 0;
    private double todayPlugConsumption = 0;
    private double totalPlugConsumption = 0;
    private long lastTimeNotSufficientPower = new Date().getTime();

    public EVTracker(Deye deye) {
        super("ev");
        this.deye = deye;
    }

    @NotNull
    private static HttpURLConnection getHttpURLConnection(URL obj) throws IOException {
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "text/plain");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        return con;
    }

    public static void main(String[] args) throws IOException {
        new EVTracker(null).trackPlugConsumption();
    }

    @Override
    public void update() throws IOException {
        checkEVStatus();
        trackPlugConsumption();
    }

    public void trackPlugConsumption() throws IOException {
        JSONObject plug = getEvPlugConsumption();
        if (plug.has("StatusSNS")) {
            JSONObject energy = plug.getJSONObject("StatusSNS").getJSONObject("ENERGY");
            if (energy.has("Power")) {
                currentPlugPower = energy.getDouble("Power");
            }
            if (energy.has("Today")) {
                todayPlugConsumption = energy.getDouble("Today");
            }
            if (energy.has("Total")) {
                totalPlugConsumption = energy.getDouble("Total");
            }
        }
    }

    private void checkEVStatus() {
        Date now = new Date();
        int currentPower = deye.modbusCallByName("total_grid_power").value();
        if (currentPower > 0) {
            lastTimeNotSufficientPower = now.getTime();
            evStatus = false;
            return;
        }
        if (currentPower < powerThreshold) {
            if (lastTimeNotSufficientPower + necessaryTimeOverThreshold <= now.getTime()) {
                evStatus = true;
            } else {
                evStatus = false;
            }
        }
    }

    private JSONObject getEvPlugConsumption() throws IOException {
        try {
            String url = "http://192.168.178.82/cm?cmnd=Status%2010";
            HttpURLConnection con = getHttpURLConnection(new URL(url));
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return new JSONObject(response.toString());
            }
        } catch (Exception e) {
            return new JSONObject();
        }
        return new JSONObject();
    }

    @Override
    public ArrayList<EntityInfo> snapshotInfo() {
        ArrayList<EntityInfo> infos = new ArrayList<>();
        infos.add(new EntityInfo("station", evStatus ? 1 : 0, evStatus ? "ON" : "OFF"));
        infos.add(new EntityInfo("station_summary", 0, evStatus ? "ON (" + df.format(currentPlugPower) + " W)" : "OFF"));
        infos.add(new EntityInfo("plug_power", currentPlugPower, df.format(currentPlugPower) + " W"));
        infos.add(new EntityInfo("plug_today_consumption", todayPlugConsumption, df.format(todayPlugConsumption) + " kWh"));
        infos.add(new EntityInfo("plug_total_consumption", totalPlugConsumption, df.format(totalPlugConsumption) + " kWh"));
        return infos;
    }

    @Override
    public ArrayList<String> influxWhitelist() {
        ArrayList<String> whitelist = new ArrayList<>();
        whitelist.add("station");
        whitelist.add("plug_power");
        whitelist.add("plug_today_consumption");
        whitelist.add("plug_total_consumption");
        return whitelist;
    }
}
