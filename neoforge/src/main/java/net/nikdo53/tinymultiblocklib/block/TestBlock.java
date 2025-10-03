package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;
import net.nikdo53.tinymultiblocklib.components.PropertyWrapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class TestBlock extends AbstractMultiBlock implements IPreviewableMultiblock, IExpandingMultiblock {
    public TestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable DirectionProperty getDirectionProperty() {
        return HorizontalDirectionalBlock.FACING;
    }

    @Override
    public List<PropertyWrapper<?>> getSyncedStateProperties() {
        List<PropertyWrapper<?>> list = super.getSyncedStateProperties();
        list.add(PropertyWrapper.addProperty(BlockStateProperties.AGE_3));
        return list;
    }

    @Override
    public Stream<BlockPos> fullBlockShape(@Nullable Direction direction, BlockPos center, BlockState state) {
        assert direction != null;
        int size = state.getValue(BlockStateProperties.AGE_3);
        return BlockPos.betweenClosedStream(center.relative(direction.getClockWise() ,size), center.above(size).relative(direction));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AGE_3);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int value = state.getValue(BlockStateProperties.AGE_3) + 1;
        if (value > 3) {
            value = 0;
        }
        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_3, value));
        System.out.println("clicked pos = " + pos);
        return InteractionResult.SUCCESS;
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
