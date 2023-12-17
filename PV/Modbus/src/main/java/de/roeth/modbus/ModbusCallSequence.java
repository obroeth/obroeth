package de.roeth.modbus;

public class ModbusCallSequence {

    public ModbusCallSequence(int startRegister, int endRegister) {
        this.startRegister = startRegister;
        this.endRegister = endRegister;
    }

    public int startRegister;
    public int endRegister;
    public int length() {
        return endRegister - startRegister + 1;
    }
}
