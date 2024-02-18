package de.roeth.model.devices;

import de.roeth.model.Device;
import de.roeth.model.Evaluator;
import de.roeth.model.input.DefaultDeviceProperty;
import de.roeth.model.input.DeviceProperty;
import de.roeth.utils.FormatUtils;
import de.roeth.utils.SystemUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class EVTracker extends Device {

    private final Evaluator evaluator;
    private final int powerThreshold = -2200;
    private final long necessaryTimeOverThreshold = 20000;
    private boolean evStatus = false;
    private double currentPlugPower = 0;
    private double todayPlugConsumption = 0;
    private double totalPlugConsumption = 0;
    private long lastTimeNotSufficientPower = new Date().getTime();

    public EVTracker(Evaluator evaluator) {
        super("ev");
        this.evaluator = evaluator;
    }

    @Override
    public String getCacheFile() {
        return "ev_plug_cache.json";
    }

    @Override
    public void update() throws IOException {
        SystemUtils.debug(this, "===> Start update of <" + name + ">.");
        try {
            checkEVStatus();
            trackPlugConsumption();
            checkAndCorrectTotalConsumption();
            fillProperties();
            saveToCache(getCacheFile());
        } catch (IOException e) {
            SystemUtils.debug(this, "Failed to read from ev plug.");
            evStatus = false;
            loadFromCache(getCacheFile());
            deviceNotReachable();
        }
        SystemUtils.debug(this, "<=== End update of <" + name + ">.");
    }

    private void checkAndCorrectTotalConsumption() throws IOException {
        loadFromCache(getCacheFile());
        if (hasProperty("plug_today_consumption") && hasProperty("plug_total_consumption")) {
            double cachedTotalConsumption = getNumericPropertyOrZero("plug_total_consumption");
            double cachedDailyConsumption = getNumericPropertyOrZero("plug_today_consumption");
            long cacheTimeDaily = getProperty("plug_today_consumption").cacheTime();
            boolean isCached = getProperty("plug_today_consumption").isCached();
            boolean resetAtNewDay = getProperty("plug_today_consumption").resetAtNewDay();
            if (isCached && resetAtNewDay && !SystemUtils.isTimeToday(cacheTimeDaily)) {
                cachedDailyConsumption = 0.;
            }
            if (todayPlugConsumption < cachedDailyConsumption) {
                SystemUtils.debug(this, "Overwriting daily plug consumption: <" + cachedDailyConsumption + "> will replace <" + todayPlugConsumption + ">.");
                todayPlugConsumption = cachedDailyConsumption;
                setEvDailyPlugConsumption(todayPlugConsumption);
            }
            if (totalPlugConsumption < cachedTotalConsumption) {
                SystemUtils.debug(this, "Overwriting total plug consumption: <" + cachedTotalConsumption + "> will replace <" + totalPlugConsumption + ">.");
                totalPlugConsumption = cachedTotalConsumption;
                setEvTotalPlugConsumption(totalPlugConsumption - cachedDailyConsumption);
            }
        }
        deviceProperties.clear();
        SystemUtils.debug(this, "Check and correct done.");
    }

    private void deviceNotReachable() {
        DeviceProperty plugTotalConsumption = getProperty("plug_total_consumption");
        deviceProperties.clear();
        deviceProperties.add(makeStation());
        deviceProperties.add(makeStationSummary(0));
        deviceProperties.add(makePlugPower(0));
        deviceProperties.add(makeTodayConsumption(0));
        deviceProperties.add(plugTotalConsumption);
    }

    private void fillProperties() {
        deviceProperties.clear();
        deviceProperties.add(makeStation());
        deviceProperties.add(makeStationSummary(currentPlugPower));
        deviceProperties.add(makePlugPower(currentPlugPower));
        deviceProperties.add(makeTodayConsumption(todayPlugConsumption));
        deviceProperties.add(makeTotalConsumption());
    }

    private DefaultDeviceProperty makeTotalConsumption() {
        return new DefaultDeviceProperty.Builder()
                .name("plug_total_consumption")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(totalPlugConsumption) + " kWh")
                .toInflux(true)
                .numericPayload(totalPlugConsumption)
                .build();
    }

    private DefaultDeviceProperty makeTodayConsumption(double value) {
        return new DefaultDeviceProperty.Builder()
                .name("plug_today_consumption")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(value) + " kWh")
                .toInflux(true)
                .numericPayload(value)
                .resetAtNewDay(true)
                .build();
    }

    private DefaultDeviceProperty makePlugPower(double value) {
        return new DefaultDeviceProperty.Builder()
                .name("plug_power")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(value) + " W")
                .toInflux(true)
                .numericPayload(value)
                .build();
    }

    private DefaultDeviceProperty makeStationSummary(double value) {
        return new DefaultDeviceProperty.Builder()
                .name("station_summary")
                .toOpenhab(true)
                .textPayload(evStatus ? "ON (" + FormatUtils.ONE_DIGIT.format(value) + " W)" : "OFF")
                .toInflux(false)
                .build();
    }

    private DefaultDeviceProperty makeStation() {
        return new DefaultDeviceProperty.Builder()
                .name("station")
                .toOpenhab(true)
                .textPayload(evStatus ? "ON" : "OFF")
                .toInflux(true)
                .numericPayload(evStatus ? 1 : 0)
                .build();
    }

    public void trackPlugConsumption() throws IOException {
        JSONObject plug = getEvPlugConsumption();
        if (plug.has("StatusSNS")) {
            JSONObject status = plug.getJSONObject("StatusSNS");
            if (status.has("ENERGY")) {
                JSONObject energy = status.getJSONObject("ENERGY");
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
        SystemUtils.debug(this, "Track plug consumption done.");
    }

    private void checkEVStatus() {
        if (evaluator.getDeye().getProperty("total_grid_power").isCached()) {
            evStatus = false;
            return;
        }
        Date now = new Date();
        double currentPower = evaluator.totalGridPower();
        double batteryPower = evaluator.batteryPower();
        if (currentPower > 100 || batteryPower > 100) {
            lastTimeNotSufficientPower = now.getTime();
            if (evStatus) {
                System.out.println("Turned EV station off at " + new Date());
            }
            evStatus = false;
            return;
        }
        if (currentPower < powerThreshold) {
            evStatus = lastTimeNotSufficientPower + necessaryTimeOverThreshold <= now.getTime();
            if (evStatus) {
                System.out.println("Turned EV station on at " + new Date());
            }
        }
    }

    private void setEvTotalPlugConsumption(double total) throws IOException {
        String url = "http://192.168.178.82/cm?cmnd=EnergyTotal%20" + ((int) (total * 1000.));
        HttpURLConnection con = SystemUtils.createPutConnection(new URL(url));
        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("Failed to set total energy of plug!");
        }
    }

    private void setEvDailyPlugConsumption(double daily) throws IOException {
        String url = "http://192.168.178.82/cm?cmnd=EnergyToday%20" + ((int) (daily * 1000.));
        HttpURLConnection con = SystemUtils.createPutConnection(new URL(url));
        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("Failed to set total energy of plug!");
        }
    }

    private JSONObject getEvPlugConsumption() throws IOException {
        String url = "http://192.168.178.82/cm?cmnd=Status%2010";
        HttpURLConnection con = SystemUtils.createGetConnection(new URL(url));
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
        } else {
            throw new IOException("Return code bad: " + responseCode);
        }
    }

//    @Override
//    public ArrayList<DeviceInfo> snapshot() {
//        ArrayList<DeviceInfo> infos = new ArrayList<>();
//        infos.add(new DeviceInfo("station", evStatus ? 1 : 0, evStatus ? "ON" : "OFF"));
//        infos.add(new DeviceInfo("station_summary", 0, evStatus ? "ON (" + FormatUtils.ONE_DIGIT.format(currentPlugPower) + " W)" : "OFF"));
//        infos.add(new DeviceInfo("plug_power", currentPlugPower, FormatUtils.ONE_DIGIT.format(currentPlugPower) + " W"));
//        infos.add(new DeviceInfo("plug_today_consumption", todayPlugConsumption, FormatUtils.ONE_DIGIT.format(todayPlugConsumption) + " kWh"));
//        infos.add(new DeviceInfo("plug_total_consumption", totalPlugConsumption, FormatUtils.ONE_DIGIT.format(totalPlugConsumption) + " kWh"));
//        return infos;
//    }
//
//    @Override
//    public ArrayList<String> influxWhitelist() {
//        ArrayList<String> whitelist = new ArrayList<>();
//        whitelist.add("station");
//        whitelist.add("plug_power");
//        whitelist.add("plug_today_consumption");
//        whitelist.add("plug_total_consumption");
//        return whitelist;
//    }
}
