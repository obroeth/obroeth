package de.roeth;

import de.roeth.model.devices.EVTracker;
import de.roeth.model.devices.SmartMeter;
import de.roeth.model.inverter.Deye;
import de.roeth.model.inverter.Solax;
import de.roeth.model.inverter.SumInverter;

import java.io.IOException;

public class PVSystem {

    public Deye deye;
    public Solax solax;
    public SumInverter sum;
    public EVTracker evTracker;
    public SmartMeter sm;

    public PVSystem() throws IOException {
        deye = new Deye();
        solax = new Solax();
        sum = new SumInverter(solax, deye);
        evTracker = new EVTracker(deye);
        sm = new SmartMeter(deye, solax);
    }

    public void update() throws IOException {
        deye.update();
        solax.update();
        sum.update();
        sm.update();
        evTracker.update();
    }

    public void tearDown() {
        sm.clearInfo();
    }

}
