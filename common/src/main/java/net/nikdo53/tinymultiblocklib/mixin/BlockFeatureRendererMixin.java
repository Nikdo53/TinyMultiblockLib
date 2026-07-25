package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.*;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.feature.BlockFeatureRenderer;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.client.MovingBlockRenderStateAdvanced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFeatureRenderer.class)
public class BlockFeatureRendererMixin {

    @Inject(method = "renderMovingBlockSubmits", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSeed(Lnet/minecraft/core/BlockPos;)J"))
    private void renderMovingBlockSubmits(SubmitNodeCollection nodeCollection,
                                          MultiBufferSource.BufferSource bufferSource, BlockStateModelSet blockStateModelSet, OptionsRenderState optionsState, boolean translucent, CallbackInfo ci,
                                          @Local(name = "movingBlockRenderState") MovingBlockRenderState state, @Local(name = "poseStack") PoseStack poseStack, @Local(name = "blockOutput") LocalRef<BlockQuadOutput> output) {

        if (state instanceof MovingBlockRenderStateAdvanced renderStateCull){
            output.set((x1, y1, z1, quad, inst) -> tinyMultiblockLib$putBakedQuadCustomRenderType(poseStack, bufferSource, x1, y1, z1, quad, inst, renderStateCull));
        }

    }

    @Definition(id = "flag", local = @Local(type = boolean.class, name = "translucent"))
    @Expression("? == flag")
    @WrapOperation(method = "renderMovingBlockSubmits", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static boolean renderMovingBlockSubmits(boolean left, boolean right, Operation<Boolean> original, @Local(name = "model") BlockStateModel model, @Local(name = "movingBlockRenderState") MovingBlockRenderState movingBlockRenderState) {
        if (movingBlockRenderState instanceof MovingBlockRenderStateAdvanced renderState) {
            return renderState.hasMaterialFlagWrap(model, right);
        }
        return original.call(left, right);
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
