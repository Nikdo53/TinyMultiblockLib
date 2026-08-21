package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @WrapOperation(method = "submitHitOutline", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShapeOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/client/renderer/rendertype/RenderType;IFZ)V",
            ordinal = 4)
    )
    private void wrapRenderHitOutline(SubmitNodeCollector instance, PoseStack poseStack, VoxelShape voxelShape, RenderType renderType, int i, float v, boolean b,
                                      Operation<Void> original, @Local(argsOnly = true, name = "state") BlockOutlineRenderState state) {
        original.call(instance, poseStack, voxelShape, renderType, i, v, b);
        if (state.interactionShape() != null) {
            original.call(instance, poseStack, state.interactionShape(), renderType, i, v, b);
        }
    }
}
