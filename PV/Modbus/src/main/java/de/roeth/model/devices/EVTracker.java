package de.roeth.model.devices;

import de.roeth.model.Entity;
import de.roeth.model.EntityInfo;
import de.roeth.model.inverter.Deye;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class EVTracker extends Entity {

    private final Deye deye;
    private final int powerThreshold = -2300;
    private final long necessaryTimeOverThreshold = 20000;
    public boolean evStatus = false;
    private long lastTimeNotSufficientPower = new Date().getTime();

    public EVTracker(Deye deye) {
        super("ev");
        this.deye = deye;
    }

    @Override
    public void update() throws IOException {
        checkEVStatus();
    }

    private void checkEVStatus() {
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

    @Override
    public ArrayList<EntityInfo> snapshotInfo() {
        ArrayList<EntityInfo> infos = new ArrayList<>();
        infos.add(new EntityInfo("station", 0, evStatus ? "ON" : "OFF"));
        return infos;
    }

    @Override
    public ArrayList<String> influxWhitelist() {
        ArrayList<String> whitelist = new ArrayList<>();
        whitelist.add("station");
        return whitelist;
    }
}
