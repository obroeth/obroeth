package de.roeth.modbus;

import com.ghgande.j2mod.modbus.procimg.Register;

public class FakeRegister implements Register {
    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public void setValue(int i) {

    }

    @Override
    public void setValue(short i) {

    }

    @Override
    public void setValue(byte[] bytes) {

    }

    @Override
    public int toUnsignedShort() {
        return 0;
    }

    @Override
    public short toShort() {
        return 0;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
