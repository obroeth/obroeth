package de.roeth.communication;

import de.roeth.PVSystem;
import de.roeth.model.Device;
import de.roeth.model.input.DeviceProperty;
import de.roeth.utils.SystemUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class HomeAutomationIO {

    public static void pushToHomeAutomation(PVSystem pvSystem) throws MqttException {
        SystemUtils.debug(OpenHabIO.class, "===> Starting sending data to Home Automation.");
        pushToHomeAutomation(pvSystem.deye);
        pushToHomeAutomation(pvSystem.solax);
        pushToHomeAutomation(pvSystem.sum);
        pushToHomeAutomation(pvSystem.evTracker);
        pushToHomeAutomation(pvSystem.sm);
        SystemUtils.debug(OpenHabIO.class, "<=== Finished sending data to Home Automation.");
    }

    public static void pushToHomeAutomation(Device device) throws MqttException {
        SystemUtils.debug(OpenHabIO.class, "===> Starting sending device <" + device.name + "> to Home Automation.");
        JSONObject json = new JSONObject();
        for (DeviceProperty prop : device.getDeviceProperties()) {
            if (prop.toOpenhab()) {
                json.put(prop.name(), prop.numericPayload());
            }
        }
        mqtt(json, device);
        SystemUtils.debug(OpenHabIO.class, "<=== Finished sending device <" + device.name + "> to Home Automation");
    }

    public static void sendSingle(String key, String value, String device) throws MqttException {
        JSONObject json = new JSONObject();
        json.put(key, value);
        HomeAutomationIO.mqtt(json, device);
    }

    private static void mqtt(JSONObject json, Device device) throws MqttException {
        mqtt(json, device.name);
    }

    public static void mqtt(JSONObject json, String device) throws MqttException {
        MqttClient client = new MqttClient("tcp://192.168.178.127:1883", "RaspberryPiPublisher", null);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName("mqtt");
        connOpts.setPassword("haFidio12per!".toCharArray());
        connOpts.setCleanSession(true);
        client.connect(connOpts);

        String topic = "pv/" + device;
        MqttMessage message = new MqttMessage(json.toString().getBytes());
        message.setQos(2); // Qualität des Dienstes (QoS): 0, 1 oder 2

        client.publish(topic, message);

        client.disconnect();
    }

}
