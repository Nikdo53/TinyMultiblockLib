package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.components.SyncedStatePropertiesBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultiBlock extends Block implements IMultiBlock, EntityBlock, IExpandingMultiblock {
    /**
     * The BlockState of the multiblocks center block, ideally you should forward all logic to this block
     * <p>
     * Note that even though it's called "CENTER", it isn't necessarily the actual center.
     * The center is just where the block would be placed, if it were just a single block
     * @see #isCenter(BlockState)
     * @see #getCenter(BlockGetter, BlockPos)
     * */
    public static final BooleanProperty CENTER = BooleanProperty.create("center");
    private final SyncedStatePropertiesBuilder SYNCED_STATE_BUILDER = new SyncedStatePropertiesBuilder();

    public AbstractMultiBlock(Properties properties) {
        super(properties);
        if (getDirectionProperty() != null){
            this.registerDefaultState(this.getStateDefinition().any().setValue(CENTER, false).setValue(getDirectionProperty(), Direction.NORTH));
        } else {
            this.registerDefaultState(this.getStateDefinition().any().setValue(CENTER, false));
        }
    }

    @Override
    public SyncedStatePropertiesBuilder getSyncedStatePropertiesBuilder() {
        return SYNCED_STATE_BUILDER;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacementHelper(context);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return getMultiblockRenderShape(state);
    }

    /**
     * Sorry for forcing everyone to override this, but its kinda important for performance and fixing visual glitches
     * <p>
     * If your block is a json model, return {@link RenderShape#MODEL}
     * <p>
     * If your block has a BlockEntity renderer, return {@link RenderShape#ENTITYBLOCK_ANIMATED} for that specific block and  {@link RenderShape#INVISIBLE}
     * @see #getStateFromOffset() The function for setting a different blockstate to each block
     * */
    public abstract RenderShape getMultiblockRenderShape(BlockState state);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CENTER);
        if (getDirectionProperty() != null) builder.add(getDirectionProperty());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        onPlaceHelper(state, level, pos, oldState);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        if (isBroken(level, pos, state)) {
            fixTick(state, level, pos);
        }
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

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
}
