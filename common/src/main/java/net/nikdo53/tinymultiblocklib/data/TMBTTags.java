package net.nikdo53.tinymultiblocklib.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.nikdo53.tinymultiblocklib.Constants;

public class TMBTTags {
    public static class BlockTags {
        public static final TagKey<Block> FROGS_SPAWNABLE_ON = tag("show_preview");

    }


    private static TagKey<Block> tag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
