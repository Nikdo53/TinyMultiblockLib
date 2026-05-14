package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.feature.BlockFeatureRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.client.MovingBlockRenderStateAdvanced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockFeatureRenderer.class)
public class BlockFeatureRendererMixin {

    @WrapOperation(method = "renderMovingBlockSubmits", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V"))
    private static void renderMovingBlockSubmits(ModelBlockRenderer instance, BlockQuadOutput output, float x, float y, float z,
                                                 BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel model, long seed,
                                                 Operation<Void> original, @Local(name = "poseStack") PoseStack poseStack, @Local(argsOnly = true) MultiBufferSource.BufferSource bufferSource) {

        if (level instanceof MovingBlockRenderStateAdvanced renderStateCull){
            output = (x1, y1, z1, quad, inst) -> tinyMultiblockLib$putBakedQuadCustomRenderType(poseStack, bufferSource, x1, y1, z1, quad, inst, renderStateCull);
        }
        original.call(instance, output, x, y, z, level, pos, blockState, model, seed);

    }

    @Unique
    private static void tinyMultiblockLib$putBakedQuadCustomRenderType(
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource,
            float x,
            float y,
            float z,
            BakedQuad quad,
            QuadInstance instance,
            MovingBlockRenderStateAdvanced renderState
    ) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        VertexConsumer buffer = bufferSource.getBuffer(renderState.renderType);
        buffer.putBakedQuad(poseStack.last(), quad, instance);
        poseStack.popPose();
    }
}
