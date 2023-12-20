package de.roeth.model;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.*;

import java.util.ArrayList;

public class Solax extends RealInverter {

    public Solax(ArrayList<ModbusCall> modbusCalls, ModbusRegister register) {
        super("solax", modbusCalls, register);
    }

    @Override
    public InputRegister[] readRegister(ModbusCallSequence sequence) {
        ModbusSerialMaster modbusMaster = new ModbusSerialMaster(getEndpoint().getParameter());
        try {
            modbusMaster.connect();
            InputRegister[] registers = modbusMaster.readInputRegisters(sequence.startRegister, sequence.length());
            modbusMaster.disconnect();

            LogRegister.writeLogRegister(registers, sequence, "solax.out.json");
            return registers;
        } catch (Exception e) {
            modbusMaster.disconnect();
            System.out.println("Using cache!");
            return LogRegister.readLogRegister(sequence, "solax.out.json");
        }
    }

    @Override
    public ModbusEndpoint getEndpoint() {
        return ModbusEndpoint.SOLAX;
    }


}