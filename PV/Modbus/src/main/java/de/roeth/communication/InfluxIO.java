package de.roeth.communication;

import de.roeth.model.*;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.ArrayList;

public class InfluxIO {

    public static void pushToInflux(SmartMeter sm) {
        pushToInflux(sm, new ArrayList<>());
        sm.clearCalls();
    }

    public static void pushToInflux(EVTracker evTracker) {
        Point.Builder dataPoint = Point.measurement("ev_status");
        dataPoint.addField("activation", evTracker.evStatus ? 1 : 0);
        pushToInflux("pv_values", dataPoint.build());
    }

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
        whitelist.add("deye_total_energy_bought");
        whitelist.add("deye_daily_energy_sold");
        whitelist.add("deye_total_energy_sold");

        pushToInflux(deye, whitelist);
    }

    public static void pushToInflux(SumInverter sum) {
        pushToInflux(sum, new ArrayList<>());
    }

    public static void pushToInflux(Entity entity, ArrayList<String> whitelist) {
        Point.Builder dataPoint = Point.measurement("pv-" + entity.name);
        for (int i = 0; i < entity.getPropertyLength(); i++) {
            if (whitelist.contains(entity.getPropertyName(i)) || whitelist.isEmpty()) {
                dataPoint.addField(entity.getPropertyName(i), entity.getPropertyScaledValue(i));
            }
        }
        if (entity.getPropertyLength() > 0) {
            pushToInflux("pv_values", dataPoint.build());
        }
    }

    public static void pushToInflux(String database, Point point) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.22:8086");
        influxDB.setDatabase(database);
        influxDB.write(point);
        influxDB.close();
    }

}
