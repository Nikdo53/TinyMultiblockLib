package net.nikdo53.tinymultiblocklib.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.BooleanValue DISABLE_MULTIBLOCK_PREVIEWS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DISABLE_MULTIBLOCK_PREVIEWS = builder
                .comment("Disables ghost previews when trying to place a multiblock")
                .translation("tinymultiblocklib.configuration.disable_multiblock_previews")
                .define("Disable Multiblock Previews", false);

        CLIENT_CONFIG = builder.build();
    }

}
