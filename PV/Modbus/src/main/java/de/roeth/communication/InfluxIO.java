package de.roeth.communication;

import de.roeth.PVSystem;
import de.roeth.model.Entity;
import de.roeth.model.EntityInfo;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.ArrayList;

public class InfluxIO {

//    public static void pushToInflux(SmartMeter sm) {
//        pushToInflux(sm, new ArrayList<>());
//        sm.clearCalls();
//    }
//
//    public static void pushToInflux(EVTracker evTracker) {
//        Point.Builder dataPoint = Point.measurement("ev_status");
//        dataPoint.addField("activation", evTracker.evStatus ? 1 : 0);
//        pushToInflux("pv_values", dataPoint.build());
//    }
//
//    public static void pushToInflux(Solax solax) {
//        ArrayList<String> whitelist = new ArrayList<>();
//        whitelist.add("solax_daily_production");
//
//        pushToInflux(solax, whitelist);
//    }
//
//    public static void pushToInflux(Deye deye) {
//        ArrayList<String> whitelist = new ArrayList<>();
//        whitelist.add("deye_total_grid_power");
//        whitelist.add("deye_battery_soc");
//        whitelist.add("deye_daily_production");
//        whitelist.add("deye_daily_energy_bought");
//        whitelist.add("deye_total_energy_bought");
//        whitelist.add("deye_daily_energy_sold");
//        whitelist.add("deye_total_energy_sold");
//
//        pushToInflux(deye, whitelist);
//    }
//
//    public static void pushToInflux(SumInverter sum) {
//        pushToInflux(sum, new ArrayList<>());
//    }

    public static void pushToInflux(PVSystem pvSystem) {
        pushToInflux(pvSystem.deye);
        pushToInflux(pvSystem.solax);
        pushToInflux(pvSystem.sum);
        pushToInflux(pvSystem.evTracker);
        pushToInflux(pvSystem.sm);
    }

    public static void pushToInflux(Entity entity) {
        ArrayList<EntityInfo> infos = entity.snapshotInfo();
        ArrayList<String> whitelist = entity.influxWhitelist();

        Point.Builder dataPoint = Point.measurement("pv-" + entity.name);
        for (EntityInfo info : infos) {
            if (whitelist.contains(info.name) || whitelist.isEmpty()) {
                dataPoint.addField(entity.name + "_" + info.name, info.value);
            }
        }
        if (!infos.isEmpty()) {
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
