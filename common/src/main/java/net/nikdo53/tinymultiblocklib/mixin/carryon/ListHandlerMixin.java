package net.nikdo53.tinymultiblocklib.mixin.carryon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.common.config.ListHandler;

import java.util.List;

@Mixin(ListHandler.class)
public abstract class ListHandlerMixin {

    @Shadow(remap = false)
    private static List<TagKey<Block>> FORBIDDEN_TILES_TAGS;

    /**
     * Their Blacklist tags just don't work on certain versions, this hardcodes it to get at least 1 working
     * */
    @Inject(method = "initConfigLists", at = @At(value = "TAIL"), remap = false)
    private static void initConfigLists(CallbackInfo ci) {
        TagKey<Block> tagKey = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.parse("carryon:block_blacklist"));
        FORBIDDEN_TILES_TAGS.add(tagKey);
    }

}
