package net.nikdo53.tinymultiblocklib.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.components.IBlockPosOffsetEnum;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static net.nikdo53.tinymultiblocklib.Constants.*;
import static net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock.CENTER;

public interface IMultiBlock extends IMBStateSharer, EntityBlock {

    /** Returns a BlockPos Stream of every block in this multiblock.
     * <p>
     * Should only be used for overriding
     * @param center The center pos of the multiblock, aka the 1st block placed
     * @param blockEntity null when being placed
     * @param direction present only when {@link #getDirectionProperty()} is overridden with a valid property
     * @see #getFullBlockShape(BlockGetter, BlockPos, BlockState)
     * */
    List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction);

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
     * */
    default @Nullable EnumProperty<Direction> getDirectionProperty(){
        return null; // null if block doesn't have directions
    }

    default @Nullable Direction getDirection(BlockState state){
        if (getDirectionProperty() != null){
            return state.getValue(getDirectionProperty());
        }
        return null;
    }

    default List<BlockPos> getFullBlockShapeNoCache(Level level, @Nullable BlockEntity blockEntity, BlockPos center, BlockState state){
        if (blockEntity == null){
            blockEntity = level.getBlockEntity(center);
        }

        List<BlockPos> list = makeFullBlockShape(level, center, state, blockEntity, getDirection(state));

        // Warn everyone of Mo-jank
        Set<BlockPos> set = new HashSet<>(list);
        if (set.size() < list.size()) {
            LOGGER.error("Multiblock {} at {} has overlapping blocks in it's shape,"
                    + " this is likely caused by the BlockPos being mutable."
                    + " Either map them to BlockPos::immutable or use IMultiBlock.posStreamToList()",
                    state.toString(), center);
        }


        return list;
    }


    default List<BlockPos> getFullBlockShape(BlockGetter level, BlockPos pos, BlockState state){
        BlockPos center = getCenter(level, pos);
        BlockEntity blockEntity = level.getBlockEntity(center);
        Level betterLevel = level instanceof Level ? (Level) level : null;

        if (!(blockEntity instanceof IMultiBlockEntity mbEntity)){
            return getFullBlockShapeNoCache(betterLevel, blockEntity ,center, state);
        }

        if (mbEntity.getFullBlockShapeCache().isEmpty()){
            List<BlockPos> blockPosList = getFullBlockShapeNoCache(betterLevel, blockEntity, center, state);
            blockPosList.forEach(BlockPos::immutable);

            mbEntity.setFullBlockShapeCache(blockPosList);
            return blockPosList;
        }

        return mbEntity.getFullBlockShapeCache();
    }

    static List<BlockPos> getFullShape(BlockGetter level, BlockPos pos){
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IMultiBlock multiBlock){
            return multiBlock.getFullBlockShape(level, pos, state);
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
     * */
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
    default List<BlockLike> prepareForPlace(List<BlockPos> shape, Level level, BlockPos centerPos, BlockState stateOriginal){
        List<BlockLike> list = new ArrayList<>();

        shape.forEach(posNew -> {
            posNew = posNew.immutable();

            BlockState stateNew = stateOriginal.setValue(AbstractMultiBlock.CENTER, centerPos.equals(posNew));
            stateNew = getStateForEachBlock(stateNew, posNew, posNew.subtract(centerPos), level, getDirection(stateOriginal));

            list.add(new BlockLike(posNew, stateNew));
        });

        return list;
    }


    default BlockState getStateForPlacementHelper(BlockPlaceContext context) {
        return getStateForPlacementHelper(context, context.getHorizontalDirection());
    }

    /**
     * Helper for {@link Block#getStateForPlacement(BlockPlaceContext)}
     * @param direction The direction the block will have when placed, ignored when {@link #getDirectionProperty()} is null
     * */
    default BlockState getStateForPlacementHelper(BlockPlaceContext context, Direction direction) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = self().defaultBlockState().setValue(CENTER, true);

        if (getDirectionProperty() != null){
            state = state.setValue(getDirectionProperty(), direction);
        }

        return canPlace(level, pos, state, context.getPlayer(), true) ? state : null;
    }

    default boolean canPlace(LevelReader level, BlockPos center, BlockState state, @Nullable Entity player, boolean ignoreEntities) {
        return getFullBlockShape(level, center, state).stream().allMatch(blockPos ->
                canReplaceBlock(level, blockPos, level.getBlockState(blockPos))
                        && extraSurviveRequirements(level, blockPos, state, blockPos.subtract(center))
                        && (entityUnobstructed(level, blockPos, state, player) || ignoreEntities)
                        && blockPos.getY() < level.getMaxY() && blockPos.getY() > level.getMinY());
    }

    /**
     * Returns true if multiblock can replace this original block, runs for the whole multiblock shape
     * */
    default boolean canReplaceBlock(LevelReader level, BlockPos blockPos, BlockState state) {
        return state.canBeReplaced();
    }

    default boolean entityUnobstructed(CollisionGetter level, BlockPos pos, BlockState state, @Nullable Entity player) {
        CollisionContext context = player == null ? CollisionContext.empty() : CollisionContext.of(player);

        return getFullBlockShape(level, pos, state).stream().allMatch(blockPos -> level.isUnobstructed(state, blockPos, context));
    }

    default void destroy(BlockPos center, LevelAccessor level, BlockState state, boolean dropBlock){
        if (level.isClientSide()) return;
        List<BlockPos> blocks = getFullBlockShape(level, center, state);

        if (level.getBlockState(center).is(state.getBlock())) {
            level.destroyBlock(center, dropBlock);
        }

        getIsolatedBlocks(center, level, state).forEach(pos ->{
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(state.getBlock())) {
                level.destroyBlock(pos, dropBlock);
            }
        });
    }

    default List<BlockPos> getIsolatedBlocks(BlockPos center, LevelAccessor level, BlockState state) {
        Set<BlockPos> posSet = new HashSet<>(getFullBlockShape(level, center, state));

        List<BlockPos> isolated = new ArrayList<>();

        for (BlockPos pos : posSet) {
            boolean hasNeighbor =
                    posSet.contains(pos.above()) ||
                            posSet.contains(pos.below()) ||
                            posSet.contains(pos.north()) ||
                            posSet.contains(pos.south()) ||
                            posSet.contains(pos.east())  ||
                            posSet.contains(pos.west());

            if (!hasNeighbor) {
                isolated.add(pos);
            }
        }

        return isolated;
    }


    default boolean allBlocksPresent(LevelReader level, BlockPos pos, BlockState state){
        if (level.isClientSide()) return true;
        BlockPos center = getCenter(level, pos);

        boolean ret = getFullBlockShape(level, center, state).stream().allMatch(blockPos -> level.getBlockState(blockPos).is(self()));

        boolean isMultiblock = isMultiblock(level, pos);
        if (ret && level.getBlockEntity(pos) instanceof IMultiBlockEntity entity && !entity.isPlaced() && isMultiblock) {
            getFullBlockShape(level, center, state).forEach(blockPos -> IMultiBlockEntity.setPlaced(level, blockPos, true));
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
            boolean extraSurvive = getFullBlockShape(level, pos, state).stream().allMatch(blockPos -> extraSurviveRequirements(level, blockPos, state, entity.getOffset()));
            return (allBlocksPresent(level, pos, state) || !entity.isPlaced()) && extraSurvive;
        } else {
            //placement logic
            return canPlace(level, pos, state, null, false);
        }
    }

    /**
     * Extra requirements for the block to survive or be placed, runs for every single block in the multiblock
     * */
    default boolean extraSurviveRequirements(LevelReader level, BlockPos pos, BlockState state, BlockPos centerOffset){
        return true;
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
            var x = (-entity.getOffset().getX()) + xOffset;
            var y = (-entity.getOffset().getY()) + yOffset;
            var z = (-entity.getOffset().getZ()) + zOffset;

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
            return shape.move(x,y,z);
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
