package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IExpandingMultiblock extends IMultiBlock {

    @Override
    default void onPlaceHelper(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        boolean willChangeShape = IMultiBlock.isCenter(state) && IMultiBlock.isMultiblock(oldState) && hasShapeChanged(state, level, pos, oldState);
        if (tryChangeShape(state, level, pos, oldState, willChangeShape)) return;

        IMultiBlock.super.onPlaceHelper(state, level, pos, oldState);
    }

    default boolean tryChangeShape(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean willChangeShape) {
        if (willChangeShape) {
            if (canChangeShape(state, level, pos)) {

                changeShape(state, level, pos, oldState);
                postChangeShape(state, level, pos, oldState);

            } else {
                cancelChangeShape(state, level, pos, oldState);
            }
            return true;
        }
        return false;
    }


    @Override
    default BlockState updateShapeHelper(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {

        if (!IMultiBlock.isMultiblock(neighborState) // avoid being activated by itself
                && level instanceof Level level1
                && hasShapeChanged(state, level1, pos, state)) {
            if (!IMultiBlock.isCenter(state)) {

                // fakes the update shape even though the neighbors haven't updated
                BlockPos center = IMultiBlock.getCenter(level, pos);
                if (center.equals(pos)){
                    destroy(center, level, state, true);
                    return Blocks.AIR.defaultBlockState(); // The block has a broken state
                }

                BlockPos relative = center.relative(Direction.NORTH);
                level.getBlockState(center).updateShape(level1, level1, pos, Direction.NORTH, relative, level.getBlockState(relative), level1.getRandom());
            } else {
               if (tryChangeShape(state, level1, pos, state, true))
                   return state;
            }
        }

        return IMultiBlock.super.updateShapeHelper(state, direction, neighborState, level, pos, neighborPos);
    }


    //TODO: should the builder state change while placed?
    default boolean hasShapeChanged(BlockState state, @Nullable Level level, BlockPos pos, BlockState oldState) {
        if (level == null) return false;

        BlockPos center = IMultiBlock.getCenter(level, pos);
        Set<BlockPos> fullBlockShape = getMultiblockShape(level, center, oldState).getGlobalPositions();
        Set<BlockPos> fullBlockShapeNoCache = getMultiblockShapeNoCache(center, state, level, level.getBlockEntity(center)).getGlobalPositions();
        return !fullBlockShape.equals(fullBlockShapeNoCache);
    }

    default void changeShape(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        if (level.isClientSide()) return;
        BlockPos center = IMultiBlock.getCenter(level, pos);

        Set<BlockPos> oldShape = getMultiblockShape(level, pos, oldState).getGlobalPositions();

        IMultiBlock.invalidateCaches(level, pos);

        Set<BlockPos> shapeNew = getMultiblockShape(level, pos, state).getGlobalPositions();


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

        MultiblockShape shape = getMultiblockShapeNoCache(center, state, level, level.getBlockEntity(center));
        return shape.getGlobalPositions().stream().allMatch(posNew -> {
            BlockState stateNew = level.getBlockState(posNew);

            return (stateNew.canBeReplaced() || IMultiBlock.isSameMultiblock(level, state, stateNew, center, posNew ))
                    && extraSurviveRequirements(level, posNew, state, posNew.subtract(center), shape)
                    && (entityUnobstructed(level, posNew, state, null, shape));
        });
    }

    default void postChangeShape(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        getMultiblockShape(level, pos, state).getGlobalPositions().forEach(posNew -> IMultiBlockEntity.setPlaced(level, posNew, true));
    }

    default void cancelChangeShape(BlockState state, Level level, BlockPos pos, BlockState oldState){
        level.setBlock(pos, oldState, 2);
    }

    private Block self(){
        if (this instanceof Block block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block");
        }
    }

}
