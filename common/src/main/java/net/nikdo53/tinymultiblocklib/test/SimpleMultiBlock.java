package net.nikdo53.tinymultiblocklib.test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IPreviewableMultiblock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleMultiBlock extends AbstractMultiBlock implements IPreviewableMultiblock {
    public SimpleMultiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        int height = 2;
        return IMultiBlock.posStreamToList(BlockPos.betweenClosedStream(center.east().north().above(height), center.west().south()));
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return RenderShape.MODEL;
    }

}
