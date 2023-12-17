package de.roeth.modbus;

public class ModbusRegisterEntry {

    public ModbusRegisterEntry(int register, int value) {
        this.register = register;
        this.value = value;
    }

    public int register;
    public int value;

}
