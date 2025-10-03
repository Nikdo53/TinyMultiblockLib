package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IExpandingMultiblock extends IMultiBlock {

    default boolean hasShapeChanged(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        BlockPos center = IMultiBlock.getCenter(level, pos);
        return !fullBlockShape(center, oldState).equals(fullBlockShape(center, state));
    }

    default void changeShape(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        BlockPos center = IMultiBlock.getCenter(level, pos);
        Set<BlockPos> oldShape = fullBlockShape(center, oldState).collect(Collectors.toSet());
        Set<BlockPos> shapeNew = fullBlockShape(center, state).collect(Collectors.toSet());

        oldShape.forEach(posOld -> {
           // IMultiBlockEntity.setPlaced(level, posOld, false);

            if (!shapeNew.contains(posOld)) {
                level.setBlock(posOld, Blocks.AIR.defaultBlockState(), 2);
            }
        });

        place(level, center, state);

    }

    @Override
    default void onPlaceHelper(BlockState state, Level level, BlockPos pos, BlockState oldState) {

        if (IMultiBlock.isMultiblock(oldState) && hasShapeChanged(state, level, pos, oldState)) {
            changeShape(state, level, pos, oldState);
            return;
        }

        IMultiBlock.super.onPlaceHelper(state, level, pos, oldState);

    }


}
