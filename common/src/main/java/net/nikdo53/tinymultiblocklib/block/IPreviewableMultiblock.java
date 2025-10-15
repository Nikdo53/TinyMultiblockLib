package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
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

    private Block self(){
        if (this instanceof Block block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block");
        }
    }
}
