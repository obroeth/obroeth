package de.roeth.model.devices;

import de.roeth.model.Device;
import de.roeth.model.Evaluator;
import de.roeth.model.input.DefaultDeviceProperty;
import de.roeth.utils.FormatUtils;
import de.roeth.utils.SystemUtils;

import java.io.IOException;

public class SumInverter extends Device {

    private final Evaluator evaluator;

    public SumInverter(Evaluator evaluator) {
        super("sum");
        this.evaluator = evaluator;
    }

    @Override
    public void update() throws IOException {
        SystemUtils.debug(this, "===> Start update of <" + name + ">.");
        deviceProperties.clear();
        deviceProperties.add(makePvPower());
        deviceProperties.add(makeLoadPower());
        deviceProperties.add(makeDailyProduction());
        deviceProperties.add(makeTotalProduction());
        deviceProperties.add(makeDailyOwnUsage());
        deviceProperties.add(makeTotalOwnUsage());
        deviceProperties.add(makeDailyAutarchy());
        deviceProperties.add(makeTotalAutarchy());
        SystemUtils.debug(this, "<=== End update of <" + name + ">.");
    }

    @Override
    public String getCacheFile() {
        return "sum_cache.json";
    }

    private DefaultDeviceProperty makeTotalAutarchy() {
        double totalAutarchy = evaluator.totalAutarchy();
        return new DefaultDeviceProperty.Builder()
                .name("total_autarc")
                .toOpenhab(true)
                .textPayload(FormatUtils.ZERO_DIGIT.format(totalAutarchy * 100) + " %")
                .toInflux(true)
                .numericPayload(totalAutarchy * 100)
                .build();
    }

    private DefaultDeviceProperty makeDailyAutarchy() {
        double dailyAutarchy = evaluator.dailyAutarchy();
        return new DefaultDeviceProperty.Builder()
                .name("daily_autarc")
                .toOpenhab(true)
                .textPayload(FormatUtils.ZERO_DIGIT.format(dailyAutarchy * 100) + " %")
                .toInflux(true)
                .numericPayload(dailyAutarchy * 100)
                .build();
    }

    private DefaultDeviceProperty makeTotalOwnUsage() {
        double totalOwnUsagePercentage = evaluator.totalOwnUsagePercentage();
        return new DefaultDeviceProperty.Builder()
                .name("total_own_usage")
                .toOpenhab(true)
                .textPayload(FormatUtils.ZERO_DIGIT.format(totalOwnUsagePercentage * 100) + " %")
                .toInflux(true)
                .numericPayload(totalOwnUsagePercentage * 100)
                .build();
    }

    private DefaultDeviceProperty makeDailyOwnUsage() {
        double dailyOwnUsagePercentage = evaluator.dailyOwnUsagePercentage();
        return new DefaultDeviceProperty.Builder()
                .name("daily_own_usage")
                .toOpenhab(true)
                .textPayload(FormatUtils.ZERO_DIGIT.format(dailyOwnUsagePercentage * 100) + " %")
                .toInflux(true)
                .numericPayload(dailyOwnUsagePercentage * 100)
                .build();
    }

    private DefaultDeviceProperty makeTotalProduction() {
        double totalProduction = evaluator.totalProduction();
        return new DefaultDeviceProperty.Builder()
                .name("total_production")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(totalProduction) + " kWh")
                .toInflux(true)
                .numericPayload(totalProduction)
                .build();
    }

    private DefaultDeviceProperty makeDailyProduction() {
        double dailyProduction = evaluator.dailyProduction();
        return new DefaultDeviceProperty.Builder()
                .name("daily_production")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(dailyProduction) + " kWh")
                .toInflux(true)
                .numericPayload(dailyProduction)
                .build();
    }

    private DefaultDeviceProperty makePvPower() {
        double pvPower = evaluator.pvPower();
        return new DefaultDeviceProperty.Builder()
                .name("pv_power")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(pvPower) + " W")
                .toInflux(true)
                .numericPayload(pvPower)
                .build();
    }

    private DefaultDeviceProperty makeLoadPower() {
        double loadPower = evaluator.loadPower();
        return new DefaultDeviceProperty.Builder()
                .name("load_power")
                .toOpenhab(true)
                .textPayload(FormatUtils.ONE_DIGIT.format(loadPower) + " W")
                .toInflux(true)
                .numericPayload(loadPower)
                .build();
    }
}
