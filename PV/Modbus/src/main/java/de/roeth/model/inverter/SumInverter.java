package de.roeth.model.inverter;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.model.Evaluator;

import java.io.IOException;
import java.util.ArrayList;

public class SumInverter extends Inverter {

    private final Evaluator evaluator;

    public SumInverter(Solax solax, Deye deye) {
        super("sum");
        this.evaluator = new Evaluator(solax, deye);
    }

    @Override
    public void update() throws IOException {

    }

    private ModbusCall makeFake(int i) {
        ModbusCall fake = new ModbusCall();
        // PV Power Total
        switch (i) {
            case 0:
                int total_pv_power = evaluator.totalPvPower();
                fake.name = "pv_power";
                fake.addValue(total_pv_power);
                fake.unit = "W";
                fake.scale = 1;
                break;
            case 1:
                total_pv_power = evaluator.totalPvPower();
                int grid_power = evaluator.totalGridPower();
                fake.name = "load_power";
                fake.addValue(total_pv_power + grid_power);
                fake.unit = "W";
                fake.scale = 1;
                break;
            case 2:
                int daily_prod = evaluator.totalDailyProduction();
                fake.name = "daily_production";
                fake.addValue(daily_prod);
                fake.unit = "kWh";
                fake.scale = 0.1;
                break;
            case 3:
                int total_prod = evaluator.totalTotalProduction();
                fake.name = "total_production";
                fake.addValue(total_prod);
                fake.unit = "kWh";
                fake.scale = 0.1;
                break;
            case 4:
                int daily_sold = evaluator.dailySold();
                daily_prod = evaluator.totalDailyProduction();
                fake.name = "daily_own_usage";
                double val = 0.;
                if (daily_prod > 0) {
                    val = 1. - 1. * daily_sold / (1. * daily_prod);
                }
                val = Math.min(val, 1);
                fake.addValue((int) (100. * val));
                fake.unit = "%";
                fake.scale = 1;
                break;
            case 5:
                int total_sold = evaluator.totalSold();
                total_prod = evaluator.totalTotalProduction();
                fake.name = "total_own_usage";
                val = 0.;
                if (total_prod > 0) {
                    val = 1. - 1. * total_sold / (1. * total_prod);
                }
                val = Math.min(val, 1);
                fake.addValue((int) (100. * val));
                fake.unit = "%";
                fake.scale = 1;
                break;
            case 6:
                daily_sold = evaluator.dailySold();
                int daily_bought = evaluator.dailyBought();
                daily_prod = evaluator.totalDailyProduction();
                int own_used = daily_prod - daily_sold;
                int daily_used = own_used + daily_bought;
                fake.name = "daily_autarc";
                val = 0.;
                if (daily_used > 0) {
                    val = 1. * own_used / (1. * daily_used);
                }
                val = Math.min(val, 1);
                fake.addValue((int) (100. * val));
                fake.unit = "%";
                fake.scale = 1;
                break;
            case 7:
                total_sold = evaluator.totalSold();
                int total_bought = evaluator.totalBought();
                total_prod = evaluator.totalTotalProduction();
                own_used = total_prod - total_sold;
                int total_used = own_used + total_bought;
                fake.name = "total_autarc";
                val = 0.;
                if (total_used > 0) {
                    val = 1. * own_used / (1. * total_used);
                }
                val = Math.min(val, 1);
                fake.addValue((int) (100. * val));
                fake.unit = "%";
                fake.scale = 1;
                break;
        }
        return fake;
    }

    @Override
    public int getPropertyLength() {
        return 8;
    }

    @Override
    public String getPropertyName(int i) {
        return makeFake(i).name;
    }

    @Override
    public int getPropertyValue(int i) {
        return makeFake(i).value();
    }

    @Override
    public double getPropertyScaledValue(int i) {
        return makeFake(i).scaledValue();
    }

    @Override
    public String getPropertyPretty(int i) {
        return makeFake(i).pretty();
    }

    @Override
    public InputRegister[] readRegister(ModbusCallSequence sequence) {
        return new InputRegister[0];
    }

    @Override
    public ArrayList<String> influxWhitelist() {
        return new ArrayList<>();
    }
}
