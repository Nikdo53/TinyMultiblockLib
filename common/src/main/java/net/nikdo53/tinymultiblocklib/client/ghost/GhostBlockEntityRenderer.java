package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.client.RenderUtils;

public class GhostBlockEntityRenderer extends GhostRenderer{
    BlockEntity blockEntity;
    protected int packedOverlay = OverlayTexture.NO_OVERLAY;

    public GhostBlockEntityRenderer(BlockPos pos, int ticksRemaining, BlockEntity blockEntity) {
        super(pos, ticksRemaining);
        this.blockEntity = blockEntity;
    }

    @Override
    public void render(float partialTick, CameraRenderState camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
        var entityRender = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);

        if (entityRender != null) {
            BlockEntityRenderState renderState = entityRender.createRenderState();
            entityRender.extractRenderState(blockEntity, renderState, partialTick, camera.pos, null);

            entityRender.submit(renderState, poseStack, submitNodeCollector, camera);
        }
    }


    public GhostBlockEntityRenderer setOverlay(int packedOverlay){
        this.packedOverlay = packedOverlay;
        return this;
    }
}
