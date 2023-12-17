package de.roeth.model;

public abstract class Entity {

    public Entity(String name) {
        this.name = name;
    }

    public String name;

    public abstract int getPropertyLength();
    public abstract String getPropertyName(int i);
    public abstract int getPropertyValue(int i);
    public abstract String getPropertyPrettyValue(int i);

}
