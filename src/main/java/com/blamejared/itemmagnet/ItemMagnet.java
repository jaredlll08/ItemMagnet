package com.blamejared.itemmagnet;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

public class ItemMagnet extends JavaPlugin {

    public static ItemMagnet INSTANCE;

    public static final String MAGNET_ITEM_ID = "BlameJared_ItemMagnet_ItemMagnet";

    public final Config<ItemMagnetConfig> config;

    public ItemMagnet(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        config = this.withConfig(ItemMagnetConfig.CODEC);
    }

    @Override
    protected void setup() {
        super.setup();
        this.config.save();
        this.getEntityStoreRegistry().registerSystem(new MagnetSystem());
    }
}
