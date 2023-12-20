package de.roeth.modbus;

import de.roeth.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class ModbusFileIO {

    public static ArrayList<ModbusCallSequence> readDeyeModbusSequences() throws IOException {
        return readModbusSequences("deye_seq.json");
    }

    public static ArrayList<ModbusCall> readDeyeModbusCalls() throws IOException {
        return readModbusCalls("deye.json");
    }

    public static ArrayList<ModbusCallSequence> readSolaxModbusSequences() throws IOException {
        return readModbusSequences("solax_seq.json");
    }

    public static ArrayList<ModbusCall> readSolaxModbusCalls() throws IOException {
        return readModbusCalls("solax.json");
    }

    private static ArrayList<ModbusCall> readModbusCalls(String file) throws IOException {
        ArrayList<ModbusCall> modbusCalls = new ArrayList<>();

        String string;
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream(file)) {
            string = new String(Objects.requireNonNull(stream).readAllBytes(), StandardCharsets.UTF_8);
        }
        JSONArray root = new JSONArray(string);

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
            modbusCalls.add(new ModbusCall(name, registers, scale, unit, offset, cachable, resetCacheAtNewDay));
        }

        return modbusCalls;
    }

    private static ArrayList<ModbusCallSequence> readModbusSequences(String file) throws IOException {
        ArrayList<ModbusCallSequence> sequences = new ArrayList<>();

        String string;
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream(file)) {
            string = new String(Objects.requireNonNull(stream).readAllBytes(), StandardCharsets.UTF_8);
        }
        JSONArray root = new JSONArray(string);

        for (int i = 0; i < root.length(); i++) {
            JSONObject call = root.getJSONObject(i);
            int start = call.getInt("start");
            int end = call.getInt("end");
            sequences.add(new ModbusCallSequence(start, end));
        }

        return sequences;
    }
}
