package de.roeth.model.inverter;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusCallSpecification;
import de.roeth.modbus.ModbusEndpoint;
import de.roeth.modbus.ModbusFileIO;

import java.util.ArrayList;

public class Solax extends Inverter {

    public Solax() {
        super("solax");
    }

    @Override
    public InputRegister[] readRegister(ModbusCallSequence sequence) throws Exception {
        ModbusSerialMaster modbusMaster = new ModbusSerialMaster(getEndpoint().getParameter());
        try {
            modbusMaster.setTimeout(1000);
            modbusMaster.connect();
            InputRegister[] registers = modbusMaster.readInputRegisters(sequence.startRegister, sequence.length());
            modbusMaster.disconnect();
            return registers;
        } catch (Exception e) {
            modbusMaster.disconnect();
            throw e;
        }
    }

    @Override
    public ArrayList<ModbusCallSpecification> loadModbusCallSpecification() {
        return ModbusFileIO.readSolaxModbusCalls();
    }

    @Override
    public ArrayList<ModbusCallSequence> loadModbusCallSequenceSpecification() {
        return ModbusFileIO.readSolaxModbusSequences();
    }

    @Override
    public ModbusEndpoint getEndpoint() {
        return new ModbusEndpoint("/dev/ttyUSB1", 1, 9600, "none", 8, 1);
    }

    @Override
    public String getCacheFile() {
        return "solax_cache.json";
    }
}