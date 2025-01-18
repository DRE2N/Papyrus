package de.erethon.papyrus;


import org.bukkit.attribute.Attribute;

public enum PDamageType {

    AIR(Attribute.ADV_AIR, Attribute.RES_AIR),
    EARTH(Attribute.ADV_EARTH, Attribute.RES_EARTH),
    FIRE(Attribute.ADV_FIRE, Attribute.RES_FIRE),
    MAGIC(Attribute.ADV_MAGIC, Attribute.RES_MAGIC),
    PHYSICAL(Attribute.ADV_PHYSICAL, Attribute.RES_PHYSICAL),
    WATER(Attribute.ADV_WATER, Attribute.RES_WATER),
    PURE(Attribute.ADV_PURE, Attribute.RES_PURE);

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
