package de.roeth.model.input;

import org.json.JSONObject;

public abstract class DeviceProperty {

    public abstract String name();

    public abstract boolean toOpenhab();

    public abstract String textPayload();

    public abstract String defaultTextPayload();

    public abstract boolean toInflux();

    public abstract double numericPayload();

    public abstract double defaultNumericPayload();

    public abstract boolean isCached();

    public abstract long cacheTime();

    public abstract boolean resetAtNewDay();
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name());
        json.put("toOpenhab", toOpenhab());
        json.put("textPayload", textPayload());
        json.put("defaultTextPayload", defaultTextPayload());
        json.put("toInflux", toInflux());
        json.put("numericPayload", numericPayload());
        json.put("defaultNumericPayload", defaultNumericPayload());
        json.put("cacheTime", System.currentTimeMillis());
        json.put("resetAtNewDay", resetAtNewDay());
        return json;
    }

}
