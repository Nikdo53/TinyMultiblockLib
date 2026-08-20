package net.nikdo53.tinymultiblocklib.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.nikdo53.tinymultiblocklib.Constants;

public class TMBLTags {
    public static class BlockTags {

    }

    public static class ItemTags {
        public static final TagKey<Item> SHOW_PREVIEW = tag("show_preview");


        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MOD_ID, name));
        }
    }
}
