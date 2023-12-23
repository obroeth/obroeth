package de.roeth;


import de.roeth.communication.InfluxIO;
import de.roeth.communication.OpenHabIO;
import de.roeth.modbus.ModbusCall;
import de.roeth.modbus.ModbusCallSequence;
import de.roeth.modbus.ModbusFileIO;
import de.roeth.modbus.ModbusRegister;
import de.roeth.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Wait a minute before start...");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("... and go!");

        long lastInfluxUpdate = 0;
        EVTracker evTracker = new EVTracker();
        while (true) {
            try {
                Deye deye = makeDeye();
                Solax solax = makeSolax();
                SumInverter sum = new SumInverter(solax, deye);
                SmartMeter sm = new SmartMeter(deye, solax);

                sm.log();
                evTracker.checkEVStatus(deye);

                // Export Openhab
                OpenHabIO.pushToOpenhab(deye);
                OpenHabIO.pushToOpenhab(solax);
                OpenHabIO.pushToOpenhab(sum);
                OpenHabIO.pushToOpenhab(evTracker);

                // Export Influx
                if (System.currentTimeMillis() - lastInfluxUpdate >= 60000) {
                    lastInfluxUpdate = System.currentTimeMillis();
                    InfluxIO.pushToInflux(solax);
                    InfluxIO.pushToInflux(deye);
                    InfluxIO.pushToInflux(sum);
                    InfluxIO.pushToInflux(evTracker);
                    InfluxIO.pushToInflux(sm);
                }
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
