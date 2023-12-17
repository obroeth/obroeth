package de.roeth;


import de.roeth.communication.OpenHabIO;
import de.roeth.modbus.*;
import de.roeth.model.Deye;
import de.roeth.model.Solax;
import de.roeth.model.SumInverter;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args)  {
        long lastInfluxUpdate = System.currentTimeMillis();
        while (true) {
            try {
                long start = System.currentTimeMillis();

                Deye deye = makeDeye();
                Solax solax = makeSolax();
                SumInverter sum = new SumInverter(solax, deye);

                // Export Openhab
                OpenHabIO.pushToOpenhab(deye);
                //OpenHabIO.pushToOpenhab(solax);
                OpenHabIO.pushToOpenhab(sum);

                // Export Influx
                if(System.currentTimeMillis() - lastInfluxUpdate >= 60000) {
                    lastInfluxUpdate = System.currentTimeMillis();

                }
                long duration = (int)((System.currentTimeMillis() - start) / 1000.);
                System.out.println("Iteration SUCCESS after " + duration + "ms");
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println("Iteration FAILED: " + e.getMessage());
            }
        }
    }

    private static Solax makeSolax() {
        return new Solax(new ArrayList<>(), new ModbusRegister());
    }

    private static Deye makeDeye() throws Exception {
        // 1.) Read modbus and fill register
        ArrayList<ModbusCallSequence> sequences = ModbusFileIO.readDeyeModbusSequences();
        ArrayList<ModbusCall> modbusCalls = ModbusFileIO.readDeyeModbusCalls();
        ModbusRegister modbusRegister = new ModbusRegister();
        for (ModbusCallSequence sequence : sequences) {
            modbusRegister.fillRegister(sequence, ModbusRegisterIO.readRegisters(ModbusEndpoint.DEYE, sequence));
        }

        // 2.) Create Deye and answer requests
        return new Deye(modbusCalls, modbusRegister);
    }
}
