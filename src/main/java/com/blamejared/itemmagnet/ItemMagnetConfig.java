package com.blamejared.itemmagnet;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.validator.RangeValidator;

public class ItemMagnetConfig {

    public static final BuilderCodec<ItemMagnetConfig> CODEC = BuilderCodec.<ItemMagnetConfig>builder(ItemMagnetConfig.class, ItemMagnetConfig::new)
            .append(new KeyedCodec<>("PickupRadius", Codec.DOUBLE), ItemMagnetConfig::setPickupRadius, ItemMagnetConfig::pickupRadius)
            .addValidator(new RangeValidator<>(0.1, Double.MAX_VALUE, true))
            .documentation("The radius to pickup items within.")
            .add()
            .append(new KeyedCodec<>("MoveSpeed", Codec.DOUBLE), ItemMagnetConfig::setMoveSpeed, ItemMagnetConfig::moveSpeed)
            .addValidator(new RangeValidator<>(0.0, 1.0, true))
            .documentation("The speed at which the items move towards the player.")
            .add()
            .build();

    private double pickupRadius = 10;
    private double moveSpeed = 0.05;

    private void setPickupRadius(double pickupRadius) {
        this.pickupRadius = pickupRadius;
    }

    public double pickupRadius() {
        return pickupRadius;
    }

    public double moveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
}
