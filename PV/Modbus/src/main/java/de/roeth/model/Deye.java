package de.roeth.model;

import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusRegister;

import java.util.ArrayList;

public class Deye extends RealInverter {

    public Deye(ArrayList<ModbusCall> modbusCalls, ModbusRegister register) {
        super("deye", modbusCalls, register);
    }

}
