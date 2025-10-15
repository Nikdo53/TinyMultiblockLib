package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockLike {
    public BlockPos pos;
    public BlockState state;
    public BlockEntity blockEntity;

    public BlockLike(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        this.pos = pos;
        this.state = state;
        this.blockEntity = blockEntity;
    }

    public BlockLike(BlockPos pos, BlockState state) {
        new BlockLike(pos, state, null);
    }
}
