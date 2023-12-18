package de.roeth.model;

import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusRegister;

import java.util.ArrayList;

public class Solax extends RealInverter {

    public Solax(ArrayList<ModbusCall> modbusCalls, ModbusRegister register) {
        super("solax", modbusCalls, register);
    }

}