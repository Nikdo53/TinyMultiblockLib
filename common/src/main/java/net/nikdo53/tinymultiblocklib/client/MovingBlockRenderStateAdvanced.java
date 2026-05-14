package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;


public class MovingBlockRenderStateAdvanced extends MovingBlockRenderState {
    public boolean cull;
    public BlockAndTintGetter level;
    public RenderType renderType;
    public Integer packedLight = null;

    public MovingBlockRenderStateAdvanced(BlockAndTintGetter level, boolean cull, RenderType renderType) {
        this.level = level;
        this.cull = cull;
        this.renderType = renderType;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return level.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return level.getFluidState(pos);
    }

    @Override
    public int getBrightness(LightLayer layer, BlockPos pos) {
        if (packedLight == null)
            return super.getBrightness(layer, pos);

        if (layer == LightLayer.BLOCK)
            return LightCoordsUtil.block(packedLight);

        return LightCoordsUtil.sky(packedLight);
    }
}
