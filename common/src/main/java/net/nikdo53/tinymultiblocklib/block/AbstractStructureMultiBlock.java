package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.CommonRegistration;
import net.nikdo53.tinymultiblocklib.blockentities.IStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStructureMultiBlock extends AbstractMultiBlock {
    public AbstractStructureMultiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!state.getBlock().equals(oldState.getBlock()) && level.getBlockEntity(pos) instanceof IStructureMultiBlockEntity structureMBEntity) {
            structureMBEntity.setOldBlockState(oldState);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!(level.getBlockEntity(pos) instanceof IStructureMultiBlockEntity structureMBEntity)) {
            super.onRemove(state, level, pos, newState, movedByPiston);

            return;
        }

        BlockState oldState = structureMBEntity.getOldBlockState();
        super.onRemove(state, level, pos, newState, movedByPiston);

        level.setBlockAndUpdate(pos, oldState);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY.get().create(pos, state);
    }

    @Override
    protected void addToValidBEBlocks() {
        CommonRegistration.BlockEntities.VALID_BLOCKS_STRUCTURE.add(this);
        Services.PLATFORM.getRegistration().addSupportedBEBlock(CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY, this);

    }
}
