package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockBehaviour;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.IBlockPosOffsetEnum;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeContext;
import net.nikdo53.tinymultiblocklib.util.TMBLUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.nikdo53.tinymultiblocklib.block.BaseMultiblock.CENTER;

public interface IMultiBlock extends IMBStateSharer, MultiblockBehaviour, EntityBlock {

    /** Builds the multiblocks shape along with each parts properties. Center is added automatically but can be overridden
     * <p>
     * Should only be used for overriding
     * @param builder The builder to add the shape to, .build gets called automatically
     * @param context The context of the shape, all getters should be included in the top of the method.
     * @see #getFullBlockShape(BlockGetter, BlockPos, BlockState)
     * @see #getFullBlockShapeNoCache(Level, BlockEntity, BlockPos, BlockState)
     * */
    void makeMultiblockShape(MultiblockShape.Builder builder, ShapeContext context);

    @ApiStatus.Internal
    default MultiblockLogic getDefaultCenterLogic(ShapeContext context){
        return MultiblockLogic.EMPTY;
    }

    @ApiStatus.Internal
    @Nullable ShapeContext.Properties getShapeProperties();

    @ApiStatus.Internal
    void setShapeProperties(ShapeContext.Properties properties);


    /**
     * Mojangs BetweenClosed methods return a mutable BlockPos, which breaks everything.
     * Use this helper method to convert them safely
     * */
    static List<BlockPos> posStreamToList(Stream<BlockPos> posStream){
        return new ArrayList<>(posStream.map(BlockPos::immutable).toList());
    }

    /**
     * Returns the multiblocks DirectionProperty.
     * <p>
     * Only used for multiblocks that can be rotated, otherwise returns null
     * <p>
     * Use {@link #makeDirectional()} instead.
     * */
    default @Nullable DirectionProperty getDirectionProperty(){
        DirectionContext directionContext = makeDirectional();
        return directionContext != null ? directionContext.property() : null; // null if block doesn't have directions
    }

    /**
     * Returns the multiblocks DirectionProperty and extractor function. Automatically registers the block state property, no additional setup necessary.
     * <p>
     * Only used for multiblocks that can be rotated, otherwise returns null
     * */
    default @Nullable DirectionContext makeDirectional(){
        return null; // null if block doesn't have directions
    }

    /**
     * Context for directional multiblocks.
     * @param property The DirectionProperty of the multiblock
     * @param directionExtractor The function to extract the direction from a block place context, returns null if the block cannot be placed
     * */
    record DirectionContext(DirectionProperty property, Function<BlockPlaceContext, Direction> directionExtractor){
        public static DirectionContext horizontal(){
            return new DirectionContext(HorizontalDirectionalBlock.FACING, UseOnContext::getHorizontalDirection);
        }

        public static DirectionContext allAxis(){
            return new DirectionContext(BlockStateProperties.FACING, UseOnContext::getClickedFace);
        }

    }

    default Direction getDirection(BlockState state){
        if (getDirectionProperty() != null){
            return state.getValue(getDirectionProperty());
        }
        return Direction.NORTH;
    }

    default MultiblockShape getFullBlockShapeNoCache(@Nullable Level level, @Nullable BlockEntity blockEntity, BlockPos center, BlockState state){
        if (blockEntity == null && level != null){
            blockEntity = level.getBlockEntity(center);
        }

        ShapeContext context = new ShapeContext(level, center, state, blockEntity, this);

        MultiblockShape.Builder builder = new MultiblockShape.Builder(center, getDefaultCenterLogic(context));
        makeMultiblockShape(builder, context);

        ShapeContext.Properties properties = context.getProperties();
        if (getShapeProperties() == null){
            setShapeProperties(properties);
        } else if (!getShapeProperties().equals(properties)){
            throw new IllegalStateException("Shape properties changed unexpectedly, please include any getters from the context at the top of the makeMultiblockShape method.");
        }


        return builder.build();
    }


    default MultiblockShape getFullBlockShape(BlockGetter level, BlockPos pos, BlockState state){
        BlockPos center = getCenter(level, pos);
        BlockEntity blockEntity = level.getBlockEntity(center);
        Level betterLevel = level instanceof Level ? (Level) level : null;

        assert betterLevel != null;
        if (!(blockEntity instanceof IMultiBlockEntity mbEntity)){
            return getFullBlockShapeNoCache(betterLevel, blockEntity ,center, state);
        }

        if (mbEntity.getFullBlockShapeCache().getShape().isEmpty()){
            return getAndUpdateShapeCache(state, mbEntity, betterLevel, blockEntity, center);
        }

        return mbEntity.getFullBlockShapeCache();
    }

    private MultiblockShape getAndUpdateShapeCache(BlockState state, IMultiBlockEntity mbEntity, Level betterLevel, BlockEntity blockEntity, BlockPos center) {
        MultiblockShape blockPosList = getFullBlockShapeNoCache(betterLevel, blockEntity, center, state);

        mbEntity.setFullBlockShapeCache(blockPosList);
        return blockPosList;
    }

    static List<BlockPos> getFullShape(BlockGetter level, BlockPos pos){
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IMultiBlock multiBlock){
            return multiBlock.getFullBlockShape(level, pos, state).getGlobalPositions().stream().toList();
        }

        return List.of(pos);
    }

    static void invalidateCaches(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(getCenter(level, pos)) instanceof IMultiBlockEntity blockEntity){
            blockEntity.invalidateCaches();
        }
    }

    /**
     * Changes the BlockState for each Block in this multiblock.
     * Works like GetStateForPlacement does in regular blocks
     * @see IBlockPosOffsetEnum#fromOffset(Class, BlockPos, Direction, Enum)
     * @deprecated : use the shape builder instead.
     * */
    @Deprecated
    default BlockState getStateForEachBlock(BlockState state, BlockPos pos, BlockPos centerOffset, Level level, @Nullable Direction direction){
        return state;
    }

    default void onPlaceHelper(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        verifyValidBlockEntity(level, pos);
        boolean isPlaced = IMultiBlockEntity.isPlaced(level, pos);

        if (isPlaced) shareBlockStates(level, pos, state);

        if (isCenter(state)) {
            if (!isPlaced) place(level, pos, state);
        }
    }

    /**
     * Places the multiblock, sets its BlockStates and BlockEntity center
     * */
    default void place(Level level, BlockPos centerPos, BlockState stateOriginal){
        prepareForPlace(getFullBlockShape(level, centerPos, stateOriginal), level, centerPos, stateOriginal).forEach(blockLike -> {
            int flags = 66;

            BlockState stateNew = blockLike.state;
            BlockPos posNew = blockLike.pos;

            // Don't replace identical blocks
            if (!level.getBlockState(posNew).equals(stateNew)) {
                level.setBlock(posNew, stateNew, flags);
            }

            if(level.getBlockEntity(posNew) instanceof IMultiBlockEntity entity && !entity.getCenter().equals(centerPos)) {
                entity.setCenter(centerPos);
                entity.getBlockEntity().setChanged();
            }
        });
    }
    /**
     * Prepares all blocks to be Placed
     * */
    default List<BlockLive> prepareForPlace(MultiblockShape shape, Level level, BlockPos centerPos, BlockState stateOriginal){
        List<BlockLive> list = new ArrayList<>();

        shape.getShape().forEach((posNew, entry) -> {
            posNew = posNew.immutable().offset(centerPos);

            BlockState stateNew = stateOriginal.setValue(BaseMultiblock.CENTER, centerPos.equals(posNew));
            stateNew = getStateForEachBlock(stateNew, posNew, posNew.subtract(centerPos), level, getDirection(stateOriginal));
            stateNew = entry.stateModifier().apply(stateNew);

            list.add(new BlockLive(posNew, stateNew));
        });

        return list;
    }


    default @Nullable BlockState getStateForPlacementHelper(BlockPlaceContext context) {
        DirectionContext directionContext = makeDirectional();
        Direction direction = directionContext == null ? context.getHorizontalDirection() : directionContext.directionExtractor().apply(context);
        if (direction == null)
            return null;
        return getStateForPlacementHelper(context, direction);
    }

    /**
     * Helper for {@link Block#getStateForPlacement(BlockPlaceContext)}
     * @param direction The direction the block will have when placed, ignored when {@link #getDirectionProperty()} is null
     * */
    default @Nullable BlockState getStateForPlacementHelper(BlockPlaceContext context, Direction direction) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = self().defaultBlockState().setValue(BaseMultiblock.CENTER, true);

        if (getDirectionProperty() != null){
            state = state.setValue(getDirectionProperty(), direction);
        }

        return canPlace(level, pos, state, context.getPlayer(), true) ? state : null;
    }

    default boolean canPlace(LevelReader level, BlockPos center, BlockState state, @Nullable Entity player, boolean ignoreEntities) {
        MultiblockShape shape = getFullBlockShape(level, center, state);
        return shape.getGlobalPositions().stream().allMatch(pos -> canPlaceBlock(pos, level, center, state, player, ignoreEntities, shape));
    }

    default boolean canPlaceBlock(BlockPos pos, LevelReader level, BlockPos center, BlockState state, @Nullable Entity player, boolean ignoreEntities, MultiblockShape shape) {
        return canReplaceBlock(level, pos, level.getBlockState(pos), shape)
                && extraSurviveRequirements(level, pos, state, pos.subtract(center), shape)
                && (entityUnobstructed(level, pos, state, player, shape) || ignoreEntities)
                && pos.getY() < level.getMaxBuildHeight() && pos.getY() > level.getMinBuildHeight();
    }

    default void destroy(BlockPos center, LevelAccessor level, BlockState state, boolean dropBlock){
        if (level.isClientSide()) return;
        Set<BlockPos> blocks = getFullBlockShape(level, center, state).getGlobalPositions();

        if (level.getBlockState(center).is(state.getBlock())) {
            level.destroyBlock(center, dropBlock);
        }

        blocks.forEach(pos ->{
            if (pos.equals(center)) return;

            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(state.getBlock())) {
                level.destroyBlock(pos, dropBlock);
            }
        });
    }

    default boolean allBlocksPresent(LevelReader level, BlockPos pos, BlockState state){
        if (level.isClientSide()) return true;
        BlockPos center = getCenter(level, pos);

        boolean ret = getFullBlockShape(level, center, state).getGlobalPositions().stream().allMatch(blockPos -> level.getBlockState(blockPos).is(self()));

        boolean isMultiblock = isMultiblock(level, pos);
        if (ret && level.getBlockEntity(pos) instanceof IMultiBlockEntity entity && !entity.isPlaced() && isMultiblock) {
            getFullBlockShape(level, center, state).getGlobalPositions().forEach(blockPos -> IMultiBlockEntity.setPlaced(level, blockPos, true));
        }

        return ret;
    }

    /**
     * Helper for Block.updateShape()
     * <p>
     * Destroys the multiblock if canSurvive returns false
     * */
    default BlockState updateShapeHelper(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos){
        if (!(level.getBlockEntity(pos) instanceof IMultiBlockEntity entity)) return Blocks.AIR.defaultBlockState();

        boolean canSurvive = state.canSurvive(level, pos);

        if (!canSurvive){
            destroy(entity.getCenter(), level, state, true);
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    /**
     * Helper for Block.canSurvive()
     * */
    default boolean canSurviveHelper(BlockState state, LevelReader level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity){
            //survive logic
            MultiblockShape shape = getFullBlockShape(level, pos, state);
            boolean extraSurvive = shape.getGlobalPositions().stream().allMatch(blockPos -> extraSurviveRequirements(level, blockPos, state, entity.getOffset(), shape));
            return (allBlocksPresent(level, pos, state) || !entity.isPlaced()) && extraSurvive;
        } else {
            //placement logic
            return canPlace(level, pos, state, null, false);
        }
    }

    /**
     * Should be added into {@link Block#playerDestroy(Level, Player, BlockPos, BlockState, BlockEntity, ItemStack)}
     * */
    default void preventCreativeDrops(Player player, Level level, BlockPos pos){
        if (player.isCreative() && level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            destroy(entity.getCenter(), level, level.getBlockState(pos), false);
        }
    }
    /**
     * Returns the center BlockPos of the multiblock
     * */
    static BlockPos getCenter(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity){
            return entity.getCenter();
        }
        return pos;
    }

    /**
     * Returns the offset BlockPos from center of the multiblock
     * */
    static BlockPos getOffset(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity){
            return entity.getOffset();
        }
        return new BlockPos(0,0,0);
    }
    
    static boolean isCenter(BlockState state){
        return state.getValue(CENTER);
    }

    static boolean isMultiblock(BlockState state){
        return state.getBlock() instanceof IMultiBlock;
    }

    static boolean isMultiblock(BlockGetter level, BlockPos pos){
        return isMultiblock(level.getBlockState(pos));
    }

    default VoxelShape voxelShapeHelper(BlockState state, BlockGetter level, BlockPos pos, VoxelShape shape){
        return voxelShapeHelper(state,level,pos,shape, 0, 0, 0);
    }

    default VoxelShape voxelShapeHelper(BlockState state, BlockGetter level, BlockPos pos, VoxelShape shape, float xOffset, float yOffset, float zOffset){
        return voxelShapeHelper(state,level,pos,shape, xOffset, yOffset, zOffset, false);
    }

    /**
     * Offsets each Blocks VoxelShape to the center, allowing for VoxelShapes larger than 1 block
     * @param hasDirectionOffsets Larger directional multiblocks may have their center in a different point for every rotation, this offsets the VoxelShapes accordingly
     * */
    default VoxelShape voxelShapeHelper(BlockState state, BlockGetter level, BlockPos pos, VoxelShape shape, float xOffset, float yOffset, float zOffset, boolean hasDirectionOffsets){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            double x = (-entity.getOffset().getX()) + xOffset;
            double y = (-entity.getOffset().getY()) + yOffset;
            double z = (-entity.getOffset().getZ()) + zOffset;

            if (getDirectionProperty() != null && hasDirectionOffsets) {
                switch (state.getValue(getDirectionProperty())) {
                    case EAST -> x += 1;
                    case NORTH -> {
                        x += 1;
                        z -= 1;
                    }
                    case WEST -> z -= 1;
                }
            }
            TriFunction<Double, Double, Double, VoxelShape> memoize = TMBLUtils.memoize(shape::move);
            return memoize.apply(x, y, z);
        }
        return shape;
    }

    static boolean isSameMultiblock(Level level, BlockState state1, BlockState state2, BlockPos center, BlockPos posNew){
        return state1.getBlock().equals(state2.getBlock()) && level.getBlockEntity(posNew) instanceof IMultiBlockEntity entity && entity.getCenter().equals(center);
    }

    private Block self(){
        if (this instanceof Block block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block");
        }
    }

    static void verifyValidBlockEntity(Level level, BlockPos pos){
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity != null){
            if (!(blockEntity instanceof IMultiBlockEntity)){
                throw new IllegalStateException(blockEntity.getClass().getSimpleName() + " does not implement IMultiBlockEntity!");
            }
        }

    }

}
