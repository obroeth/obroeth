package de.roeth.model;

import de.roeth.model.input.DeviceProperty;
import de.roeth.model.input.DevicePropertyCache;
import de.roeth.utils.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Device {

    public final String name;
    protected final Map<String, DeviceProperty> propertyByName;
    protected List<DeviceProperty> deviceProperties;

    public Device(String name) {
        this(name, new ArrayList<>());
    }

    public Device(String name, List<DeviceProperty> deviceProperties) {
        this.name = name;
        this.deviceProperties = deviceProperties;
        this.propertyByName = new HashMap<>();
        updateMapByName();
        SystemUtils.debug(this, "Created device <" + name + "> with <" + deviceProperties.size() + "> properties.");
    }

    public boolean hasProperty(String key) {
        return propertyByName.containsKey(key);
    }

    public DeviceProperty getProperty(String key) {
        return propertyByName.get(key);
    }

    public double getNumericPropertyOrZero(String property) {
        return hasProperty(property) ? getProperty(property).numericPayload() : 0.;
    }

    public List<DeviceProperty> getDeviceProperties() {
        return deviceProperties;
    }

    public void clearProperties() {
        deviceProperties.clear();
    }

    protected void updateMapByName() {
        propertyByName.clear();
        for (DeviceProperty deviceProperty : deviceProperties) {
            propertyByName.put(deviceProperty.name(), deviceProperty);
        }
        SystemUtils.debug(this, "Updated map by name of <" + name + ">.");
    }

    public abstract void update() throws IOException;

    public abstract String getCacheFile();

    public void loadFromCache(String cacheFile) {
        deviceProperties = DevicePropertyCache.read(cacheFile);
        SystemUtils.debug(this, "Loaded cache of <" + name + "> with <" + deviceProperties.size() + "> properties.");
        updateMapByName();
    }

    public void saveToCache(String cacheFile) throws IOException {
        DevicePropertyCache.write(cacheFile, deviceProperties);
        SystemUtils.debug(this, "Save cache of <" + name + "> to path: " + new File(cacheFile).getAbsolutePath());
    }

}
