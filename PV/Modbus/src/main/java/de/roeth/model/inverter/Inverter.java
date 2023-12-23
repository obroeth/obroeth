package de.roeth.model.inverter;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.model.Entity;
import de.roeth.model.EntityInfo;

import java.util.ArrayList;

public abstract class Inverter extends Entity {

    public Inverter(String name) {
        super(name);
    }

    public abstract int getPropertyLength();

    public abstract String getPropertyName(int i);

    public abstract int getPropertyValue(int i);

    public abstract double getPropertyScaledValue(int i);

    public abstract String getPropertyPretty(int i);

    public abstract InputRegister[] readRegister(ModbusCallSequence sequence);

    @Override
    public ArrayList<EntityInfo> snapshotInfo() {
        ArrayList<EntityInfo> info = new ArrayList<>();
        for (int i = 0; i < getPropertyLength(); i++) {
            info.add(new EntityInfo(getPropertyName(i), getPropertyScaledValue(i), getPropertyPretty(i)));
        }
        return info;
    }
}
