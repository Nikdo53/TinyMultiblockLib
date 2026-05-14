package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.world.level.block.state.BlockState;

public interface IStructureMultiBlockEntity extends IMultiBlockEntity {
    BlockState getOldBlockState();
    void setOldBlockState(BlockState blockState);
}
