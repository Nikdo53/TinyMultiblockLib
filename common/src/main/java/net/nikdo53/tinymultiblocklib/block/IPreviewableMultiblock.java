package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public interface IPreviewableMultiblock extends IMultiBlock {
    /**
     * May save performance and fix translucency glitches if your preview only uses a block entity renderer.
     * Usually it's easier to override {@link BlockBehaviour#getRenderShape(BlockState)} instead
     * */
    default boolean skipJsonRendering() {
        return false;
    }

    /**
     * Returns the default BlockState that will be used for previews
     * */
    default BlockState getDefaultStateForPreviews(Direction direction) {
        BlockState blockState = self().defaultBlockState().setValue(AbstractMultiBlock.CENTER, true);

        if (getDirectionProperty() == null) return blockState;
        return blockState.trySetValue(getDirectionProperty(), direction);
    };

    /**
     * Allows changing the blocks block entity before it gets rendered as a preview.
     * This block entity matches the one your {@link net.minecraft.world.level.block.EntityBlock#newBlockEntity(BlockPos, BlockState)} returns,
     * so feel free to cast it.
     * */
    default BlockEntity getBlockEntityForPreviews(BlockEntity entity, Level level, BlockPos blockPos) {
        return entity;
    }

    private Block self(){
        if (this instanceof Block block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block");
        }
    }
}
