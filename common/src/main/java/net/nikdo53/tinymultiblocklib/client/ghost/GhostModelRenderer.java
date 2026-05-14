package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.nikdo53.tinymultiblocklib.client.RenderUtils;

import java.util.function.Function;

public class GhostModelRenderer extends GhostRenderer{
    protected final ModelPart MODEL_PART;
    protected final SpriteId MATERIAL;
    protected Function<Identifier, RenderType> renderTypeFunction = RenderTypes::entityTranslucent;
    protected int packedOverlay = OverlayTexture.NO_OVERLAY;

    public GhostModelRenderer(BlockPos pos, int ticksRemaining, ModelPart modelPart, SpriteId material) {
        super(pos, ticksRemaining);
        this.MODEL_PART = modelPart;
        this.MATERIAL = material;
    }

    @Override
    public void render(float partialTick, CameraRenderState camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        MODEL_PART.render(poseStack,
                MATERIAL.buffer(Minecraft.getInstance().getAtlasManager(), buffer, renderTypeFunction),
                RenderUtils.getPackedLight(level, getBlockPos()), packedOverlay
        );
    }

    public GhostModelRenderer setRenderType(Function<Identifier, RenderType> renderTypeFunction){
        this.renderTypeFunction = renderTypeFunction;
        return this;
    }

    public GhostModelRenderer setOverlay(int packedOverlay){
        this.packedOverlay = packedOverlay;
        return this;
    }
}
