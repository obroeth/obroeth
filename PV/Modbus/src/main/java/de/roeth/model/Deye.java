package de.roeth.model;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import de.roeth.modbus.*;

import java.util.ArrayList;

public class Deye extends RealInverter {

    public Deye(ArrayList<ModbusCall> modbusCalls, ModbusRegister register) {
        super("deye", modbusCalls, register);
    }

    @Override
    public InputRegister[] readRegister(ModbusCallSequence sequence) {
        ModbusSerialMaster modbusMaster = new ModbusSerialMaster(getEndpoint().getParameter());
        try {
            modbusMaster.connect();
            Register[] registers = modbusMaster.readMultipleRegisters(getEndpoint().slave, sequence.startRegister, sequence.length());
            modbusMaster.disconnect();
            return registers;
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
        return ModbusEndpoint.DEYE;
    }
}
