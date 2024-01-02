package de.roeth.model.input;

import de.roeth.modbus.ModbusCallSpecification;
import de.roeth.utils.FormatUtils;

import java.util.ArrayList;

public class ModbusCall extends DeviceProperty {

    private final ModbusCallSpecification specification;
    private final ArrayList<Integer> values = new ArrayList<>();

    public ModbusCall(ModbusCallSpecification specification) {
        this.specification = specification;
    }

    public void addValue(int val) {
        values.add(val);
    }

    @Override
    public String name() {
        return specification.name;
    }

    @Override
    public boolean toOpenhab() {
        return specification.toOpenhab;
    }

    @Override
    public String textPayload() {
        return FormatUtils.ONE_DIGIT.format(value()) + " " + specification.unit;
    }

    @Override
    public String defaultTextPayload() {
        return FormatUtils.ONE_DIGIT.format(defaultNumericPayload()) + " " + specification.unit;
    }

    @Override
    public boolean toInflux() {
        return specification.toInflux;
    }

    @Override
    public double numericPayload() {
        return value();
    }

    @Override
    public double defaultNumericPayload() {
        return 0;
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public long cacheTime() {
        return 0;
    }

    public double value() {
        return (values.get(0) + specification.offset) * specification.scale;
    }

    @Override
    public boolean resetAtNewDay() {
        return specification.resetCacheAtNewDay;
    }
}
