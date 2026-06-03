package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.MultiBlockLogic;

import java.util.Map;

public class AbstractLogicMultiBlockEntity extends AbstractMultiBlockEntity{
    public AbstractLogicMultiBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    private Map<BlockPos, MultiBlockLogic> logicShapeCache = Map.of();

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        logicShapeCache.clear();
    }

    public Map<BlockPos, MultiBlockLogic> getLogicShapeCache() {
        return logicShapeCache;
    }

    public void setLogicShapeCache(Map<BlockPos, MultiBlockLogic> logicShapeCache) {
        this.logicShapeCache = logicShapeCache;
    }
}
