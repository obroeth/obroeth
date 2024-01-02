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

    protected abstract List<ModbusCallSpecification> loadModbusCallSpecification() throws IOException;

    protected abstract ArrayList<ModbusCallSequence> loadModbusCallSequenceSpecification() throws IOException;

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

    private void performOnCalls() throws IOException {
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


//    private void createNameMap() {
//        for (ModbusCall call : modbusCalls) {
//            modbusCallByName.put(call.name, call);
//        }
//    }
//
//    public ModbusCall modbusCallByName(String name) {
//        return modbusCallByName.get(name);
//    }
//
//    private ModbusCall makeFake(int i) {
//        ModbusCall fake = new ModbusCall();
//        // PV Power Total
//        if (i == modbusCalls.size()) {
//            fake.name = "pv_power_total";
//            fake.addValue(modbusCallByName("pv_power_1").value() + modbusCallByName("pv_power_2").value());
//            fake.unit = "W";
//            fake.scale = 1;
//        }
//        return fake;
//    }
//
//    @Override
//    public String getPropertyName(int i) {
//        if (i < modbusCalls.size()) {
//            return modbusCalls.get(i).name;
//        }
//        return makeFake(i).name;
//    }
//
//    @Override
//    public int getPropertyValue(int i) {
//        if (i < modbusCalls.size()) {
//            return modbusCalls.get(i).value();
//        }
//        return makeFake(i).value();
//    }
//
//    @Override
//    public double getPropertyScaledValue(int i) {
//        if (i < modbusCalls.size()) {
//            return modbusCalls.get(i).scaledValue();
//        }
//        return makeFake(i).scaledValue();
//    }
//
//    @Override
//    public String getPropertyPretty(int i) {
//        if (i < modbusCalls.size()) {
//            return modbusCalls.get(i).pretty();
//        }
//        return makeFake(i).pretty();
//    }
//
//    @Override
//    public int getPropertyLength() {
//        return modbusCalls.size() + 1;
//    }

    public abstract InputRegister[] readRegister(ModbusCallSequence sequence) throws Exception;
}
