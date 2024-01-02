package de.roeth.model.devices;

import de.roeth.model.Device;
import de.roeth.model.Evaluator;
import de.roeth.model.input.DefaultDeviceProperty;
import de.roeth.utils.FormatUtils;
import de.roeth.utils.JsonIOUtils;
import de.roeth.utils.SystemUtils;
import de.roeth.utils.TimeUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class SmartMeter extends Device {

    private final Evaluator evaluator;
    private long endOfDay;
    private long now;

    public SmartMeter(Evaluator evaluator) throws IOException {
        super("smartmeter");
        this.evaluator = evaluator;
        if (!load()) {
            endOfDay = TimeUtils.getEndOfToday().getTime();
        } else {
            SystemUtils.debug(this, "Successfully loaded smart meter cache.");
        }
        System.out.println("Started smart meter! EOD is " + new Date(endOfDay));
    }

    @Override
    public void update() throws IOException {
        SystemUtils.debug(this, "===> Start update of <" + name + ">.");
        now = new Date().getTime();
        logDay();
        save();
        SystemUtils.debug(this, "<=== End update of <" + name + ">.");
    }

    @Override
    public String getCacheFile() {
        return "smartmeter_cache.json";
    }

    private void logDay() {
        if (now >= endOfDay) {
            deviceProperties.add(makeDailyOwnUsage());
            deviceProperties.add(makeDailyEnergyBought());
            deviceProperties.add(makeDailyConsumption());
            deviceProperties.add(makeDailyProduction());
            deviceProperties.add(makeDailySold());
            System.out.println("Logged daily stuff at " + new Date(now));
            endOfDay = TimeUtils.getEndOfTomorrow().getTime();
            System.out.println("Next daily stuff will be logged at " + new Date(endOfDay));
        }
    }

    private boolean load() throws IOException {
        if (new File(getCacheFile()).exists()) {
            JSONObject jsonObject = JsonIOUtils.readJsonObject(getCacheFile());
            endOfDay = jsonObject.getLong("endOfDay");
            return true;
        }
        return false;
    }

    private void save() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endOfDay", endOfDay);
        JsonIOUtils.writeJsonObject(getCacheFile(), jsonObject);
    }

    private DefaultDeviceProperty makeDailyOwnUsage() {
        double dailyOwnUsed = evaluator.dailyOwnUsage();
        return new DefaultDeviceProperty.Builder()
                .name("daily_own_used")
                .toOpenhab(false)
                .textPayload(FormatUtils.ONE_DIGIT.format(dailyOwnUsed) + " kWh")
                .toInflux(true)
                .numericPayload(dailyOwnUsed)
                .build();
    }

    private DefaultDeviceProperty makeDailyEnergyBought() {
        double dailyEnergyBought = evaluator.dailyEnergyBought();
        return new DefaultDeviceProperty.Builder()
                .name("daily_bought")
                .toOpenhab(false)
                .textPayload(FormatUtils.ONE_DIGIT.format(dailyEnergyBought) + " kWh")
                .toInflux(true)
                .numericPayload(dailyEnergyBought)
                .build();
    }

    private DefaultDeviceProperty makeDailyConsumption() {
        double dailyConsumption = evaluator.dailyConsumption();
        return new DefaultDeviceProperty.Builder()
                .name("daily_used_consumption")
                .toOpenhab(false)
                .textPayload(FormatUtils.ONE_DIGIT.format(dailyConsumption) + " kWh")
                .toInflux(true)
                .numericPayload(dailyConsumption)
                .build();
    }

    private DefaultDeviceProperty makeDailyProduction() {
        double dailyProduction = evaluator.dailyProduction();
        return new DefaultDeviceProperty.Builder()
                .name("daily_production")
                .toOpenhab(false)
                .textPayload(FormatUtils.ONE_DIGIT.format(dailyProduction) + " kWh")
                .toInflux(true)
                .numericPayload(dailyProduction)
                .build();
    }

    private DefaultDeviceProperty makeDailySold() {
        double dailyEnergySold = evaluator.dailyEnergySold();
        return new DefaultDeviceProperty.Builder()
                .name("daily_sold")
                .toOpenhab(false)
                .textPayload(FormatUtils.ONE_DIGIT.format(dailyEnergySold) + " kWh")
                .toInflux(true)
                .numericPayload(dailyEnergySold)
                .build();
    }
}
