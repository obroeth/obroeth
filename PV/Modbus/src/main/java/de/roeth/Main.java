package de.roeth;


import de.roeth.communication.HomeAutomationIO;
import de.roeth.communication.InfluxIO;
import de.roeth.communication.OpenHabIO;
import de.roeth.utils.SystemUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            SystemUtils.VERBOSE = Integer.parseInt(args[0]) == 1;
        } else {
            SystemUtils.VERBOSE = false;
        }
        if (!SystemUtils.VERBOSE) {
            try {
                System.out.println("Wait a minute before start...");
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("... and go!");
        }

        long lastInfluxUpdate = 0;
        PVSystem pvSystem = new PVSystem();
        while (true) {
            long start = System.currentTimeMillis();
            try {
                // Iterate
                try {
                    pvSystem.update();
                } catch (Exception e) {
                    System.out.println("Failed to update PV System at " + new Date());
                    throw e;
                }

                // Export Openhab
                try {
                    OpenHabIO.pushToOpenhab(pvSystem);
                } catch (Exception e) {
                    System.out.println("Failed to update OH at " + new Date());
                    e.printStackTrace();
                }
                try {
                    HomeAutomationIO.pushToHomeAutomation(pvSystem);
                } catch (Exception e) {
                    System.out.println("Failed to update HA at " + new Date());
                }

                // Export Influx
                if (System.currentTimeMillis() - lastInfluxUpdate >= 60000) {
                    try {

                        lastInfluxUpdate = System.currentTimeMillis();
                        InfluxIO.pushToInflux(pvSystem);
                        pvSystem.sm.clearProperties();
                    } catch (Exception e) {
                        System.out.println("Failed to update InfluxDB at " + new Date());
                    }
                }

                try {
                    Locale locale = new Locale("de", "DE");
                    DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
                    String date = dateFormat.format(new Date());
                    OpenHabIO.curl("pv_backend_status", date);
                    HomeAutomationIO.sendSingle("backend_status", date, "backend");
                } catch (Exception e) {
                    System.out.println("Failed to do single curls at " + new Date());
                    e.printStackTrace();
                }
                // Sleep
                Thread.sleep(1);
            } catch (Exception e) {
                System.out.println(new Date() + ": Iteration FAILED:");
                e.printStackTrace();
            }
            System.out.println("Iteration successful at " + new Date() + " and took " + (System.currentTimeMillis() - start) + "ms.");
        }
    }
}
