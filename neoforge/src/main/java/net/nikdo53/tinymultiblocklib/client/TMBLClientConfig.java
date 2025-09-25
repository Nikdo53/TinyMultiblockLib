package net.nikdo53.tinymultiblocklib.client;


import net.neoforged.neoforge.common.ModConfigSpec;

public class TMBLClientConfig {
    public static final ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec.BooleanValue DISABLE_MULTIBLOCK_PREVIEWS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        DISABLE_MULTIBLOCK_PREVIEWS = builder
                .comment("Disables ghost previews when trying to place a multiblock")
                .translation("tinymultiblocklib.configuration.disable_multiblock_previews")
                .define("Disable Multiblock Previews", false);

        CLIENT_CONFIG = builder.build();
    }

}
