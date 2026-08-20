package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeDataKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @WrapOperation(method = "renderHitOutline", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V")
    )
    private void tryRenderMultiblockOutline(
            PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha, Operation<Void> original,
            @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockPos pos
    ) {
        if (state.getBlock() instanceof IMultiBlock multiBlock) {
            if (!tinyMultiblockLib$actuallyRenderTheOutline(poseStack, consumer, x, y, z, red, green, blue, alpha, original, state, pos, multiBlock))
                return;
        }

        original.call(poseStack, consumer, shape, x, y, z, red, green, blue, alpha);

    }

    @Unique
    //returns true if the original outline should be rendered too
    private static boolean tinyMultiblockLib$actuallyRenderTheOutline(PoseStack poseStack, VertexConsumer consumer, double x, double y, double z, float red, float green, float blue, float alpha, Operation<Void> original, BlockState state, BlockPos pos, IMultiBlock multiBlock) {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;

        MultiblockShape multiblockShape = multiBlock.getFullBlockShape(level, pos, state);
        BlockPos offset = multiblockShape.getOffset(pos).multiply(-1);
        MultiblockShape.Entry entry = multiblockShape.getEntry(offset);

        VoxelShape jointVoxelShape = multiblockShape.getJointVoxelShape(level, state);
        if (jointVoxelShape.isEmpty()) return true;

        original.call(poseStack, consumer, jointVoxelShape.move(offset.getX(), offset.getY(), offset.getZ()), x, y, z, red, green, blue, alpha);
        if (entry == null) return false;
        return entry.getDataOrDefault(ShapeDataKey.STANDALONE_VOXEL_SHAPE, false);
    }

}
