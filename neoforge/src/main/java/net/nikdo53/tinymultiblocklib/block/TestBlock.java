package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TestBlock extends AbstractMultiBlock implements IPreviewableMultiblock {
    public TestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable DirectionProperty getDirectionProperty() {
        return HorizontalDirectionalBlock.FACING;
    }

    @Override
    public Stream<BlockPos> fullBlockShape(@Nullable Direction direction, BlockPos center) {
        assert direction != null;
        return BlockPos.betweenClosedStream(center.relative(direction.getClockWise() ,2), center.above(2).relative(direction));
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestMultiblockEntity(pos, state);
    }
}
