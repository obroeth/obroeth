package de.roeth.model;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusEndpoint;

public abstract class Entity {

    public String name;

    public Entity(String name) {
        this.name = name;
    }

    public abstract int getPropertyLength();

    public abstract String getPropertyName(int i);

    public abstract int getPropertyValue(int i);

    public abstract double getPropertyScaledValue(int i);

    public abstract String getPropertyPrettyValue(int i);

    public abstract InputRegister[] readRegister(ModbusCallSequence sequence);

    public abstract ModbusEndpoint getEndpoint();

}
