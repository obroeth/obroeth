package de.roeth.model;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.communication.Utils;
import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusEndpoint;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class SmartMeter extends Entity {

    private final Evaluator evaluator;
    private final ArrayList<ModbusCall> calls = new ArrayList<>();
    private long endOfDay;
    private long now;

    public SmartMeter(Deye deye, Solax solax) throws IOException {
        super("smartmeter");
        this.evaluator = new Evaluator(solax, deye);
        if (!load()) {
            endOfDay = Utils.getEndOfToday().getTime();
        }
    }

    public void clearCalls() {
        calls.clear();
    }

    public void log() throws IOException {
        now = new Date().getTime();
        logDay();
        save();
    }

    private void logDay() {
        if (now >= endOfDay) {
            calls.add(makeDailyOwnUsed());
            calls.add(makeDailyBought());
            calls.add(makeDailyConsumption());
            calls.add(makeDailyProduction());
            calls.add(makeDailySold());
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

        jsonObject.write(new FileWriter((new File("smart_meter.json")))).flush();
    }

    private ModbusCall makeDailyOwnUsed() {
        ModbusCall fake = new ModbusCall();
        int daily_sold = evaluator.dailySold();
        int daily_prod = evaluator.totalDailyProduction();
        int own_used = daily_prod - daily_sold;
        fake.name = name + "_daily_own_used";
        fake.addValue(own_used);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return fake;
    }

    private ModbusCall makeDailyBought() {
        ModbusCall fake = new ModbusCall();
        int daily_bought = evaluator.dailyBought();
        fake.name = name + "_daily_bought";
        fake.addValue(daily_bought);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return fake;
    }

    private ModbusCall makeDailyConsumption() {
        ModbusCall fake = new ModbusCall();
        int daily_sold = evaluator.dailySold();
        int daily_prod = evaluator.totalDailyProduction();
        int own_used = daily_prod - daily_sold;
        int daily_bought = evaluator.dailyBought();
        fake.name = name + "_daily_used_consumption";
        fake.addValue(own_used + daily_bought);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return fake;
    }

    private ModbusCall makeDailyProduction() {
        ModbusCall fake = new ModbusCall();
        int daily_prod = evaluator.totalDailyProduction();
        fake.name = name + "_daily_production";
        fake.addValue(daily_prod);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return fake;
    }

    private ModbusCall makeDailySold() {
        ModbusCall fake = new ModbusCall();
        int daily_prod = evaluator.dailySold();
        fake.name = name + "_daily_sold";
        fake.addValue(daily_prod);
        fake.unit = "kWh";
        fake.scale = 0.1;
        return fake;
    }

    private ModbusCall makeFake(int i) {
        ModbusCall fake = new ModbusCall();
        // PV Power Total
        switch (i) {
            case 0:
                int daily_sold = evaluator.dailySold();
                int daily_prod = evaluator.totalDailyProduction();
                int own_used = daily_prod - daily_sold;
                fake.name = name + "_daily_own_used";
                fake.addValue(own_used);
                fake.unit = "kWh";
                fake.scale = 0.1;
                break;
            case 1:
                int daily_bought = evaluator.dailyBought();
                fake.name = name + "_daily_bought";
                fake.addValue(daily_bought);
                fake.unit = "kWh";
                fake.scale = 0.1;
                break;
        }
        return fake;
    }

    public int getPropertyLength() {
        return calls.size();
    }

    @Override
    public String getPropertyName(int i) {
        return calls.get(i).name;
    }

    @Override
    public int getPropertyValue(int i) {
        return calls.get(i).value();
    }

    @Override
    public double getPropertyScaledValue(int i) {
        return calls.get(i).scaledValue();
    }

    @Override
    public String getPropertyPrettyValue(int i) {
        return calls.get(i).pretty();
    }

    @Override
    public InputRegister[] readRegister(ModbusCallSequence sequence) {
        return new InputRegister[0];
    }

    @Override
    public ModbusEndpoint getEndpoint() {
        return null;
    }
}
