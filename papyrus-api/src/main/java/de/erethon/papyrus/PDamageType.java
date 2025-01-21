package de.erethon.papyrus;


import org.bukkit.attribute.Attribute;

public enum PDamageType {

    AIR(Attribute.ADVANTAGE_AIR, Attribute.RESISTANCE_AIR),
    EARTH(Attribute.ADVANTAGE_EARTH, Attribute.RESISTANCE_EARTH),
    FIRE(Attribute.ADVANTAGE_FIRE, Attribute.RESISTANCE_FIRE),
    MAGIC(Attribute.ADVANTAGE_MAGICAL, Attribute.RESISTANCE_MAGICAL),
    PHYSICAL(Attribute.ADVANTAGE_PHYSICAL, Attribute.RESISTANCE_PHYSICAL),
    WATER(Attribute.ADVANTAGE_WATER, Attribute.RESISTANCE_WATER),
    PURE(Attribute.ADVANTAGE_PURE, Attribute.RESISTANCE_PURE);

    private final Attribute advAttr;
    private final Attribute resAttr;

    PDamageType(Attribute advAttr, Attribute resAttr) {
        this.advAttr = advAttr;
        this.resAttr = resAttr;
    }

    public Attribute getAdvAttr() {
        return this.advAttr;
    }


    public Attribute getResAttr() {
        return this.resAttr;
    }
}
