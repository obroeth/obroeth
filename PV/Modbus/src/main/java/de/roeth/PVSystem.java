package de.roeth;

import de.roeth.model.Device;
import de.roeth.model.Evaluator;
import de.roeth.model.devices.EVTracker;
import de.roeth.model.devices.SmartMeter;
import de.roeth.model.devices.SumInverter;
import de.roeth.model.inverter.Deye;
import de.roeth.model.inverter.Solax;

import java.io.IOException;

public class PVSystem {

    public Device deye;
    public Device solax;
    public Device sum;
    public Device evTracker;
    public Device sm;
    public Evaluator evaluator;

    public PVSystem() throws IOException {
        deye = new Deye();
        solax = new Solax();
        evaluator = new Evaluator(solax, deye);
        sum = new SumInverter(evaluator);
        evTracker = new EVTracker(evaluator);
        sm = new SmartMeter(evaluator);
    }

    public void update() throws IOException {
        deye.update();
        solax.update();
        sum.update();
        sm.update();
        evTracker.update();
    }

}
