package de.roeth.model;

import de.roeth.modbus.ModbusCall;

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
                int solax_pv_power = 0;
                fake.name = name + "_pv_power";
                fake.addValue(deye_pv_power + solax_pv_power);
                fake.unit = "W";
                fake.scale = 1;
                break;
            case 1:
                deye_pv_power = deye.modbusCallByName("pv_power_1").value() + deye.modbusCallByName("pv_power_2").value();
                solax_pv_power = 0;
                int deye_grid_power = deye.modbusCallByName("total_grid_power").value();
                fake.name = name + "_load_power";
                fake.addValue(deye_pv_power + solax_pv_power + deye_grid_power);
                fake.unit = "W";
                fake.scale = 1;
                break;
            case 2:
                int deye_daily_prod = deye.modbusCallByName("daily_production").value();
                int solax_daily_prod = 0;
                fake.name = name + "_daily_production";
                fake.addValue(deye_daily_prod + solax_daily_prod);
                fake.unit = "kWh";
                fake.scale = 0.1;
                break;
        }
        return fake;
    }

    @Override
    public int getPropertyLength() {
        return 3;
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
}
