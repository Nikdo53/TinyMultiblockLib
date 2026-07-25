package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;

import java.util.Map;

public class AbstractLogicMultiBlockEntity extends AbstractMultiBlockEntity{
    public AbstractLogicMultiBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    private Map<BlockPos, MultiblockLogic> logicShapeCache = Map.of();

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        logicShapeCache.clear();
    }

    public Map<BlockPos, MultiblockLogic> getLogicShapeCache() {
        return logicShapeCache;
    }

    public void setLogicShapeCache(Map<BlockPos, MultiblockLogic> logicShapeCache) {
        this.logicShapeCache = logicShapeCache;
    }
}
