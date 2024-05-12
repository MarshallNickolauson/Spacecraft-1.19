package net.marsh.spacecraft.util;

import net.minecraft.util.StringRepresentable;

public enum WireConnectionType implements StringRepresentable {
    NONE,
    WIRE,
    ENERGY_INPUT,
    ENERGY_OUTPUT;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
