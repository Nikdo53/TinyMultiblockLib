package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

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
        BlockState blockState = getBlock().defaultBlockState().setValue(AbstractMultiBlock.CENTER, true);

        if (getDirectionProperty() == null) return blockState;
        return blockState.trySetValue(getDirectionProperty(), direction);
    };

    /**
     * Prepares all blocks to be previewed
     * */
    default List<Pair<BlockPos, BlockState>> getPreviewStates(BlockPos posOriginal, BlockState stateOriginal){
        List<Pair<BlockPos, BlockState>> list = new ArrayList<>();

        getFullBlockShapeNoCache(posOriginal, stateOriginal).forEach(posNew -> {

            posNew = posNew.immutable();
            BlockState stateNew = stateOriginal.setValue(AbstractMultiBlock.CENTER, posOriginal.equals(posNew));
            if (getStateFromOffset() != null) stateNew = getStateFromOffset().apply(stateNew, posNew.subtract(posOriginal));

            list.add(new Pair<>(posNew, stateNew));
        });

        return list;
    }
}
