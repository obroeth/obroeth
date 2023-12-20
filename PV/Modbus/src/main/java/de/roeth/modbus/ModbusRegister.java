package de.roeth.modbus;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.roeth.communication.Utils;

import java.util.HashMap;

public class ModbusRegister {

    private final HashMap<Integer, ModbusRegisterEntry> entryMap = new HashMap<>();

    private void addRegisterEntry(ModbusRegisterEntry entry) {
        entryMap.put(entry.register, entry);
    }

    public int getRegister(int register, boolean useCache, boolean resetCacheAtNewDay) {
        if (entryMap.get(register).cached && useCache) {
            if (resetCacheAtNewDay && entryMap.get(register).cacheTime < Utils.getTodayMidnight().getTime()) {
                return 0;
            }
            return entryMap.get(register).value;
        }
        if (entryMap.get(register).cached && !useCache) {
            return 0;
        }
        return entryMap.get(register).value;
    }

    public void fillRegister(ModbusCallSequence seq, InputRegister[] registers) {
        for (int i = seq.startRegister; i <= seq.endRegister; i++) {
            if (registers[i - seq.startRegister] instanceof LogRegister) {
                LogRegister logRegister = (LogRegister) registers[i - seq.startRegister];
                addRegisterEntry(new ModbusRegisterEntry(i, registers[i - seq.startRegister].toShort(), true, logRegister.cacheTime));
            } else {
                addRegisterEntry(new ModbusRegisterEntry(i, registers[i - seq.startRegister].toShort(), false, 0));
            }
        }
    }
}
