package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;

import javax.annotation.Nullable;

//TODO: Finish this
public class GhostMultiBlockRenderer extends GhostRenderer{
    BlockState blockState;
    @Nullable
    AbstractMultiBlockEntity multiBlockEntity;

    public GhostMultiBlockRenderer(BlockPos pos, int ticksRemaining, Block block) {
        super(Vec3.atLowerCornerOf(pos), ticksRemaining);
        blockState = block.defaultBlockState();
    }

    @Override
    public void render(float partialTick, CameraRenderState camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
/*        ((IMultiBlock) blockState.getBlock()).getFullBlockShapeNoCache(posEither, blockState).forEach(pos1 -> {

        });*/
    }
}
