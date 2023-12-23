package de.roeth.model;

import de.roeth.model.inverter.Deye;
import de.roeth.model.inverter.Solax;

public class Evaluator {

    private Solax solax;
    private Deye deye;

    public Evaluator(Solax solax, Deye deye) {
        this.solax = solax;
        this.deye = deye;
    }

    public int totalPvPower() {
        int deye_pv_power = deye.modbusCallByName("pv_power_1").value() + deye.modbusCallByName("pv_power_2").value();
        int solax_pv_power = solax.modbusCallByName("pv_power_1").value() + solax.modbusCallByName("pv_power_2").value();
        return deye_pv_power + solax_pv_power;
    }

    public int totalGridPower() {
        return deye.modbusCallByName("total_grid_power").value();
    }

    public int totalDailyProduction() {
        int deye_daily_prod = deye.modbusCallByName("daily_production").value();
        int solax_daily_prod = solax.modbusCallByName("daily_production").value();
        return solax_daily_prod + deye_daily_prod;
    }

    public int totalTotalProduction() {
        int deye_total_prod = deye.modbusCallByName("total_production").value();
        int solax_total_prod = solax.modbusCallByName("total_production").value();
        return deye_total_prod + solax_total_prod;
    }

    public int dailySold() {
        return deye.modbusCallByName("daily_energy_sold").value();
    }

    public int totalSold() {
        return deye.modbusCallByName("total_energy_sold").value();
    }

    public int dailyBought() {
        return deye.modbusCallByName("daily_energy_bought").value();
    }

    public int totalBought() {
        return deye.modbusCallByName("total_energy_bought").value();
    }

}
