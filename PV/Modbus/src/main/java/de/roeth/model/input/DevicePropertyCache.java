package de.roeth.model.input;

import de.roeth.utils.JsonIOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DevicePropertyCache {

    public static List<DeviceProperty> read(String file) {
        ArrayList<DeviceProperty> properties = new ArrayList<>();

        JSONArray root = JsonIOUtils.readJsonArray(file);
        for (int i = 0; i < root.length(); i++) {
            JSONObject entry = root.getJSONObject(i);
            DefaultDeviceProperty prop = new DefaultDeviceProperty.Builder()
                    .name(entry.getString("name"))
                    .toOpenhab(entry.getBoolean("toOpenhab"))
                    .textPayload(entry.getString("textPayload"))
                    .defaultTextPayload(entry.getString("defaultTextPayload"))
                    .toInflux(entry.getBoolean("toInflux"))
                    .numericPayload(entry.getDouble("numericPayload"))
                    .defaultNumericPayload(entry.getDouble("defaultNumericPayload"))
                    .cacheTime(entry.getLong("cacheTime"))
                    .resetAtNewDay(entry.getBoolean("resetAtNewDay"))
                    .isCached(true)
                    .build();
            properties.add(prop);
        }

        return properties;
    }

    public static void write(String file, List<DeviceProperty> properties) throws IOException {
        JSONArray json = new JSONArray();
        for (DeviceProperty property : properties) {
            json.put(property.toJson());
        }
        JsonIOUtils.writeJsonArray(file, json);
    }

}
