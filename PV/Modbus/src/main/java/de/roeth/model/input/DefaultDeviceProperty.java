package de.roeth.model.input;

import de.roeth.utils.SystemUtils;

public class DefaultDeviceProperty extends DeviceProperty {

    public String name = "";
    public boolean isCached = false;
    public long cacheTime = 0L;
    public boolean resetAtNewDay = false;
    public boolean toOpenhab = false;
    public String textPayload = "";
    public String defaultTextPayload = "Unset default";
    public boolean toInflux = false;
    public double numericPayload = 0;
    public double defaultNumericPayload = 0;


    public String name() {
        return name;
    }

    @Override
    public boolean toOpenhab() {
        return toOpenhab;
    }

    @Override
    public String textPayload() {
        if (isCached && resetAtNewDay && !SystemUtils.isTimeToday(cacheTime())) {
            return defaultTextPayload();
        } else {
            return textPayload;
        }
    }

    @Override
    public String defaultTextPayload() {
        return defaultTextPayload;
    }

    @Override
    public boolean toInflux() {
        return toInflux;
    }

    @Override
    public double numericPayload() {
        if (isCached && resetAtNewDay && !SystemUtils.isTimeToday(cacheTime())) {
            return defaultNumericPayload();
        } else {
            return numericPayload;
        }
    }

    @Override
    public double defaultNumericPayload() {
        return defaultNumericPayload;
    }

    @Override
    public boolean isCached() {
        return isCached;
    }

    @Override
    public long cacheTime() {
        return cacheTime;
    }

    @Override
    public boolean resetAtNewDay() {
        return resetAtNewDay;
    }

    public static class Builder {
        private final DefaultDeviceProperty prop = new DefaultDeviceProperty();

        public DefaultDeviceProperty build() {
            return prop;
        }

        public Builder resetAtNewDay(boolean reset) {
            prop.resetAtNewDay = reset;
            return this;
        }

        public Builder toOpenhab(boolean toOpenhab) {
            prop.toOpenhab = toOpenhab;
            return this;
        }

        public Builder textPayload(String payload) {
            prop.textPayload = payload;
            return this;
        }

        public Builder defaultTextPayload(String def) {
            prop.defaultTextPayload = def;
            return this;
        }

        public Builder toInflux(boolean toInflux) {
            prop.toInflux = toInflux;
            return this;
        }

        public Builder numericPayload(double payload) {
            prop.numericPayload = payload;
            return this;
        }

        public Builder defaultNumericPayload(double def) {
            prop.defaultNumericPayload = def;
            return this;
        }

        public Builder cacheTime(long cacheTime) {
            prop.cacheTime = cacheTime;
            return this;
        }

        public Builder name(String name) {
            prop.name = name;
            return this;
        }

        public Builder isCached(boolean isCached) {
            prop.isCached = isCached;
            return this;
        }
    }
}
