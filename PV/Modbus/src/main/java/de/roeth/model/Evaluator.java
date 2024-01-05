package de.roeth.model;

public class Evaluator {

    private final Device solax;
    private final Device deye;

    public Evaluator(Device solax, Device deye) {
        this.solax = solax;
        this.deye = deye;
    }

    public Device getDeye() {
        return deye;
    }

    public Device getSolax() {
        return solax;
    }

    public double pvPower() {
        double deye_pv_power = deye.getProperty("pv_power_1").numericPayload() + deye.getProperty("pv_power_2").numericPayload();
        double solax_pv_power = solax.getProperty("pv_power_1").numericPayload() + solax.getProperty("pv_power_2").numericPayload();
        return deye_pv_power + solax_pv_power;
    }

    public double batteryPower() {
        return deye.getProperty("battery_power").numericPayload();
    }

    public double batterySoCinWatt() {
        return batterySoC() * 14. / 100.;
    }

    public double batterySoC() {
        return deye.getProperty("battery_soc").numericPayload();
    }

    public double totalGridPower() {
        return deye.getProperty("total_grid_power").numericPayload();
    }

    public double loadPower() {
        return pvPower() + totalGridPower() + batteryPower();
    }

    public double dailyProduction() {
        double deye_daily_prod = deye.getProperty("daily_production").numericPayload();
        double solax_daily_prod = solax.getProperty("daily_production").numericPayload();
        return solax_daily_prod + deye_daily_prod;
    }

    public double totalProduction() {
        double deye_total_prod = deye.getProperty("total_production").numericPayload();
        double solax_total_prod = solax.getProperty("total_production").numericPayload();
        return deye_total_prod + solax_total_prod;
    }

    public double dailyBatteryCharge() {
        return deye.getProperty("daily_battery_charge").numericPayload();
    }

    public double dailyBatteryDischarge() {
        return deye.getProperty("daily_battery_discharge").numericPayload();
    }

    public double totalBatteryCharge() {
        return deye.getProperty("total_battery_charge").numericPayload();
    }

    public double totalBatteryDischarge() {
        return deye.getProperty("total_battery_discharge").numericPayload();
    }

    public double dailyConsumption() {
        return dailyOwnUsage() + dailyEnergyBought() + dailyBatteryDischarge();
    }

    public double totalConsumption() {
        return totalOwnUsage() + totalEnergyBought() + totalBatteryDischarge();
    }

    public double dailyOwnUsage() {
        return dailyProduction() - dailyEnergySold();
    }

    public double totalOwnUsage() {
        return totalProduction() - totalEnergySold();
    }

    public double dailyOwnUsagePercentage() {
        if (dailyProduction() <= 0.) {
            return 0.;
        }
        double val = dailyOwnUsage() / dailyProduction();
        return Math.min(val, 1.);
    }

    public double totalOwnUsagePercentage() {
        if (totalProduction() <= 0.) {
            return 0.;
        }
        double val = totalOwnUsage() / totalProduction();
        return Math.min(val, 1.);
    }

    public double dailyAutarchy() {
        double daily_used = dailyOwnUsage() + dailyEnergyBought();
        if (daily_used <= 0.) {
            return 0.;
        }
        double autarchy = dailyOwnUsage() / daily_used;
        return Math.min(autarchy, 1.);
    }

    public double totalAutarchy() {
        // Tag des Speicherstart am 03.01.2024:
        double totalOwnUsageOffset = 90.6;
        double totalEnergyBoughtOffset = 319.5;
        double ownUsage = totalOwnUsage() - totalOwnUsageOffset;
        double totalBought = totalEnergyBought() - totalEnergyBoughtOffset;
        double total_used = ownUsage + totalBought;
        if (total_used <= 0.) {
            return 1.;
        }
        double autarchy = ownUsage / total_used;
        return Math.min(autarchy, 1.);
    }

    public double dailyEnergySold() {
        return deye.getProperty("daily_energy_sold").numericPayload();
    }

    public double totalEnergySold() {
        return deye.getProperty("total_energy_sold").numericPayload();
    }

    public double dailyEnergyBought() {
        return deye.getProperty("daily_energy_bought").numericPayload();
    }

    public double totalEnergyBought() {
        return deye.getProperty("total_energy_bought").numericPayload();
    }

}
