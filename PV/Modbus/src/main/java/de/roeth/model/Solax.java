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
            InputRegister[] inputRegisters = modbusMaster.readInputRegisters(sequence.startRegister, sequence.length());
            modbusMaster.disconnect();
            return inputRegisters;
        } catch (Exception e) {
            modbusMaster.disconnect();
            FakeRegister[] fake = new FakeRegister[sequence.length()];
            for (int i = 0; i < sequence.length(); i++) {
                fake[i] = new FakeRegister();
            }
            return fake;
        }
    }

    @Override
    public ModbusEndpoint getEndpoint() {
        return ModbusEndpoint.SOLAX;
    }


}