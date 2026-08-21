package net.nikdo53.tinymultiblocklib.mixin.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeDataKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(LevelExtractor.class)
public class LevelExtractorMixinNeo {
    @WrapOperation(method = "extractBlockOutline", at = @At(value = "NEW",
            target = "(Lnet/minecraft/core/BlockPos;ZZLnet/minecraft/world/phys/shapes/VoxelShape;Ljava/util/List;)Lnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;")
    )
    private BlockOutlineRenderState wrapExtractBlockOutline(
            BlockPos pos, boolean isTranslucent, boolean highContrast, VoxelShape shape, List<CustomBlockOutlineRenderer> renderers, Operation<BlockOutlineRenderState> original, @Local(name = "state") BlockState state
    ) {
        if (state.getBlock() instanceof IMultiBlock multiBlock) {
            ClientLevel level = Minecraft.getInstance().level;
            assert level != null;

            MultiblockShape multiblockShape = multiBlock.getFullBlockShape(level, pos, state);
            BlockPos offset = multiblockShape.getOffset(pos);
            MultiblockShape.Entry entry = multiblockShape.getEntry(offset);

            VoxelShape jointVoxelShape = multiblockShape.getJointVoxelShape(level, state);
            if (jointVoxelShape.isEmpty()) return original.call(pos, isTranslucent, highContrast, shape, renderers);

            VoxelShape standAloneShape = null;
            if (entry != null) {
                if (entry.getDataOrDefault(ShapeDataKey.STANDALONE_VOXEL_SHAPE, false)){
                    standAloneShape = shape;
                };
            }

            return new BlockOutlineRenderState(pos, isTranslucent, highContrast,
                    jointVoxelShape.move(-offset.getX(), -offset.getY(),-offset.getZ()),
                    null, null,
                    standAloneShape, renderers);

        }


        return original.call(pos, isTranslucent, highContrast, shape, renderers);
    }

}
