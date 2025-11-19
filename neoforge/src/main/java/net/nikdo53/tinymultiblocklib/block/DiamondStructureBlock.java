package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiamondStructureBlock extends AbstractStructureMultiBlock{
    public static final VoxelShape SHAPE = Shapes.box(0, 0, -1, 2, 2, 1);

    public DiamondStructureBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestMultiblockEntity(pos, state);
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state) {
        return state.getValue(CENTER) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public List<BlockPos> makeFullBlockShape(@Nullable Direction direction, BlockPos center, BlockState state) {
        return IMultiBlock.posStreamToList(BlockPos.betweenClosedStream(center, center.above().north().east()));
    }

    public static BlockPattern getBlockPattern() {
        return BlockPatternBuilder.start()
                .aisle("xx", "xx")
                .aisle("xx", "xx")
                .where('x', blockInWorld -> blockInWorld.getState().is(Blocks.DIAMOND_BLOCK))
                .build();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return voxelShapeHelper(state, level, pos, SHAPE);
    }
}
