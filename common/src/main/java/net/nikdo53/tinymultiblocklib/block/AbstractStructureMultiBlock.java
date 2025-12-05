package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.IStructureMultiBlockEntity;

public abstract class AbstractStructureMultiBlock extends AbstractMultiBlock {
    public AbstractStructureMultiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!state.getBlock().equals(oldState.getBlock()) && level.getBlockEntity(pos) instanceof IStructureMultiBlockEntity structureMBEntity){
            structureMBEntity.setOldBlockState(oldState);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!(level.getBlockEntity(pos)  instanceof IStructureMultiBlockEntity structureMBEntity)){
            super.onRemove(state, level, pos, newState, movedByPiston);

            return;
        }

        BlockState oldState = structureMBEntity.getOldBlockState();
        super.onRemove(state, level, pos, newState, movedByPiston);

        level.setBlockAndUpdate(pos, oldState);
    }

}
