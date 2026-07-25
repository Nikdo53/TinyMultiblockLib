package net.nikdo53.tinymultiblocklib.mixin.fabric;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.client.renderer.VanillaBlockModelPartEncoder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.nikdo53.tinymultiblocklib.client.PreviewRenderFixer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(VanillaBlockModelPartEncoder.class)
public class VanillaBlockModelPartEncoderMixin {
    @Inject(method = "emitQuads",
            at = @At(value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;fromBakedQuad(Lnet/minecraft/client/resources/model/geometry/BakedQuad;)Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;",
                    shift = At.Shift.AFTER)
    )
    private static void emitQuads(BlockStateModelPart part, QuadEmitter emitter, Predicate<@Nullable Direction> cullTest, CallbackInfo ci) {
        RenderType renderType = PreviewRenderFixer.PREVIEW_RENDER_TYPE;
        if (renderType != null){
            emitter.itemRenderType(renderType);
            emitter.chunkLayer(ChunkSectionLayer.TRANSLUCENT);
        }
    }

}
