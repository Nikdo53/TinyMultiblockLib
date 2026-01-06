package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.CommonRegistration;

public class SimpleStructureMultiBlockEntity extends AbstractStructureMultiBlockEntity{
    public SimpleStructureMultiBlockEntity(BlockPos pos, BlockState state) {
        super(CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY.get(), pos, state);
    }
}
