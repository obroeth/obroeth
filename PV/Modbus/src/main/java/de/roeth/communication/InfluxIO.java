package de.roeth.communication;

import de.roeth.PVSystem;
import de.roeth.model.Device;
import de.roeth.model.input.DeviceProperty;
import de.roeth.utils.SystemUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxIO {

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
        SystemUtils.debug(InfluxIO.class, "===> Starting sending to measurement <pv-" + device.name + "> of InfluxDB.");
        StringBuilder debugString = new StringBuilder();
        InfluxLogbook.startNewLog();
        for (DeviceProperty prop : device.getDeviceProperties()) {
            if (prop.toInflux()) {
                send = true;
                dataPoint.addField(device.name + "_" + prop.name(), prop.numericPayload());
                debugString.append("Field: ").append(device.name).append("_").append(prop.name()).append(" -> ").append(prop.numericPayload()).append(",");
                InfluxLogbook.log(device, prop);
            }
        }
        if (send) {
            SystemUtils.debug(InfluxIO.class, "Send point: " + debugString);
            pushToInfluxOH("pv_values", dataPoint.build());
            pushToInfluxHA("pv_values", dataPoint.build());
        }
        SystemUtils.debug(InfluxIO.class, "<=== Finished sending device <" + device.name + "> to InfluxDB.");
    }

    public static void recover(String device, List<DeviceProperty> props, long timestamp) {
        boolean send = false;
        Point.Builder dataPoint = Point.measurement("pv-" + device).time(timestamp, TimeUnit.MILLISECONDS);
        SystemUtils.debug(InfluxIO.class, "===> Starting recovering measurement <pv-" + device + "> of InfluxDB.");
        StringBuilder debugString = new StringBuilder();
        for (DeviceProperty prop : props) {
            if (prop.toInflux()) {
                send = true;
                dataPoint.addField(device + "_" + prop.name(), prop.numericPayload());
                debugString.append("Field: ").append(device).append("_").append(prop.name()).append(" -> ").append(prop.numericPayload()).append(",");
            }
        }
        if (send) {
            SystemUtils.debug(InfluxIO.class, "Send point: " + debugString);
            pushToInfluxOH("pv_values", dataPoint.build());
            pushToInfluxHA("pv_values", dataPoint.build());
        }
        SystemUtils.debug(InfluxIO.class, "<=== Finished recovering device <" + device + "> to InfluxDB.");
    }

    public static void pushToInfluxOH(String database, Point point) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.22:8086");
        influxDB.setDatabase(database);
        influxDB.write(point);
        influxDB.close();
    }

    public static void pushToInfluxHA(String database, Point point) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.127:8086", "influx", "haFidio12per!");
        influxDB.setDatabase(database);
        influxDB.write(point);
        influxDB.close();
    }

}
