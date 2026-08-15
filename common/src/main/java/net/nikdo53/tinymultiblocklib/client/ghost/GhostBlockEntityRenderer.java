package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GhostBlockEntityRenderer extends GhostRenderer<GhostBlockEntityRenderer>{
    BlockEntity blockEntity;
    protected int packedOverlay = OverlayTexture.NO_OVERLAY;

    public GhostBlockEntityRenderer(BlockPos pos, int ticksRemaining, BlockEntity blockEntity) {
        super(pos, ticksRemaining);
        this.blockEntity = blockEntity;
    }

    @Override
    public void render(float partialTick, Camera camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
        var entityRender = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);

        if (entityRender != null) {
            entityRender.render(blockEntity, partialTick, poseStack, buffer, getLight(), packedOverlay);

        }
    }


    public GhostBlockEntityRenderer setOverlay(int packedOverlay){
        this.packedOverlay = packedOverlay;
        return this;
    }
}
