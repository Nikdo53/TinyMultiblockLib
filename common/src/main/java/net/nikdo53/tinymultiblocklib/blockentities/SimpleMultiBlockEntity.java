package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.CommonRegistration;

public class SimpleMultiBlockEntity extends AbstractMultiBlockEntity {
    public SimpleMultiBlockEntity(BlockPos pos, BlockState state) {
        super(CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY.get(), pos, state);
    }
}
