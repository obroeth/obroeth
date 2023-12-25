package de.roeth;


import de.roeth.communication.InfluxIO;
import de.roeth.communication.OpenHabIO;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

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
        PVSystem pvSystem = new PVSystem();
        while (true) {
            try {
                // Iterate
                pvSystem.update();

                // Export Openhab
                OpenHabIO.pushToOpenhab(pvSystem);

                // Export Influx
                if (System.currentTimeMillis() - lastInfluxUpdate >= 60000) {
                    lastInfluxUpdate = System.currentTimeMillis();
                    InfluxIO.pushToInflux(pvSystem);
                    pvSystem.sm.clearInfo();
                }

                Locale locale = new Locale("de", "DE");
                DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
                String date = dateFormat.format(new Date());
                OpenHabIO.curl("pv_backend_status", date);
                // Sleep
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println(new Date() + ": Iteration FAILED:");
                e.printStackTrace();
            }
        }
    }
}
