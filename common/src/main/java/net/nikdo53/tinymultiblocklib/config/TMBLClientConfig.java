package net.nikdo53.tinymultiblocklib.config;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class TMBLClientConfig {
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.BooleanValue DISABLE_MULTIBLOCK_PREVIEWS;
    public static final ForgeConfigSpec.BooleanValue PREVIEWS_FOR_EVERYTHING;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> PREVIEW_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> PREVIEW_BLACKLIST;



    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DISABLE_MULTIBLOCK_PREVIEWS = builder
                .comment("Disables ghost previews when trying to place a multiblock")
                .translation("tinymultiblocklib.configuration.disable_multiblock_previews")
                .define("Disable Multiblock Previews", false);

        PREVIEWS_FOR_EVERYTHING = builder
                .comment("Enables previews for everything, Used for debugging")
                .translation("tinymultiblocklib.configuration.previews_for_everything")
                .define("Previews for Everything", false);

        PREVIEW_WHITELIST = builder
                .comment("List of blocks for which to show previews")
                .translation("tinymultiblocklib.configuration.preview_whitelist")
                .defineList("Preview Whitelist", new ArrayList<>(), TMBLClientConfig::validateBlockName);

        PREVIEW_BLACKLIST = builder
                .comment("List of blocks for which to hide previews")
                .translation("tinymultiblocklib.configuration.preview_blacklist")
                .defineList("Preview Blacklist", new ArrayList<>(), TMBLClientConfig::validateBlockName);



        CLIENT_CONFIG = builder.build();
    }

    public static boolean validateBlockName(final Object object){
        if (object instanceof String s){
            ResourceLocation parse = ResourceLocation.tryParse(s);
            if (parse != null){
               return BuiltInRegistries.BLOCK.containsKey(parse);
            }
        }
        return false;
    }
}
