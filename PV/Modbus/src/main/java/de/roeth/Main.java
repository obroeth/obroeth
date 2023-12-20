package de.roeth;


import de.roeth.communication.InfluxIO;
import de.roeth.communication.OpenHabIO;
import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusFileIO;
import de.roeth.modbus.ModbusRegister;
import de.roeth.model.Deye;
import de.roeth.model.Solax;
import de.roeth.model.SumInverter;

import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
//        try {
//            System.out.println("Wait a minute before start...");
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        long lastInfluxUpdate = 0;
        while (true) {
            try {
                long start = System.currentTimeMillis();
                Deye deye = makeDeye();
                Solax solax = makeSolax();
                SumInverter sum = new SumInverter(solax, deye);

                // Export Openhab
                OpenHabIO.pushToOpenhab(deye);
                OpenHabIO.pushToOpenhab(solax);
                OpenHabIO.pushToOpenhab(sum);

                // Export Influx
                if (System.currentTimeMillis() - lastInfluxUpdate >= 60000) {
                    lastInfluxUpdate = System.currentTimeMillis();
                    InfluxIO.pushToInflux(solax);
                    InfluxIO.pushToInflux(deye);
                    InfluxIO.pushToInflux(sum);
                }
//                long duration = (int) ((System.currentTimeMillis() - start) / 1000.);
//                System.out.println(new Date() + ": Iteration SUCCESS after " + duration + "ms");
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println(new Date() + ": Iteration FAILED:");
                e.printStackTrace();
            }
        }
    }

    private static Solax makeSolax() throws Exception {
        ArrayList<ModbusCallSequence> sequences = ModbusFileIO.readSolaxModbusSequences();
        ArrayList<ModbusCall> modbusCalls = ModbusFileIO.readSolaxModbusCalls();
        ModbusRegister modbusRegister = new ModbusRegister();
        Solax solax = new Solax(modbusCalls, modbusRegister);

        for (ModbusCallSequence sequence : sequences) {
            modbusRegister.fillRegister(sequence, solax.readRegister(sequence));
        }

        solax.perform();
        return solax;
    }

    private static Deye makeDeye() throws Exception {
        // 1.) Read modbus and fill register
        ArrayList<ModbusCallSequence> sequences = ModbusFileIO.readDeyeModbusSequences();
        ArrayList<ModbusCall> modbusCalls = ModbusFileIO.readDeyeModbusCalls();
        ModbusRegister modbusRegister = new ModbusRegister();
        Deye deye = new Deye(modbusCalls, modbusRegister);

        for (ModbusCallSequence sequence : sequences) {
            modbusRegister.fillRegister(sequence, deye.readRegister(sequence));
        }

        deye.perform();
        return deye;
    }
}
