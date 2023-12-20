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

            LogRegister.writeLogRegister(registers, sequence, "deye.out.json");
            return registers;
        } catch (Exception e) {
            e.printStackTrace();
            modbusMaster.disconnect();
            return LogRegister.readLogRegister(sequence, "deye.out.json");
        }
    }

    @Override
    public ModbusEndpoint getEndpoint() {
        return ModbusEndpoint.DEYE;
    }
}
