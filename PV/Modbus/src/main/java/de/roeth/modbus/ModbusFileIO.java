package de.roeth.modbus;

import de.roeth.utils.JsonIOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ModbusFileIO {

    public static ArrayList<ModbusCallSequence> readDeyeModbusSequences() {
        return readModbusSequences("deye_seq.json");
    }

    public static ArrayList<ModbusCallSpecification> readDeyeModbusCalls() {
        return readModbusCalls("deye.json");
    }

    public static ArrayList<ModbusCallSequence> readSolaxModbusSequences() {
        return readModbusSequences("solax_seq.json");
    }

    public static ArrayList<ModbusCallSpecification> readSolaxModbusCalls() {
        return readModbusCalls("solax.json");
    }

    private static ArrayList<ModbusCallSpecification> readModbusCalls(String file) {
        ArrayList<ModbusCallSpecification> modbusCalls = new ArrayList<>();
        JSONArray root = JsonIOUtils.readJsonArrayResource(file);

        for (int i = 0; i < root.length(); i++) {
            JSONObject call = root.getJSONObject(i);
            String name = call.getString("name");
            JSONArray registerJson = call.getJSONArray("register");
            int[] registers = new int[registerJson.length()];
            for (int j = 0; j < registerJson.length(); j++) {
                registers[j] = registerJson.getInt(j);
            }
            double scale = call.getDouble("scale");
            String unit = call.getString("unit");
            int offset = 0;
            if (call.has("offset")) {
                offset = call.getInt("offset");
            }
            boolean cachable = false;
            if (call.has("cachable")) {
                cachable = call.getBoolean("cachable");
            }
            boolean resetCacheAtNewDay = false;
            if (call.has("resetCacheAtNewDay")) {
                resetCacheAtNewDay = call.getBoolean("resetCacheAtNewDay");
            }
            boolean toOpenhab = true;
            if (call.has("toOpenhab")) {
                toOpenhab = call.getBoolean("toOpenhab");
            }
            boolean toInflux = false;
            if (call.has("toInflux")) {
                toInflux = call.getBoolean("toInflux");
            }
            modbusCalls.add(new ModbusCallSpecification(name, unit, registers, scale, offset, cachable, resetCacheAtNewDay, toOpenhab, toInflux));
        }

        return modbusCalls;
    }

    private static ArrayList<ModbusCallSequence> readModbusSequences(String file) {
        ArrayList<ModbusCallSequence> sequences = new ArrayList<>();
        JSONArray root = JsonIOUtils.readJsonArrayResource(file);

        for (int i = 0; i < root.length(); i++) {
            JSONObject call = root.getJSONObject(i);
            int start = call.getInt("start");
            int end = call.getInt("end");
            sequences.add(new ModbusCallSequence(start, end));
        }

        return sequences;
    }
}
