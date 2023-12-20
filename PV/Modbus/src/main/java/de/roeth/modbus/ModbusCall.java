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
    public int offset;
    public boolean cachable;
    public boolean resetCacheAtNewDay;

    public ModbusCall() {
        this("", new int[0], 1, "", 0, false, false);
    }

    public ModbusCall(String name, int[] register, double scale, String unit, int offset, boolean cachable, boolean resetCacheAtNewDay) {
        this.name = name;
        this.register = register;
        this.scale = scale;
        this.unit = unit;
        this.offset = offset;
        this.cachable = cachable;
        this.resetCacheAtNewDay = resetCacheAtNewDay;
    }

    public void addValue(int value) {
        values.add(value);
    }

    public int value() {
        return values.get(0) + offset;
    }

    public double scaledValue() {
        return value() * scale;
    }

    public String pretty() {
        return df.format(scaledValue()) + " " + unit;
    }
}
