package net.nikdo53.tinymultiblocklib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.init.TMBLBlockEntities;

public class TestMultiblockEntity extends AbstractMultiBlockEntity {
    public TestMultiblockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TestMultiblockEntity(BlockPos pos, BlockState state) {
        super(TMBLBlockEntities.TEST_BE.get(), pos, state);
    }
}
