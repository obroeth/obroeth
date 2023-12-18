package de.roeth.modbus;

import com.ghgande.j2mod.modbus.procimg.Register;

import java.util.HashMap;

public class ModbusRegister {

    private final HashMap<Integer, ModbusRegisterEntry> entryMap = new HashMap<>();

    private void addRegisterEntry(ModbusRegisterEntry entry) {
        entryMap.put(entry.register, entry);
    }

    public int getRegister(int register) {
        return entryMap.get(register).value;
    }

    public void fillRegister(ModbusCallSequence seq, Register[] registers) {
        for (int i = seq.startRegister; i <= seq.endRegister; i++) {
            addRegisterEntry(new ModbusRegisterEntry(i, registers[i - seq.startRegister].toShort()));
        }
    }

}
