package de.roeth.model.inverter;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusCallSpecification;
import de.roeth.modbus.ModbusEndpoint;
import de.roeth.modbus.ModbusFileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Deye extends Inverter {

    public Deye() {
        super("deye");
    }

    @Override
    public InputRegister[] readRegister(ModbusCallSequence sequence) throws Exception {
        ModbusSerialMaster modbusMaster = new ModbusSerialMaster(getEndpoint().getParameter());
        try {
            modbusMaster.connect();
            Register[] registers = modbusMaster.readMultipleRegisters(getEndpoint().slave, sequence.startRegister, sequence.length());
            modbusMaster.disconnect();
            return registers;
        } catch (Exception e) {
            modbusMaster.disconnect();
            throw e;
        }
    }

    @Override
    public List<ModbusCallSpecification> loadModbusCallSpecification() throws IOException {
        return ModbusFileIO.readDeyeModbusCalls();
    }

    @Override
    public ArrayList<ModbusCallSequence> loadModbusCallSequenceSpecification() throws IOException {
        return ModbusFileIO.readDeyeModbusSequences();
    }

    @Override
    public ModbusEndpoint getEndpoint() {
        return new ModbusEndpoint("/dev/ttyUSB0", 1, 9600, "none", 8, 1);
    }

    @Override
    public String getCacheFile() {
        return "deye_cache.json";
    }

//    @Override
//    public ArrayList<String> influxWhitelist() {
//        ArrayList<String> whitelist = new ArrayList<>();
//        whitelist.add("total_grid_power");
//        whitelist.add("battery_soc");
//        whitelist.add("daily_production");
//        whitelist.add("daily_energy_bought");
//        whitelist.add("total_energy_bought");
//        whitelist.add("daily_energy_sold");
//        whitelist.add("total_energy_sold");
//        return whitelist;
//    }
}
