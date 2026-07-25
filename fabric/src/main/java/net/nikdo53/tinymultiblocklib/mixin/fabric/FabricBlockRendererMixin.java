package net.nikdo53.tinymultiblocklib.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.render.AltModelBlockRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.client.MovingBlockRenderStateAdvanced;
import net.nikdo53.tinymultiblocklib.client.PreviewRenderFixer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AltModelBlockRendererImpl.class)
public class FabricBlockRendererMixin {

    @Mutable
    @Shadow
    @Final
    private boolean cull;

    @WrapMethod(method = "tesselateBlock")
    public void tesselateBlock(QuadEmitter output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel model, long seed, Operation<Void> original) {
        if (level instanceof MovingBlockRenderStateAdvanced renderState){
            PreviewRenderFixer.PREVIEW_RENDER_TYPE = renderState.renderType;
            boolean oldCull = this.cull;
            this.cull = renderState.cull;

            original.call(output, x, y, z, level, pos, blockState, model, seed);

            this.cull = oldCull;
            PreviewRenderFixer.PREVIEW_RENDER_TYPE = null;
        } else {
            original.call(output, x, y, z, level, pos, blockState, model, seed);
        }
    }

}
