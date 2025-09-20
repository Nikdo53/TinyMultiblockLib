package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

public interface IMultiBlockEntity {
    BlockPos getCenter();
    void setCenter(BlockPos pos);

    /**
     * True once the whole placing logic runs (to prevent updateShape from breaking it early)
     * */
    boolean isPlaced();
    void setPlaced(boolean placed);

    PreviewMode getPreviewMode();
    void setPreviewMode(PreviewMode mode);

    static void setPlaced(LevelReader level, BlockPos blockPos) {
        if(level.getBlockEntity(blockPos) instanceof IMultiBlockEntity entity) entity.setPlaced(true);
    }

    default boolean isCenter(){
        return getBlockEntity().getBlockPos().equals(getCenter());
    }

    default BlockEntity getBlockEntity(){
        if (this instanceof BlockEntity entity){
            return entity;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block Entity");
        }
    }
}
