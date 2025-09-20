package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;
import net.nikdo53.tinymultiblocklib.blocks.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.blocks.IPreviewableMultiblock;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TestBlock extends AbstractMultiBlock implements IPreviewableMultiblock {
    public TestBlock(Properties properties) {
        super(properties);
    }


    @Override
    public Stream<BlockPos> fullBlockShape(@Nullable Direction direction, BlockPos center) {
        return BlockPos.betweenClosedStream(center.east(2), center.above(2).north());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestMultiblockEntity(pos, state);
    }
}
