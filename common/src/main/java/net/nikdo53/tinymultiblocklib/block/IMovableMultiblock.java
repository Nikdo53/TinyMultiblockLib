package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.BlockLive;

import java.util.ArrayList;
import java.util.List;

public interface IMovableMultiblock extends IExpandingMultiblock {

    default void moveMultiblock(Level level, BlockPos pos, BlockState state, Direction direction){
        BlockPos center = IMultiBlock.getCenter(level, pos);
        BlockPos centerMoved = center.relative(direction);

        List<BlockPos> fullBlockShape = getFullBlockShape(level, center, state);
        fullBlockShape.forEach(pos1 -> IMultiBlockEntity.setPlaced(level, pos1, false));

        List<BlockLive> originalBlocks = gatherStates(level, center, state);

        fullBlockShape.forEach(pos1 -> level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 66));

        originalBlocks.forEach(blockLike -> blockLike.move(level, BlockPos.ZERO.relative(direction)));


        getFullBlockShape(level, centerMoved, state).forEach(pos1 -> IMultiBlockEntity.setPlaced(level, pos1, true));
    }

    default List<BlockLive> gatherStates(Level level, BlockPos center, BlockState state){
        List<BlockLive> list = new ArrayList<>();

        getFullBlockShape(level, center, state).forEach(pos -> list.add(BlockLive.fromPos(level, pos)));

        return list;
    }

}
