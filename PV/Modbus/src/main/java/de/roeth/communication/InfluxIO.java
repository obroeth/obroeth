package de.roeth.communication;

import de.roeth.model.Deye;
import de.roeth.model.Solax;
import de.roeth.model.SumInverter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.ArrayList;

public class InfluxIO {

    public static void pushToInflux(Solax solax) {

    }

    public static void pushToInflux(Deye deye) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.22:8086");
        influxDB.setDatabase("pv_values");
        Point.Builder dataPoint = Point.measurement("pv-deye");

        ArrayList<String> whitelist = new ArrayList<>();
        whitelist.add("deye_total_grid_power");

        for (int i = 0; i < deye.getPropertyLength(); i++) {
            if(whitelist.contains(deye.getPropertyName(i))) {
                dataPoint.addField(deye.getPropertyName(i), deye.getPropertyScaledValue(i));
            }
        }

        influxDB.write(dataPoint.build());
        influxDB.close();
    }

    public static void pushToInflux(SumInverter sum) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.22:8086");
        influxDB.setDatabase("pv_values");
        Point.Builder dataPoint = Point.measurement("pv-sum");

        for (int i = 0; i < sum.getPropertyLength(); i++) {
            if(sum.getPropertyName(i).contains("_pv_power")) {
                dataPoint.addField("pv_power", sum.getPropertyScaledValue(i));
            }
            if(sum.getPropertyName(i).contains("_load_power")) {
                dataPoint.addField("load_power", sum.getPropertyScaledValue(i));
            }
        }

        influxDB.write(dataPoint.build());
        influxDB.close();
    }

}
