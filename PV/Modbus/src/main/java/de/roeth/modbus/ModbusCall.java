package de.roeth.modbus;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ModbusCall {

    public ModbusCall() {
        this("", new int[0], 1, "");
    }

    public ModbusCall(String name, int[] register, double scale, String unit) {
        this.name = name;
        this.register = register;
        this.scale = scale;
        this.unit = unit;
    }

    public String name;
    public int[] register;
    public double scale;
    public String unit;
    public ArrayList<Integer> values = new ArrayList<>();

    public int value() {
        return values.get(0);
    }

    private static final DecimalFormat df = new DecimalFormat("0.0");
    public String pretty() {
        return df.format(value() * scale) + " " + unit;
    }
}
