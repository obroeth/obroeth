package de.roeth.model.inverter;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.*;

import java.io.IOException;
import java.util.ArrayList;

public class Solax extends RealInverter {

    public Solax() {
        super("solax");
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
            return LogRegister.readLogRegister(sequence, "solax.out.json");
        }
    }

    @Override
    public ArrayList<ModbusCall> loadModbusCallSpecification() throws IOException {
        return ModbusFileIO.readSolaxModbusCalls();
    }

    @Override
    public ArrayList<ModbusCallSequence> loadModbusCallSequenceSpecification() throws IOException {
        return ModbusFileIO.readSolaxModbusSequences();
    }

    @Override
    public ModbusEndpoint getEndpoint() {
        return new ModbusEndpoint("/dev/ttyUSB1", 1, 9600, "none", 8, 1);
    }

    @Override
    public ArrayList<String> influxWhitelist() {
        ArrayList<String> whitelist = new ArrayList<>();
        whitelist.add("daily_production");
        return whitelist;
    }


}