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
        double deye_pv_power = deye.getNumericPropertyOrZero("pv_power_1") + deye.getNumericPropertyOrZero("pv_power_2");
        double solax_pv_power = solax.getNumericPropertyOrZero("pv_power_1") + solax.getNumericPropertyOrZero("pv_power_2");
        return deye_pv_power + solax_pv_power;
    }

    public double batteryPower() {
        return deye.getNumericPropertyOrZero("battery_power");
    }

    public double batterySoCinWatt() {
        return batterySoC() * 14. / 100.;
    }

    public double batterySoC() {
        return deye.getNumericPropertyOrZero("battery_soc");
    }

    public double totalGridPower() {
        return deye.getNumericPropertyOrZero("total_grid_power");
    }

    public double loadPower() {
        return pvPower() + totalGridPower() + batteryPower();
    }

    public double dailyProduction() {
        double deye_daily_prod = deye.getNumericPropertyOrZero("daily_production");
        double solax_daily_prod = solax.getNumericPropertyOrZero("daily_production");
        return solax_daily_prod + deye_daily_prod;
    }

    public double totalProduction() {
        double deye_total_prod = deye.getNumericPropertyOrZero("total_production");
        double solax_total_prod = solax.getNumericPropertyOrZero("total_production");
        return deye_total_prod + solax_total_prod;
    }

    public double dailyBatteryCharge() {
        return deye.getNumericPropertyOrZero("daily_battery_charge");
    }

    public double dailyBatteryDischarge() {
        return deye.getNumericPropertyOrZero("daily_battery_discharge");
    }

    public double totalBatteryCharge() {
        return deye.getNumericPropertyOrZero("total_battery_charge");
    }

    public double totalBatteryDischarge() {
        return deye.getNumericPropertyOrZero("total_battery_discharge");
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
        return deye.getNumericPropertyOrZero("daily_energy_sold");
    }

    public double totalEnergySold() {
        return deye.getNumericPropertyOrZero("total_energy_sold");
    }

    public double dailyEnergyBought() {
        return deye.getNumericPropertyOrZero("daily_energy_bought");
    }

    public double totalEnergyBought() {
        return deye.getNumericPropertyOrZero("total_energy_bought");
    }

}
