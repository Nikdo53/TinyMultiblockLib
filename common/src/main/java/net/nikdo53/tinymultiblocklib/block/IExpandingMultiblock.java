package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;

import java.util.Set;
import java.util.stream.Collectors;

import static net.nikdo53.tinymultiblocklib.Constants.DEBUG_ENABLED;
import static net.nikdo53.tinymultiblocklib.Constants.LOGGER;

public interface IExpandingMultiblock extends IMultiBlock {

    default boolean hasShapeChanged(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        BlockPos center = IMultiBlock.getCenter(level, pos);
        return !getfullBlockShape(center, oldState).equals(getfullBlockShape(center, state));
    }

    default void changeShape(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        if (DEBUG_ENABLED) LOGGER.warn("changeShape");
        if (level.isClientSide()) return;

        BlockPos center = IMultiBlock.getCenter(level, pos);
        Set<BlockPos> oldShape = getfullBlockShape(center, oldState).collect(Collectors.toSet());
        Set<BlockPos> shapeNew = getfullBlockShape(center, state).collect(Collectors.toSet());


        oldShape.forEach(posOld -> {
            IMultiBlockEntity.setPlaced(level, posOld, false);

            if (!shapeNew.contains(posOld)) {
                level.removeBlockEntity(posOld);
                level.setBlock(posOld, Blocks.AIR.defaultBlockState(), 2);
            }

        });

        place(level, center, state);

/*        oldShape.forEach(posOld -> {

            if (hasShrunk && !shapeNew.contains(posOld)) {
                System.out.println("sddaa");
                level.removeBlockEntity(posOld);
                level.setBlock(posOld, Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
            }

        });*/

    }

    @Override
    default void onPlaceHelper(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        if (DEBUG_ENABLED) LOGGER.warn("onPlaceHelper");

        if (IMultiBlock.isMultiblock(oldState) && hasShapeChanged(state, level, pos, oldState)) {
            changeShape(state, level, pos, oldState);
            return;
        }

        IMultiBlock.super.onPlaceHelper(state, level, pos, oldState);
    }

}
