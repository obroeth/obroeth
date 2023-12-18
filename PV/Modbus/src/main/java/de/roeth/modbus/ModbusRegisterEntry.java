package de.roeth.modbus;

public class ModbusRegisterEntry {

    public int register;
    public int value;
    public ModbusRegisterEntry(int register, int value) {
        this.register = register;
        this.value = value;
    }

}
