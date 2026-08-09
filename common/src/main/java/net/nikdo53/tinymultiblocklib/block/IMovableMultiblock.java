package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.BlockLive;

import java.util.HashSet;
import java.util.Set;

public interface IMovableMultiblock extends IExpandingMultiblock {

    default void moveMultiblock(Level level, BlockPos pos, BlockState state, Direction direction){
        BlockPos center = IMultiBlock.getCenter(level, pos);
        BlockPos centerMoved = center.relative(direction);

        Set<BlockPos> fullBlockShape = getFullBlockShape(level, center, state).getGlobalPositions();
        fullBlockShape.forEach(pos1 -> IMultiBlockEntity.setPlaced(level, pos1, false));

        Set<BlockLive> originalBlocks = new HashSet<>();
        fullBlockShape.forEach(pos1 -> originalBlocks.add(new BlockLive.Tag(level, pos1)));

        fullBlockShape.forEach(pos1 -> level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 66));

        originalBlocks.forEach(blockLike -> blockLike.move(level, BlockPos.ZERO.relative(direction)));


        getFullBlockShape(level, centerMoved, state).getGlobalPositions().forEach(pos1 -> IMultiBlockEntity.setPlaced(level, pos1, true));
    }

}
