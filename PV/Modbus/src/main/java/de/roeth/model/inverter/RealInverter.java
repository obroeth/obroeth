package de.roeth.model.inverter;

import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusEndpoint;
import de.roeth.modbus.ModbusRegister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class RealInverter extends Inverter {

    private final Map<String, ModbusCall> modbusCallByName = new HashMap<>();
    private ArrayList<ModbusCall> modbusCalls;
    private ArrayList<ModbusCallSequence> modbusCallSequences;
    private ModbusRegister register;

    public RealInverter(String name) {
        super(name);
    }

    public abstract ArrayList<ModbusCall> loadModbusCallSpecification() throws IOException;

    public abstract ArrayList<ModbusCallSequence> loadModbusCallSequenceSpecification() throws IOException;

    public abstract ModbusEndpoint getEndpoint();

    @Override
    public void update() throws IOException {
        modbusCalls = loadModbusCallSpecification();
        modbusCallSequences = loadModbusCallSequenceSpecification();
        modbusCallByName.clear();
        register = new ModbusRegister();
        for (ModbusCallSequence sequence : modbusCallSequences) {
            register.fillRegister(sequence, readRegister(sequence));
        }
        performOnCalls();
        createNameMap();
    }

    private void createNameMap() {
        for (ModbusCall call : modbusCalls) {
            modbusCallByName.put(call.name, call);
        }
    }

    private void performOnCalls() {
        for (ModbusCall call : modbusCalls) {
            for (int i : call.register) {
                call.addValue((register.getRegister(i, call.cachable, call.resetCacheAtNewDay)));
            }
        }
    }

    public ModbusCall modbusCallByName(String name) {
        return modbusCallByName.get(name);
    }

    private ModbusCall makeFake(int i) {
        ModbusCall fake = new ModbusCall();
        // PV Power Total
        if (i == modbusCalls.size()) {
            fake.name = "pv_power_total";
            fake.addValue(modbusCallByName("pv_power_1").value() + modbusCallByName("pv_power_2").value());
            fake.unit = "W";
            fake.scale = 1;
        }
        return fake;
    }

    @Override
    public String getPropertyName(int i) {
        if (i < modbusCalls.size()) {
            return modbusCalls.get(i).name;
        }
        return makeFake(i).name;
    }

    @Override
    public int getPropertyValue(int i) {
        if (i < modbusCalls.size()) {
            return modbusCalls.get(i).value();
        }
        return makeFake(i).value();
    }

    @Override
    public double getPropertyScaledValue(int i) {
        if (i < modbusCalls.size()) {
            return modbusCalls.get(i).scaledValue();
        }
        return makeFake(i).scaledValue();
    }

    @Override
    public String getPropertyPretty(int i) {
        if (i < modbusCalls.size()) {
            return modbusCalls.get(i).pretty();
        }
        return makeFake(i).pretty();
    }

    @Override
    public int getPropertyLength() {
        return modbusCalls.size() + 1;
    }
}
