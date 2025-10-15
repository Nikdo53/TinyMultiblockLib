package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.BlockLike;

import java.util.ArrayList;
import java.util.List;

public interface IMovableMultiblock extends IExpandingMultiblock {

    default void moveMultiblock(Level level, BlockPos pos, BlockState state, Direction direction){
        BlockPos center = IMultiBlock.getCenter(level, pos);
        BlockPos centerMoved = center.relative(direction);

        List<BlockPos> fullBlockShape = getFullBlockShape(center, state, level);
        fullBlockShape.forEach(pos1 -> IMultiBlockEntity.setPlaced(level, pos1, false));

        List<BlockLike> originalBlocks = gatherStates(level, center, state);

        fullBlockShape.forEach(pos1 -> level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 66));

        originalBlocks.forEach(blockLike -> {
            BlockPos posMoved = blockLike.pos.relative(direction);
            BlockEntity blockEntity = blockLike.blockEntity;


            level.setBlock(posMoved, blockLike.state, 66);

            if (blockEntity instanceof IMultiBlockEntity multiBlockEntity)
                multiBlockEntity.setCenter(centerMoved);

           if (blockEntity != null) level.setBlockEntity(blockLike.blockEntity);
        });


        getFullBlockShape(centerMoved, state, level).forEach(pos1 -> IMultiBlockEntity.setPlaced(level, pos1, true));
    }

    default List<BlockLike> gatherStates(Level level, BlockPos center, BlockState state){
        List<BlockLike> list = new ArrayList<>();

        getFullBlockShape(center, state, level).forEach(pos -> list.add(new BlockLike(pos, level.getBlockState(pos), level.getBlockEntity(pos))));

        return list;
    }

}
