package de.roeth.modbus;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.Register;

public class ModbusRegisterIO {
    public static Register[] readRegisters(ModbusEndpoint endpoint, ModbusCallSequence sequence) {
        ModbusSerialMaster modbusMaster = new ModbusSerialMaster(endpoint.getParameter());
        try {
            modbusMaster.connect();
            Register[] registers = modbusMaster.readMultipleRegisters(endpoint.slave, sequence.startRegister, sequence.length());
            modbusMaster.disconnect();
            return registers;
        } catch (Exception e) {
            modbusMaster.disconnect();
        }
        return null;
    }
}
