package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;
import net.nikdo53.tinymultiblocklib.components.SyncedStatePropertiesBuilder;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class TestBlock extends AbstractMultiBlock implements IPreviewableMultiblock, IExpandingMultiblock {
    public TestBlock(Properties properties) {
        super(properties);
    }
    public static final VoxelShape SHAPE = makeShape();

    @Override
    public @Nullable DirectionProperty getDirectionProperty() {
        return HorizontalDirectionalBlock.FACING;
    }

    @Override
    public void createSyncedBlockStates(SyncedStatePropertiesBuilder builder) {
        super.createSyncedBlockStates(builder);
        builder.add(BlockStateProperties.AGE_3);
    }

    @Override
    public List<BlockPos> makeFullBlockShape(@Nullable Direction direction, BlockPos center, BlockState state) {
        assert direction != null;
        int size = state.getValue(BlockStateProperties.AGE_3);
        if (size < 1) size = 1;

        List<BlockPos> list = IMultiBlock.posStreamToList(BlockPos.betweenClosedStream(center.relative(Direction.NORTH, size).relative(Direction.EAST, size), center.above(size)));
        list.add(center.above().relative(direction, 3));
        return new HashSet<>(list).stream().toList();
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

        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);

/*        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_3, value).setValue(getDirectionProperty(), direction.getClockWise()));

        if (level.getBlockState(pos).equals(state) ) {
            player.displayClientMessage(Component.literal("Your action has been cancelled"), true);
        }*/

        moveMultiblock(level, pos, state, direction);

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

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
       // return voxelShapeHelper(state, level, pos, SHAPE, 0 , 0, 0, true);
    }

    public static VoxelShape makeShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(-1, 0, 0, 1, 2, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1.5, 0.75, 0.75, 1.5, 1.25, 1.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1, 0.75, 0.75, 2, 1.25, 1.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-2, 0.75, 0.75, 1, 1.25, 1.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1.5, 0.75, 0.75, 1.5, 1.25, 1.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1.5, 1.5, 0.75, 1.5, 2, 1.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1.5, 0, 0.75, 1.5, 0.5, 1.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1.5, 0.75, 1.5, 1.5, 1.25, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1.5, 0.75, 0, 1.5, 1.25, 0.5), BooleanOp.OR);

        return shape;
    }
}
