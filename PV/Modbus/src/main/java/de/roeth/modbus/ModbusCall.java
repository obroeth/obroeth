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
    private ArrayList<Integer> values = new ArrayList<>();

    public void addValue(int value)
    {
//        if(signed && value > 30000) {
//            values.add(value - 65535);
//        } else {
            values.add(value);
        //}
    }
    public int value() {
        return values.get(0);
    }

    public double scaledValue() {
        return value() * scale;
    }

    private static final DecimalFormat df = new DecimalFormat("0.0");
    public String pretty() {
        return df.format(scaledValue()) + " " + unit;
    }
}
