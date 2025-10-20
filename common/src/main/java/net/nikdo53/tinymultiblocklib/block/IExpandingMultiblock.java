package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;

import java.util.List;

public interface IExpandingMultiblock extends IMultiBlock {

    @Override
    default void onPlaceHelper(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        boolean willChangeShape = IMultiBlock.isCenter(state) && IMultiBlock.isMultiblock(oldState) && hasShapeChanged(state, level, pos, oldState);
        if (willChangeShape) {
            if (canChangeShape(state, level, pos)) {

                changeShape(state, level, pos, oldState);
                postChangeShape(state, level, pos, oldState);

            } else {
                cancelChangeShape(state, level, pos, oldState);
            }
            return;
        }

        IMultiBlock.super.onPlaceHelper(state, level, pos, oldState);
    }

    default boolean hasShapeChanged(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        BlockPos center = IMultiBlock.getCenter(level, pos);
        return !getFullBlockShape(center, oldState, level).equals(getFullBlockShapeNoCache(pos, state));
    }

    default void changeShape(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        if (level.isClientSide()) return;

        IMultiBlock.invalidateCaches(level, pos);

        BlockPos center = IMultiBlock.getCenter(level, pos);
        List<BlockPos> oldShape = getFullBlockShapeNoCache(pos, oldState);
        List<BlockPos> shapeNew = getFullBlockShape(pos, state, level);


        oldShape.forEach(posOld -> {
            IMultiBlockEntity.setPlaced(level, posOld, false);

            if (!shapeNew.contains(posOld)) {
                level.removeBlockEntity(posOld);
                level.setBlock(posOld, Blocks.AIR.defaultBlockState(), 2);
            }

        });

        place(level, center, state);
    }

    default boolean canChangeShape(BlockState state, Level level, BlockPos pos) {
        BlockPos center = IMultiBlock.getCenter(level, pos);

        return getFullBlockShapeNoCache(center, state).stream().allMatch(posNew -> {
            BlockState stateNew = level.getBlockState(posNew);

            return (stateNew.canBeReplaced() || IMultiBlock.isSameMultiblock(level, state, stateNew, center, posNew ))
                    && extraSurviveRequirements(level, posNew, state, posNew.subtract(center))
                    && (entityUnobstructed(level, posNew, state, null));
        });
    }

    default void postChangeShape(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        getFullBlockShape(pos, state, level).forEach(posNew -> IMultiBlockEntity.setPlaced(level, posNew, true));
    }

    default void cancelChangeShape(BlockState state, Level level, BlockPos pos, BlockState oldState){
        level.setBlock(pos, oldState, 2);
    }
}
