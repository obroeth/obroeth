package de.roeth.model.inverter;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusCallSpecification;
import de.roeth.modbus.ModbusEndpoint;
import de.roeth.model.Device;
import de.roeth.model.input.ModbusCall;
import de.roeth.utils.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Inverter extends Device {
    private final Map<Integer, Short> register = new HashMap<>();

    public Inverter(String name) {
        super(name);
    }

    protected abstract List<ModbusCallSpecification> loadModbusCallSpecification();

    protected abstract ArrayList<ModbusCallSequence> loadModbusCallSequenceSpecification();

    public abstract ModbusEndpoint getEndpoint();


    @Override
    public void update() throws IOException {
        SystemUtils.debug(this, "===> Start update of <" + name + ">.");
        if (fillRegister()) {
            performOnCalls();
            updateMapByName();
            saveToCache(getCacheFile());
        } else {
            loadFromCache(getCacheFile());
        }
        SystemUtils.debug(this, "<=== End update of <" + name + ">.");
    }

    public boolean fillRegister() {
        try {
            register.clear();
            for (ModbusCallSequence sequence : loadModbusCallSequenceSpecification()) {
                InputRegister[] inputRegisters = readRegister(sequence);
                for (int i = sequence.startRegister; i <= sequence.endRegister; i++) {
                    register.put(i, inputRegisters[i - sequence.startRegister].toShort());
                }
            }
            SystemUtils.debug(this, "Filled register of <" + name + "> with <" + register.size() + "> entries.");
            return true;
        } catch (Exception e) {
            SystemUtils.debug(this, "Failed to fill register of <" + name + ">!");
            return false;
        }
    }

    private void performOnCalls() {
        deviceProperties.clear();
        List<ModbusCallSpecification> modbusCallSpecifications = loadModbusCallSpecification();
        for (ModbusCallSpecification spec : modbusCallSpecifications) {
            ModbusCall call = new ModbusCall(spec);
            for (int i : spec.register) {
                call.addValue(register.get(i));
            }
            deviceProperties.add(call);
        }
        SystemUtils.debug(this, "Turned register of <" + name + "> into <" + deviceProperties.size() + "> properties.");
    }

    public abstract InputRegister[] readRegister(ModbusCallSequence sequence) throws Exception;
}
