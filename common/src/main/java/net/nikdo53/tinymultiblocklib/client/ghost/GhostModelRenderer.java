package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.client.RenderUtils;

import java.util.function.Function;

public class GhostModelRenderer extends GhostRenderer<GhostModelRenderer>{
    protected final ModelPart MODEL_PART;
    protected RenderType renderType;
    protected final TextureAtlasSprite sprite;
    protected int packedOverlay = OverlayTexture.NO_OVERLAY;

    public GhostModelRenderer(Vec3 pos, int ticksRemaining, ModelPart modelPart, RenderType renderType, TextureAtlasSprite sprite) {
        super(pos, ticksRemaining);
        this.MODEL_PART = modelPart;
        this.renderType = renderType;
        this.sprite = sprite;
    }

    @Override
    public void render(float partialTick, CameraRenderState camera, ClientLevel level, PoseStack poseStack) {
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));

        submitNodeCollector.submitModelPart(MODEL_PART, poseStack, renderType, packedLight == null ? LightCoordsUtil.FULL_BRIGHT : packedLight, packedOverlay, sprite);
    }

    public GhostModelRenderer setRenderType(RenderType renderType){
        this.renderType = renderType;
        return this;
    }

    public GhostModelRenderer setOverlay(int packedOverlay){
        this.packedOverlay = packedOverlay;
        return this;
    }
}
