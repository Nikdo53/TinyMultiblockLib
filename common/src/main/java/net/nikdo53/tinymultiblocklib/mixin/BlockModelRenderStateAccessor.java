package net.nikdo53.tinymultiblocklib.mixin;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockModelRenderState.class)
public interface BlockModelRenderStateAccessor {

    @Accessor
    RenderType getRenderType();

    @Accessor
    void setRenderType(RenderType renderType);
}
