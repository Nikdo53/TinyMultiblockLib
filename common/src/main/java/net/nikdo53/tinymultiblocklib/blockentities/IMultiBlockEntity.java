package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import java.util.List;

public interface IMultiBlockEntity {
    BlockPos getOffset();
    void setOffset(BlockPos offset);

    /**
     * True once the whole placing logic runs (to prevent updateShape from breaking it early)
     * */
    boolean isPlaced();
    void setPlaced(boolean placed);

    List<BlockPos> getFullBlockShapeCache();
    void setFullBlockShapeCache(List<BlockPos> shapeCache);

    void invalidateCaches();

    PreviewMode getPreviewMode();
    void setPreviewMode(PreviewMode mode);

    static void setPlaced(LevelReader level, BlockPos blockPos, boolean placed) {
        if(level.getBlockEntity(blockPos) instanceof IMultiBlockEntity entity) entity.setPlaced(placed);
    }

    static boolean isPlaced(LevelReader level, BlockPos blockPos) {
        if(level.getBlockEntity(blockPos) instanceof IMultiBlockEntity entity) return entity.isPlaced();
        return false;
    }

    static boolean isCenter(LevelReader level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            return entity.isCenter();
        }
        return false;
    }

    default boolean isCenter(){
        return getOffset().equals(new BlockPos(0,0,0));
    }

    default void setCenter(BlockPos center){
       setOffset(getBlockEntity().getBlockPos().subtract(center));
    };

    default BlockPos getCenter(){
       return getBlockEntity().getBlockPos().subtract(getOffset());
    }

    default BlockEntity getBlockEntity(){
        if (this instanceof BlockEntity entity){
            return entity;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a BlockEntity");
        }
    }
}
