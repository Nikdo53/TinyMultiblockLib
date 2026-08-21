package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.CommonRegistration;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeDataKey;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.SharedStatePropertiesBuilder;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeContext;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMultiblock extends Block implements IMovableMultiblock {
    /**
     * The BlockState of the multiblocks center block, ideally you should forward all logic to this block
     * <p>
     * Note that even though it's called "CENTER", it isn't necessarily the actual center.
     * The center is just where the block would be placed if it were just a single block
     * @see #isCenter(BlockState)
     * @see #getCenter(BlockGetter, BlockPos)
     * */
    public static final BooleanProperty CENTER = BooleanProperty.create("center");
    private final SharedStatePropertiesBuilder SHARED_STATE_BUILDER = new SharedStatePropertiesBuilder();
    protected @Nullable ShapeContext.Properties shapeProperties = null;

    public BaseMultiblock(Properties properties) {
        super(properties);
        if (getDirectionProperty() != null){
            this.registerDefaultState(this.getStateDefinition().any().setValue(CENTER, true).setValue(getDirectionProperty(), Direction.NORTH));
        } else {
            this.registerDefaultState(this.getStateDefinition().any().setValue(CENTER, true));
        }

        if (!hasCustomBE())
            addToValidBEBlocks();
    }

    @Override
    public ShapeContext.@Nullable Properties getShapeProperties() {
        return shapeProperties;
    }

    @Override
    public void setShapeProperties(ShapeContext.Properties properties) {
        this.shapeProperties = properties;
    }

    @Override
    public SharedStatePropertiesBuilder getSharedStatePropertiesBuilder() {
        return SHARED_STATE_BUILDER;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacementHelper(context);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return getMultiblockRenderShape(state, IMultiBlock.isCenter(state));
    }

    /**
     * If your block is a JSON model, return {@link RenderShape#MODEL}
     * <p>
     * If your block has a BlockEntity renderer, return {@link RenderShape#INVISIBLE} for that specific block and  {@link RenderShape#INVISIBLE} everywhere else
     * */
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter){
        return RenderShape.MODEL;
    };

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
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return updateShapeHelper(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSurviveHelper(state, level, pos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        preventCreativeDrops(player, level, pos);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        if (getDirectionProperty() != null) {
            Direction currentDirection = state.getValue(getDirectionProperty());
            return state.setValue(getDirectionProperty(), rotation.rotate(currentDirection));
        }
        return super.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (getDirectionProperty() != null) {
            Direction currentDirection = state.getValue(getDirectionProperty());
            return state.setValue(getDirectionProperty(), mirror.getRotation(currentDirection).rotate(currentDirection));
        }
        return super.mirror(state, mirror);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        MultiblockShape fullBlockShape = getFullBlockShape(level, pos, state);
        BlockPos offset = IMultiBlock.getOffset(level, pos);
        MultiblockShape.Entry entry = fullBlockShape.getShape().get(offset);

        if (entry != null && entry.hasData(ShapeDataKey.VOXEL_SHAPE)){
            return entry.getData(ShapeDataKey.VOXEL_SHAPE);
        }
        return super.getShape(state, level, pos, context);
    }

    /**
     * Remember to override {@link #hasCustomBE()} when overriding, so the block doesn't get added to valid blocks for no reason
     * */
    @Override
    public @Nullable AbstractMultiBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY.get().create(pos, state);
    }

    public boolean hasCustomBE(){
        return false;
    }

    protected void addToValidBEBlocks(){
        CommonRegistration.BlockEntities.VALID_BLOCKS_SIMPLE.add(this);
        Services.PLATFORM.getRegistration().addSupportedBEBlock(CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY, this);
    }

}
