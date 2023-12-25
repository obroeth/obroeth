package de.roeth.model.devices;

import de.roeth.communication.Utils;
import de.roeth.modbus.ModbusCall;
import de.roeth.model.Entity;
import de.roeth.model.EntityInfo;
import de.roeth.model.Evaluator;
import de.roeth.model.inverter.Deye;
import de.roeth.model.inverter.Solax;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class SmartMeter extends Entity {

    private final Evaluator evaluator;
    private final ArrayList<EntityInfo> infos = new ArrayList<>();
    private long endOfDay;
    private long now;

    public SmartMeter(Deye deye, Solax solax) throws IOException {
        super("smartmeter");
        this.evaluator = new Evaluator(solax, deye);
        if (!load()) {
            endOfDay = Utils.getEndOfToday().getTime();
        }
        System.out.println("Started smart meter! EOD is " + new Date(endOfDay));
    }

    @Override
    public void update() throws IOException {
        now = new Date().getTime();
        logDay();
        save();
    }

    public void clearInfo() {
        infos.clear();
    }

    private void logDay() {
        if (now >= endOfDay) {
            infos.add(makeDailyOwnUsed());
            infos.add(makeDailyBought());
            infos.add(makeDailyConsumption());
            infos.add(makeDailyProduction());
            infos.add(makeDailySold());
            System.out.println("Logged daily stuff at " + new Date(now));
            endOfDay = Utils.getEndOfTomorrow().getTime();
            System.out.println("Next daily stuff will be logged at " + new Date(endOfDay));
        }
    }

    private boolean load() throws IOException {
        if (new File("smart_meter.json").exists()) {
            JSONObject jsonObject = Utils.createJsonObject("smart_meter.json");
            endOfDay = jsonObject.getLong("endOfDay");
            return true;
        }
        return false;
    }

    private void save() throws IOException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("endOfDay", endOfDay);

        jsonObject.write(new FileWriter("smart_meter.json")).flush();
    }

    private EntityInfo makeDailyOwnUsed() {
        ModbusCall fake = new ModbusCall();
        int daily_sold = evaluator.dailySold();
        int daily_prod = evaluator.totalDailyProduction();
        int own_used = daily_prod - daily_sold;
        fake.name = "daily_own_used";
        fake.addValue(own_used);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return new EntityInfo(fake.name, fake.scaledValue(), fake.pretty());
    }

    private EntityInfo makeDailyBought() {
        ModbusCall fake = new ModbusCall();
        int daily_bought = evaluator.dailyBought();
        fake.name = "daily_bought";
        fake.addValue(daily_bought);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return new EntityInfo(fake.name, fake.scaledValue(), fake.pretty());
    }

    private EntityInfo makeDailyConsumption() {
        ModbusCall fake = new ModbusCall();
        int daily_sold = evaluator.dailySold();
        int daily_prod = evaluator.totalDailyProduction();
        int own_used = daily_prod - daily_sold;
        int daily_bought = evaluator.dailyBought();
        fake.name = "daily_used_consumption";
        fake.addValue(own_used + daily_bought);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return new EntityInfo(fake.name, fake.scaledValue(), fake.pretty());
    }

    private EntityInfo makeDailyProduction() {
        ModbusCall fake = new ModbusCall();
        int daily_prod = evaluator.totalDailyProduction();
        fake.name = "daily_production";
        fake.addValue(daily_prod);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return new EntityInfo(fake.name, fake.scaledValue(), fake.pretty());
    }

    private EntityInfo makeDailySold() {
        ModbusCall fake = new ModbusCall();
        int daily_prod = evaluator.dailySold();
        fake.name = "daily_sold";
        fake.addValue(daily_prod);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return new EntityInfo(fake.name, fake.scaledValue(), fake.pretty());
    }

    @Override
    public ArrayList<EntityInfo> snapshotInfo() {
        return infos;
    }

    @Override
    public ArrayList<String> influxWhitelist() {
        ArrayList<String> whitelist = new ArrayList<>();
        whitelist.add("daily_own_used");
        whitelist.add("daily_bought");
        whitelist.add("daily_used_consumption");
        whitelist.add("daily_production");
        whitelist.add("daily_sold");
        return whitelist;
    }

}
