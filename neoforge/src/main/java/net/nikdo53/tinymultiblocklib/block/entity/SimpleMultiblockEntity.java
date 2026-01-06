package net.nikdo53.tinymultiblocklib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.TinyMultiblockLibForge;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;

public class SimpleMultiblockEntity extends AbstractMultiBlockEntity {
    public SimpleMultiblockEntity(BlockPos pos, BlockState state) {
        super(TinyMultiblockLibForge.SIMPLE_MULTIBLOCK_ENTITY.get(), pos, state);
    }
}
