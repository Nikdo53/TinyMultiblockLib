package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

public interface IPreviewableMultiblock extends IMultiBlock {
    /**
     * Returns the default BlockState that will be used for previews
     * */
    default BlockState getDefaultStateForPreviews(BlockState state, BlockPlaceContext blockPlaceContext) {
        return state;
    };

    private Block self(){
        if (this instanceof Block block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block");
        }
    }
}
