package de.roeth.modbus;

public class ModbusRegisterEntry {

    public int register;
    public int value;
    public boolean cached;
    public long cacheTime;

    public ModbusRegisterEntry(int register, int value, boolean cached, long cacheTime) {
        this.register = register;
        this.value = value;
        this.cached = cached;
        this.cacheTime = cacheTime;
    }

}
