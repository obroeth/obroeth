package de.roeth.model;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusEndpoint;

public class SumInverter extends Entity {

    private Solax solax;
    private Deye deye;

    public SumInverter(Solax solax, Deye deye) {
        super("sum");
        this.solax = solax;
        this.deye = deye;
    }

    private ModbusCall makeFake(int i) {
        ModbusCall fake = new ModbusCall();
        // PV Power Total
        switch (i) {
            case 0:
                int deye_pv_power = deye.modbusCallByName("pv_power_1").value() + deye.modbusCallByName("pv_power_2").value();
                int solax_pv_power = solax.modbusCallByName("pv_power_1").value() + solax.modbusCallByName("pv_power_2").value();
                fake.name = name + "_pv_power";
                fake.addValue(deye_pv_power + solax_pv_power);
                fake.unit = "W";
                fake.scale = 1;
                break;
            case 1:
                deye_pv_power = deye.modbusCallByName("pv_power_1").value() + deye.modbusCallByName("pv_power_2").value();
                solax_pv_power = solax.modbusCallByName("pv_power_1").value() + solax.modbusCallByName("pv_power_2").value();
                int deye_grid_power = deye.modbusCallByName("total_grid_power").value();
                fake.name = name + "_load_power";
                fake.addValue(deye_pv_power + solax_pv_power + deye_grid_power);
                fake.unit = "W";
                fake.scale = 1;
                break;
            case 2:
                int deye_daily_prod = deye.modbusCallByName("daily_production").value();
                int solax_daily_prod = solax.modbusCallByName("daily_production").value();
                fake.name = name + "_daily_production";
                fake.addValue(deye_daily_prod + solax_daily_prod);
                fake.unit = "kWh";
                fake.scale = 0.1;
                break;
            case 3:
                int deye_total_prod = deye.modbusCallByName("total_production").value();
                int solax_total_prod = solax.modbusCallByName("total_production").value();
                fake.name = name + "_total_production";
                fake.addValue(deye_total_prod + solax_total_prod);
                fake.unit = "kWh";
                fake.scale = 0.1;
                break;
            case 4:
                int deye_daily_sold = deye.modbusCallByName("daily_energy_sold").value();
                deye_daily_prod = deye.modbusCallByName("daily_production").value();
                solax_daily_prod = solax.modbusCallByName("daily_production").value();
                int total_prod = deye_daily_prod + solax_daily_prod;
                fake.name = name + "_daily_own_usage";
                double val = 0.;
                if (total_prod > 0) {
                    val = 1. - 1. * deye_daily_sold / (1. * total_prod);
                }
                val = Math.min(val, 1);
                fake.addValue((int) (100. * val));
                fake.unit = "%";
                fake.scale = 1;
                break;
            case 5:
                int deye_total_sold = deye.modbusCallByName("total_energy_sold").value();
                deye_total_prod = deye.modbusCallByName("total_production").value();
                solax_total_prod = solax.modbusCallByName("total_production").value();
                total_prod = deye_total_prod + solax_total_prod;
                fake.name = name + "_total_own_usage";
                val = 0.;
                if (total_prod > 0) {
                    val = 1. - 1. * deye_total_sold / (1. * total_prod);
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
        return 6;
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
    public String getPropertyPrettyValue(int i) {
        return makeFake(i).pretty();
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
