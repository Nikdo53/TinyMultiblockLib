package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractLogicMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLogicMultiBlock extends AbstractMultiBlock{
    public AbstractLogicMultiBlock(Properties properties) {
        super(properties);
    }

    public abstract Map<BlockPos, MultiBlockLogic> makeFullBlockLogicShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction);

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        Map<BlockPos, MultiBlockLogic> logicBlockMap = makeFullBlockLogicShape(level, center, state, blockEntity, direction);
        return logicBlockMap.keySet().stream().toList();
    }

    @Override
    public List<BlockPos> getAndUpdateShapeCache(BlockState state, IMultiBlockEntity mbEntity, Level betterLevel, BlockEntity blockEntity, BlockPos center) {
        Map<BlockPos, MultiBlockLogic> logicBlockMap = makeFullBlockLogicShape(betterLevel, center, state, blockEntity, getDirection(state));

        List<BlockPos> blockPosList = logicBlockMap.keySet().stream().toList();
        mbEntity.setFullBlockShapeCache(blockPosList);

        if (mbEntity instanceof AbstractLogicMultiBlockEntity logicMultiBlock){
            logicMultiBlock.setLogicShapeCache(logicBlockMap);
        } else {
            throw new IllegalStateException("BlockEntity must be an instance of AbstractLogicMultiBlockEntity to use logic blocks");
        }

        return blockPosList;
    }
}
