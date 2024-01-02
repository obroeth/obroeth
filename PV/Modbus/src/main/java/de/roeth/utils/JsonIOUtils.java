package de.roeth.utils;

import de.roeth.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JsonIOUtils {

    public static JSONArray readJsonArray(String file) {
        try (InputStream stream = new FileInputStream(file)) {
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static JSONArray readJsonArrayResource(String file) {
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream(file)) {
            String string = new String(Objects.requireNonNull(stream).readAllBytes(), StandardCharsets.UTF_8);
            return new JSONArray(string);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static void writeJsonArray(String file, JSONArray json) throws IOException {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        json.write(new FileWriter(f)).flush();
    }

    public static JSONObject readJsonObject(String file) {
        try (InputStream stream = new FileInputStream(file)) {
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(json);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static void writeJsonObject(String file, JSONObject json) throws IOException {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        json.write(new FileWriter(f)).flush();
    }
}
