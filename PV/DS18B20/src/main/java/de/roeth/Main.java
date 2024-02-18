package de.roeth;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start Temperature Logging at " + new Date());
        System.out.println("=> Waiting for 60s after startup...");
        Thread.sleep(60000);
        while(true) {
            try {
                double temp = readSensorValue();
                System.out.println("=> Temperature: " + temp);
                writeToDatabase(temp);
                sendMqttCommand(temp);
                Thread.sleep(60000);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private static void sendMqttCommand(double temp) throws MqttException {
        // MQTT-Client initialisieren
        MqttClient client = new MqttClient("tcp://192.168.178.22:1883", "tempi");
        client.connect();

        // MQTT-Befehl senden (Beispiel)
        if(temp > 35) {
            MqttMessage message = new MqttMessage("ON".getBytes());
            client.publish("cmnd/tasmota_5C1602/POWER", message);
            System.out.println("!!! Turned fan ON at " + new Date());
        }
        if(temp < 28) {
            MqttMessage message = new MqttMessage("OFF".getBytes());
            client.publish("cmnd/tasmota_5C1602/POWER", message);
            System.out.println("!!! Turned fan OFF at " + new Date());
        }

        // Verbindung schließen
        client.disconnect();
    }

    private static void writeToDatabase(double temp) {
        // InfluxDB-Verbindung herstellen
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.178.127:8086", "influx", "haFidio12per!");

        // Daten in InfluxDB speichern (Beispiel, anpassen an Ihre Struktur)
        influxDB.setDatabase("pv_values");
        Point dataPoint = Point.measurement("pv-room").addField("temperature", temp).build();
        influxDB.write(dataPoint);

        // Verbindung schließen
        influxDB.close();
    }

    private static double readSensorValue() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("/sys/bus/w1/devices/28-09225477cbdb/w1_slave")));
        String checkLine = reader.readLine();
        String tempLine = reader.readLine();
        if(checkLine.contains("YES")) {
            return Double.parseDouble(tempLine.split("t=")[1]) / 1000.;
        }
        return 0;
    }

}
