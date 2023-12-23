package de.roeth.model;

import java.util.Date;

public class EVTracker {

    private final int powerThreshold = -2300;
    private final long necessaryTimeOverThreshold = 20000;
    public boolean evStatus = false;
    private long lastTimeNotSufficientPower = new Date().getTime();

    public void checkEVStatus(Deye deye) {
        Date now = new Date();
        int currentPower = deye.modbusCallByName("total_grid_power").value();
        if (currentPower > powerThreshold) {
            lastTimeNotSufficientPower = now.getTime();
            evStatus = false;
            return;
        }
        if (currentPower < powerThreshold) {
            if (lastTimeNotSufficientPower + necessaryTimeOverThreshold <= now.getTime()) {
                evStatus = true;
            } else {
                evStatus = false;
            }
        }
    }

}
