package de.roeth.model.inverter;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.communication.OpenHabIO;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusCallSpecification;
import de.roeth.modbus.ModbusEndpoint;
import de.roeth.modbus.ModbusFileIO;
import de.roeth.model.input.DefaultDeviceProperty;
import de.roeth.utils.FormatUtils;

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
            OpenHabIO.curl("pv_backend_status_solax", "Online");
            return registers;
        } catch (Exception e) {
            modbusMaster.disconnect();
            OpenHabIO.curl("pv_backend_status_solax", "Offline");
            throw e;
        }
    }

    @Override
    public void loadFromCache(String cacheFile) {
        super.loadFromCache(cacheFile);
        deviceProperties.remove(propertyByName.get("pv_power_1"));
        deviceProperties.remove(propertyByName.get("pv_power_2"));
        deviceProperties.remove(propertyByName.get("pv_power_total"));

        DefaultDeviceProperty pvPower1 = new DefaultDeviceProperty.Builder()
                .name("pv_power_1")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(0) + " W")
                .toInflux(false)
                .numericPayload(0)
                .build();
        DefaultDeviceProperty pvPower2 = new DefaultDeviceProperty.Builder()
                .name("pv_power_2")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(0) + " W")
                .toInflux(false)
                .numericPayload(0)
                .build();
        deviceProperties.add(pvPower1);
        propertyByName.put("pv_power_1", pvPower1);
        deviceProperties.add(pvPower2);
        propertyByName.put("pv_power_2", pvPower2);
        DefaultDeviceProperty pvSum = makePvPowerSum();
        deviceProperties.add(pvSum);
        propertyByName.put("pv_power_total", pvSum);
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