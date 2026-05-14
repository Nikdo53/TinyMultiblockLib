package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.client.RenderUtils;
import org.jspecify.annotations.Nullable;

public class GhostBlockRenderer extends GhostRenderer{
    protected BlockState state;
    protected RenderType renderType = Sheets.translucentBlockSheet();
    protected boolean checkSides = true;
    protected @Nullable Holder<Biome> biome = null;

    public GhostBlockRenderer(BlockPos pos, int ticksRemaining, BlockState state){
        super(pos, ticksRemaining);
        this.state = state;
    }

    @Override
    public void render(float partialTick, CameraRenderState camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
        MovingBlockRenderState renderState = RenderUtils.createMovingBlockRenderState(level, getBlockPos(), state, checkSides, renderType, packedLight, biome);
        submitNodeCollector.submitMovingBlock(poseStack, renderState);
    }

    public GhostBlockRenderer setRenderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public GhostBlockRenderer setCheckSided(boolean checkSides) {
        this.checkSides = checkSides;
        return this;
    }

    public GhostBlockRenderer setBiome(@Nullable Holder<Biome> biome) {
        this.biome = biome;
        return this;
    }

}
