package de.erethon.papyrus;


import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public enum CraftPDamageType {

    AIR(Attributes.ADVANTAGE_AIR, Attributes.RESISTANCE_AIR, Attributes.PENETRATION_AIR,  PDamageType.AIR),
    EARTH(Attributes.ADVANTAGE_EARTH, Attributes.RESISTANCE_EARTH, Attributes.PENETRATION_EARTH, PDamageType.EARTH),
    FIRE(Attributes.ADVANTAGE_FIRE, Attributes.RESISTANCE_FIRE, Attributes.PENETRATION_FIRE, PDamageType.FIRE),
    WATER(Attributes.ADVANTAGE_WATER, Attributes.RESISTANCE_WATER, Attributes.PENETRATION_WATER, PDamageType.WATER),
    MAGIC(Attributes.ADVANTAGE_MAGICAL, Attributes.RESISTANCE_MAGICAL, Attributes.PENETRATION_MAGICAL, PDamageType.MAGIC),
    PHYSICAL(Attributes.ADVANTAGE_PHYSICAL, Attributes.RESISTANCE_PHYSICAL, Attributes.PENETRATION_PHYSICAL,PDamageType.PHYSICAL),
    PURE(Attributes.ADVANTAGE_PURE, Attributes.RESISTANCE_PURE, Attributes.PENETRATION_PURE, PDamageType.PURE);

    public final Holder<Attribute> advantage;
    public final Holder<Attribute> resistance;
    public final Holder<Attribute> penetration;
    public final PDamageType apiType;


    CraftPDamageType(Holder<Attribute> advantage, Holder<Attribute> res, Holder<Attribute> pen, PDamageType apiType) {
        this.advantage = advantage;
        this.resistance = res;
        this.penetration = pen;
        this.apiType = apiType;
    }

    public static CraftPDamageType ofAPI(PDamageType apiType) {
        switch (apiType) {
            case AIR -> {
                return CraftPDamageType.AIR;
            }
            case EARTH -> {
                return CraftPDamageType.EARTH;
            }
            case FIRE -> {
                return CraftPDamageType.FIRE;
            }
            case MAGIC -> {
                return CraftPDamageType.MAGIC;
            }
            case PHYSICAL -> {
                return CraftPDamageType.PHYSICAL;
            }
            case WATER -> {
                return CraftPDamageType.WATER;
            }
            case PURE -> {
                return CraftPDamageType.PURE;
            }
        }
        return CraftPDamageType.PURE;
    }
}
