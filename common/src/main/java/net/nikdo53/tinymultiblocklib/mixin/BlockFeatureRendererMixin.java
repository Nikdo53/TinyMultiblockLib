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
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.*;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.MovingBlockFeatureRenderer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.nikdo53.tinymultiblocklib.client.MovingBlockRenderStateAdvanced;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MovingBlockFeatureRenderer.class)
public abstract class BlockFeatureRendererMixin extends RenderTypeFeatureRenderer<MovingBlockFeatureRenderer.Submit> {

    @Shadow
    @Final
    private PoseStack poseStack;

    @Inject(method = "buildGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSeed(Lnet/minecraft/core/BlockPos;)J"))
    private void renderMovingBlockSubmits(FeatureFrameContext context, List<MovingBlockFeatureRenderer.Submit> submits, CallbackInfo ci,
                                          @Local(name = "movingBlockRenderState") MovingBlockRenderState state, @Local(name = "blockOutput") LocalRef<BlockQuadOutput> output, @Local(name = "submit") MovingBlockFeatureRenderer.Submit submit) {

        if (state instanceof MovingBlockRenderStateAdvanced renderStateCull){
            output.set((x1, y1, z1, quad, inst) -> tinyMultiblockLib$putBakedQuadCustomRenderType(poseStack, x1, y1, z1, quad, inst, submit.outlineColor(), renderStateCull));
        }

    }

    @Unique
    private void tinyMultiblockLib$putBakedQuadCustomRenderType(
            final PoseStack poseStack,
            final float x,
            final float y,
            final float z,
            final BakedQuad quad,
            final QuadInstance instance,
            final int outlineColor,
            MovingBlockRenderStateAdvanced renderState

    ) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        RenderType renderType = renderState.renderType;
        VertexConsumer buffer;
        if (outlineColor != 0 && renderType.outline().isPresent()) {
            instance.setColor(outlineColor);
            buffer = this.getVertexBuilder(renderType.outline().get());
        } else {
            buffer = this.getVertexBuilder(renderType);
        }

        buffer.putBakedQuad(poseStack.last(), quad, instance);
        poseStack.popPose();
    }

}
