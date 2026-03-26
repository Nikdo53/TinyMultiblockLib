package net.nikdo53.tinymultiblocklib.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor
    BlockModelResolver getBlockModelResolver();

}
