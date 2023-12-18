package de.roeth.model;

public abstract class Entity {

    public String name;

    public Entity(String name) {
        this.name = name;
    }

    public abstract int getPropertyLength();

    public abstract String getPropertyName(int i);

    public abstract int getPropertyValue(int i);

    public abstract double getPropertyScaledValue(int i);

    public abstract String getPropertyPrettyValue(int i);

}
