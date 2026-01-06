package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.TinyMultiblockLibForge;
import net.nikdo53.tinymultiblocklib.block.entity.SimpleMultiblockEntity;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;
import net.nikdo53.tinymultiblocklib.mixin.BlockEntityTypeAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleMultiBlock extends AbstractMultiBlock{
    public SimpleMultiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        return IMultiBlock.posStreamToList(BlockPos.betweenClosedStream(center.east().north().above(), center.west().south()));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        var type = TinyMultiblockLibForge.SIMPLE_MULTIBLOCK_ENTITY.get();

        if (!type.getValidBlocks().contains(this)) {
            Set<Block> validBlocks = new HashSet<>(type.getValidBlocks());
            validBlocks.add(this);
            ((BlockEntityTypeAccessor) type).tinymultiblocklib$setValidBlocks(validBlocks);
        }

        return TinyMultiblockLibForge.SIMPLE_MULTIBLOCK_ENTITY.get().create(pos, state);
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return RenderShape.MODEL;
    }

}
