package de.roeth.communication;

import de.roeth.PVSystem;
import de.roeth.model.Device;
import de.roeth.model.input.DeviceProperty;
import de.roeth.utils.SystemUtils;
import org.influxdb.dto.Point;

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
        SystemUtils.debug(InfluxIO.class, "===> Starting sending data to InfluxDB.");
        pushToInflux(pvSystem.deye);
        pushToInflux(pvSystem.solax);
        pushToInflux(pvSystem.sum);
        pushToInflux(pvSystem.evTracker);
        pushToInflux(pvSystem.sm);
        SystemUtils.debug(InfluxIO.class, "<=== Finished sending data to InfluxDB.");
    }

    public static void pushToInflux(Device device) {
        boolean send = false;
        Point.Builder dataPoint = Point.measurement("pv-" + device.name);
        SystemUtils.debug(InfluxIO.class, "===> Starting sending to database <pv-" + device.name + "> of InfluxDB.");
        StringBuilder debugString = new StringBuilder();
        for (DeviceProperty prop : device.getDeviceProperties()) {
            if (prop.toInflux()) {
                send = true;
                dataPoint.addField(device.name + "_" + prop.name(), prop.numericPayload());
                debugString.append("Field: ").append(device.name).append("_").append(prop.name()).append(" -> ").append(prop.numericPayload()).append(",");
            }
        }
        if (send) {
            SystemUtils.debug(InfluxIO.class, "Send point: " + debugString);
            pushToInflux("pv_values", dataPoint.build());
        }
        SystemUtils.debug(InfluxIO.class, "<=== Finished sending device <" + device.name + "> to InfluxDB.");
    }

    public static void pushToInflux(String database, Point point) {
//        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.22:8086");
//        influxDB.setDatabase(database);
//        influxDB.write(point);
//        influxDB.close();
    }

}
