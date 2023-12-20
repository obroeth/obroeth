package de.roeth.model;

import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class RealInverter extends Entity {

    private final Map<String, ModbusCall> modbusCallByName = new HashMap<>();
    public ArrayList<ModbusCall> modbusCalls;
    public ModbusRegister register;

    public RealInverter(String name, ArrayList<ModbusCall> modbusCalls, ModbusRegister register) {
        super(name);
        this.modbusCalls = modbusCalls;
        this.register = register;
    }

    public void perform() {
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
            fake.name = name + "_pv_power_total";
            fake.addValue(modbusCallByName("pv_power_1").value() + modbusCallByName("pv_power_2").value());
            fake.unit = "W";
            fake.scale = 1;
        }
        return fake;
    }

    @Override
    public String getPropertyName(int i) {
        if (i < modbusCalls.size()) {
            return name + "_" + modbusCalls.get(i).name;
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
    public String getPropertyPrettyValue(int i) {
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
