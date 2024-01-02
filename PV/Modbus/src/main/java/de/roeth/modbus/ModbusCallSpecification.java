package de.roeth.modbus;

public class ModbusCallSpecification {

    public String name;
    public String unit;
    public int[] register;
    public double scale;
    public int offset;
    public boolean cachable;
    public boolean resetCacheAtNewDay;
    public boolean toOpenhab;
    public boolean toInflux;

    public ModbusCallSpecification(String name, String unit, int[] register, double scale, int offset, boolean cachable, boolean resetCacheAtNewDay, boolean toOpenhab, boolean toInflux) {
        this.name = name;
        this.unit = unit;
        this.register = register;
        this.scale = scale;
        this.offset = offset;
        this.cachable = cachable;
        this.resetCacheAtNewDay = resetCacheAtNewDay;
        this.toOpenhab = toOpenhab;
        this.toInflux = toInflux;
    }
}
