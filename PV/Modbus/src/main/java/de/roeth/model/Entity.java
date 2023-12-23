package de.roeth.model;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Entity {

    public String name;

    public Entity(String name) {
        this.name = name;
    }

    public abstract ArrayList<EntityInfo> snapshotInfo();

    public abstract ArrayList<String> influxWhitelist();

    public abstract void update() throws IOException;

}
