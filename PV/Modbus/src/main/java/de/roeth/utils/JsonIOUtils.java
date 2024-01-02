package de.roeth.utils;

import org.json.JSONArray;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonIOUtils {

    public static JSONArray read(String file) {
        try (InputStream stream = new FileInputStream(file)) {
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static void write(String file, JSONArray json) throws IOException {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        json.write(new FileWriter(f)).flush();
    }
}
