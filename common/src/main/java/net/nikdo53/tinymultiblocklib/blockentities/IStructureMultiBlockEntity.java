package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.BlockLike;

public interface IStructureMultiBlockEntity extends IMultiBlockEntity {
    BlockState getOldBlockState();
    void setOldBlockState(BlockState blockState);
}
