package de.roeth.communication;

import de.roeth.PVSystem;
import de.roeth.model.Device;
import de.roeth.model.input.DeviceProperty;
import de.roeth.utils.SystemUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenHabIO {

    public static void pushToOpenhab(PVSystem pvSystem) throws IOException {
        SystemUtils.debug(OpenHabIO.class, "===> Starting sending data to Openhab.");
        pushToOpenhab(pvSystem.deye);
        pushToOpenhab(pvSystem.solax);
        pushToOpenhab(pvSystem.sum);
        pushToOpenhab(pvSystem.evTracker);
        pushToOpenhab(pvSystem.sm);
        SystemUtils.debug(OpenHabIO.class, "<=== Finished sending data to Openhab.");
    }

    public static void pushToOpenhab(Device device) throws IOException {
        SystemUtils.debug(OpenHabIO.class, "===> Starting sending device <" + device.name + "> to Openhab.");
        for (DeviceProperty prop : device.getDeviceProperties()) {
            if (prop.toOpenhab()) {
                SystemUtils.debug(OpenHabIO.class, "Set <" + prop.name() + "> to <" + prop.textPayload() + ">.");
                curl(device.name, prop);
            }
        }
        SystemUtils.debug(OpenHabIO.class, "<=== Finished sending device <" + device.name + "> to Openhab.");
    }

    public static void curl(String device, String info) throws IOException {
        SystemUtils.debug(OpenHabIO.class, "===> Sending single curle call: <" + device + "> and data: <" + info + ">.");
        String url = "http://192.168.178.22:8080/rest/items/" + device;
        if (!writeToConnection(info, SystemUtils.createPostConnection(new URL(url)))) {
            System.out.println("POST Request failed. Tried to send name: " + device + " and data: " + info);
        }
    }

    public static void curl(String prefix, DeviceProperty prop) throws IOException {
        String url = "http://192.168.178.22:8080/rest/items/" + prefix + "_" + prop.name();
        if (!writeToConnection(prop.textPayload(), SystemUtils.createPostConnection(new URL(url)))) {
            System.out.println("POST Request failed. Tried to send name: " + prefix + "_" + prop.name() + " and data: " + prop.textPayload());
        }
    }

    private static boolean writeToConnection(String data, HttpURLConnection con) throws IOException {
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            System.out.println("Openhab Curl returned code: " + con.getResponseCode());
            return false;
        }
        return true;
    }
}
