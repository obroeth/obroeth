package de.roeth.communication;

import de.roeth.model.Deye;
import de.roeth.model.Entity;
import de.roeth.model.Solax;
import de.roeth.model.SumInverter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.ArrayList;

public class InfluxIO {

    public static void pushToInflux(Solax solax) {
        ArrayList<String> whitelist = new ArrayList<>();
        whitelist.add("solax_daily_production");

        pushToInflux(solax, whitelist);
    }

    public static void pushToInflux(Deye deye) {
        ArrayList<String> whitelist = new ArrayList<>();
        whitelist.add("deye_total_grid_power");
        whitelist.add("deye_battery_soc");
        whitelist.add("deye_daily_production");
        whitelist.add("deye_daily_energy_bought");
        whitelist.add("deye_daily_energy_sold");

        pushToInflux(deye, whitelist);
    }

    public static void pushToInflux(SumInverter sum) {
        pushToInflux(sum, new ArrayList<>());
    }

    private static void pushToInflux(Entity entity, ArrayList<String> whitelist) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.22:8086");
        influxDB.setDatabase("pv_values");
        Point.Builder dataPoint = Point.measurement("pv-" + entity.name);

        for (int i = 0; i < entity.getPropertyLength(); i++) {
            if (whitelist.contains(entity.getPropertyName(i)) || whitelist.isEmpty()) {
                dataPoint.addField(entity.getPropertyName(i), entity.getPropertyScaledValue(i));
            }
        }

        influxDB.write(dataPoint.build());
        influxDB.close();
    }

}
