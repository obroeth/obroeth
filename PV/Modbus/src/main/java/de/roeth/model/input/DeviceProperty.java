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

    public String[] ToCsv() {
        String[] csv = new String[9];
        csv[0] = name();
        csv[1] = String.valueOf(toOpenhab());
        csv[2] = textPayload();
        csv[3] = defaultTextPayload();
        csv[4] = String.valueOf(toInflux());
        csv[5] = String.valueOf(numericPayload());
        csv[6] = String.valueOf(defaultNumericPayload());
        csv[7] = String.valueOf(System.currentTimeMillis());
        csv[8] = String.valueOf(resetAtNewDay());
        return csv;
    }


}
