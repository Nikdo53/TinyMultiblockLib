package net.nikdo53.tinymultiblocklib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMultiBlock extends Block implements IMultiBlock, EntityBlock {
    public static final BooleanProperty CENTER = BooleanProperty.create("center");

    public AbstractMultiBlock(Properties properties) {
        super(properties);
        if (getDirectionProperty() != null){
            this.registerDefaultState(this.getStateDefinition().any().setValue(CENTER, false).setValue(getDirectionProperty(), Direction.NORTH));
        } else {
            this.registerDefaultState(this.getStateDefinition().any().setValue(CENTER, false));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacementHelper(context);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        if (isCenter(state)) return RenderShape.ENTITYBLOCK_ANIMATED;
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CENTER);
        if (getDirectionProperty() != null) builder.add(getDirectionProperty());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (state.getValue(CENTER)) {
            place(level, pos, state);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        if (isBroken(level, pos, state))
            fixTick(state, level , pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return updateShapeHelper(state, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSurviveHelper(state, level, pos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        preventCreativeDrops(player, level, pos);
        super.playerWillDestroy(level, pos, state, player);
    }



}
