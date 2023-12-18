package de.roeth.modbus;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ModbusCall {

    private static final DecimalFormat df = new DecimalFormat("0.0");
    private final ArrayList<Integer> values = new ArrayList<>();
    public String name;
    public int[] register;
    public double scale;
    public String unit;

    public ModbusCall() {
        this("", new int[0], 1, "");
    }

    public ModbusCall(String name, int[] register, double scale, String unit) {
        this.name = name;
        this.register = register;
        this.scale = scale;
        this.unit = unit;
    }

    public void addValue(int value) {
        values.add(value);
    }

    public int value() {
        return values.get(0);
    }

    public double scaledValue() {
        return value() * scale;
    }

    public String pretty() {
        return df.format(scaledValue()) + " " + unit;
    }
}
