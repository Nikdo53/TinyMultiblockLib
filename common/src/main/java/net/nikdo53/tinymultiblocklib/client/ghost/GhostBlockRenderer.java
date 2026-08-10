package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.client.RenderUtils;
import org.jetbrains.annotations.Nullable;

public class GhostBlockRenderer extends GhostRenderer{
    protected BlockState state;
    protected RenderType renderType = Sheets.translucentCullBlockSheet();
    protected boolean checkSides = true;
    protected @Nullable Holder<Biome> biome = null;

    public GhostBlockRenderer(BlockPos pos, int ticksRemaining, BlockState state){
        super(pos, ticksRemaining);
        this.state = state;
    }

    @Override
    public void render(float partialTick, Camera camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
        Minecraft.getInstance().getBlockRenderer().renderBatched(
                state,
                getBlockPos(),
                level,
                poseStack,
                buffer.getBuffer(renderType),
                checkSides,
                level.getRandom()
        );
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
