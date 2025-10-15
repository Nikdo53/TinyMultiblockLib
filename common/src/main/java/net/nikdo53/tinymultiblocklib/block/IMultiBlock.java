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
import net.nikdo53.tinymultiblocklib.components.IBlockPosOffsetEnum;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static net.nikdo53.tinymultiblocklib.Constants.*;
import static net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock.CENTER;

public interface IMultiBlock extends IMBStateSyncer {

    /** Returns a BlockPos Stream of every block in this multiblock.
     * <p>
     * Should only be used for overriding
     * @see #getFullBlockShapeNoCache(BlockPos, BlockState)
     * */
    List<BlockPos> makeFullBlockShape(@Nullable Direction direction, BlockPos center, BlockState state);

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
    default @Nullable DirectionProperty getDirectionProperty(){
        return null; // null if block doesn't have directions
    }

    default @Nullable Direction getDirection(BlockState state){
        if (getDirectionProperty() != null){
            return state.getValue(getDirectionProperty());
        }
        return null;
    }

    default List<BlockPos> getFullBlockShapeNoCache(BlockPos center, BlockState state){
        List<BlockPos> list;

        if (getDirectionProperty() == null) {
            list = makeFullBlockShape(null, center, state);
        } else {
            list = makeFullBlockShape(state.getValue(getDirectionProperty()), center, state);
        }

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


    default List<BlockPos> getFullBlockShape(BlockPos pos, BlockState state, BlockGetter level){
        BlockPos center = getCenter(level, pos);

        if (!(level.getBlockEntity(center) instanceof IMultiBlockEntity blockEntity)){
            return getFullBlockShapeNoCache(center, state);
        }

        if (blockEntity.getFullBlockShapeCache().isEmpty()){
            List<BlockPos> blockPosList = getFullBlockShapeNoCache(center, state);
            blockPosList.forEach(BlockPos::immutable);

            blockEntity.setFullBlockShapeCache(blockPosList);
            return blockPosList;
        }

        return blockEntity.getFullBlockShapeCache();
    }

    static List<BlockPos> getFullShape(Level level, BlockPos pos){
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IMultiBlock multiBlock){
            return multiBlock.getFullBlockShape(pos, state, level);
        }

        return List.of(pos);
    }

    static void invalidateCaches(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(getCenter(level, pos)) instanceof IMultiBlockEntity blockEntity){
            blockEntity.invalidateCaches();
        }
    }

    /**
     * Changes the BlockState for each Block based on its offset from center
     * @see IBlockPosOffsetEnum#fromOffset(Class, BlockPos, Direction, Enum)
     * */
    default BlockState getStateFromOffset(BlockState state, BlockPos offset){
        return state;
    }

    default void onPlaceHelper(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        boolean isPlaced = IMultiBlockEntity.isPlaced(level, pos);

        if (isPlaced) syncBlockStates(level, pos, state);

        if (isCenter(state)) {
            if (!isPlaced) place(level, pos, state);
        }
    }

    /**
     * Places the multiblock, sets its BlockStates and BlockEntity center
     * */
    default void place(Level level, BlockPos centerPos, BlockState stateOriginal){
        prepareForPlace(level, centerPos, stateOriginal).forEach(pair -> {
            int flags = 66;

            BlockState stateNew = pair.getSecond();
            BlockPos posNew = pair.getFirst();

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
    default List<Pair<BlockPos, BlockState>> prepareForPlace(Level level, BlockPos centerPos, BlockState stateOriginal){
        List<Pair<BlockPos, BlockState>> list = new ArrayList<>();

        getFullBlockShape(centerPos, stateOriginal, level).forEach(posNew -> {
            posNew = posNew.immutable();

            BlockState stateNew = stateOriginal.setValue(AbstractMultiBlock.CENTER, centerPos.equals(posNew));
            stateNew = getStateFromOffset(stateNew, posNew.subtract(centerPos));

            list.add(new Pair<>(posNew, stateNew));
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

        return canPlace(level, pos, state, context.getPlayer(), false) ? state : null;
    }

    default boolean canPlace(LevelReader level, BlockPos center, BlockState state, @Nullable Entity player, boolean ignoreEntities) {
        return getFullBlockShape(center, state, level).stream().allMatch(blockPos ->
                level.getBlockState(blockPos).canBeReplaced()
                        && extraSurviveRequirements(level, blockPos, state)
                        && (entityUnobstructed(level, blockPos, state, player) || ignoreEntities));
    }

    default boolean entityUnobstructed(CollisionGetter level, BlockPos pos, BlockState state, @Nullable Entity player) {
        CollisionContext context = player == null ? CollisionContext.empty() : CollisionContext.of(player);

        return getFullBlockShape(pos, state, level).stream().allMatch(blockPos -> level.isUnobstructed(state, blockPos, context));
    }

    default void destroy(BlockPos center, Level level, BlockState state, boolean dropBlock){
        if (level.isClientSide()) return;
        List<BlockPos> blocks = getFullBlockShape(center, state, level);

        level.destroyBlock(center, false);

        blocks.forEach(pos ->{
            BlockState blockState = level.getBlockState(pos);
            Block block = state.getBlock();
            if (blockState.is(block)) {
                level.destroyBlock(pos, dropBlock);
            }
        });
    }

    default boolean allBlocksPresent(LevelReader level, BlockPos pos, BlockState state){
        if (level.isClientSide()) return true;
        BlockPos center = getCenter(level, pos);

        boolean ret = getFullBlockShape(center, state, level).stream().allMatch(blockPos -> level.getBlockState(blockPos).is(self()));

        boolean isMultiblock = isMultiblock(level, pos);
        if (ret && level.getBlockEntity(pos) instanceof IMultiBlockEntity entity && !entity.isPlaced() && isMultiblock) {
            getFullBlockShape(center, state, level).forEach(blockPos -> IMultiBlockEntity.setPlaced(level, blockPos, true));
        }

        return ret;
    }

    /**
     * Helper for Block.updateShape()
     * <p>
     * Destroys the multiblock if canSurvive returns false
     * */
    default BlockState updateShapeHelper(BlockState state, LevelAccessor level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity){
            BlockPos centerPos = getCenter(level, pos);

            boolean canSurvive = state.canSurvive(level, centerPos);
            if (!canSurvive){
                destroy(entity.getCenter(), (Level) level, state, true);
                return Blocks.AIR.defaultBlockState();
            }
        }else {
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
            boolean extraSurvive = getFullBlockShape(pos, state, level).stream().allMatch(blockPos -> extraSurviveRequirements(level, blockPos, state));
            return (allBlocksPresent(level, pos, state) || !entity.isPlaced()) && extraSurvive;
        } else {
            //placement logic
            return canPlace(level, pos, state, null, false);
        }
    }

    /**
     * Extra requirements for the block to survive or be placed, runs for every single block in the multiblock
     * */
    default boolean extraSurviveRequirements(LevelReader level, BlockPos pos, BlockState state){
        return true;
    }

    /**
     * Should be added into {@link Block#playerDestroy(Level, Player, BlockPos, BlockState, BlockEntity, ItemStack)}
     * */
    default void preventCreativeDrops(Player player, Level level, BlockPos pos){
        if (player.isCreative() && level.getBlockEntity(pos) instanceof IMultiBlockEntity) {
            BlockPos center = getCenter(level, pos);

            destroy(center, level, level.getBlockState(pos), false);
        }
    }


    /**
     * Prevents desyncs and ghost blocks when multiblocks are used in structures
     * */
    default void fixInStructures(BlockState state, ServerLevelAccessor level, BlockPos pos){
        if (isCenter(state)) {
            level.scheduleTick(pos, state.getBlock(), 3);
        }
    }

    /**
     * Tries to fix the multiblock, called after {@link #fixInStructures(BlockState, ServerLevelAccessor, BlockPos)}
     * */
    default void fixTick(BlockState state, Level level, BlockPos pos){
        if (isCenter(state)){

            getFullBlockShape(pos, state, level).forEach(posNew -> {
                if (level.getBlockEntity(posNew) instanceof IMultiBlockEntity entity) {
                    entity.setCenter(pos);

                    entity.getBlockEntity().setChanged();
                    level.sendBlockUpdated(posNew, state, state, 2);
                }
            });
        }
    }

    /**
     * Checks if the multiblock needs fixing by  {@link #fixTick(BlockState, Level, BlockPos)}
     * */
    default boolean isBroken(LevelReader level, BlockPos pos, BlockState state){
        if (!isCenter(state)) return false;

        return getFullBlockShape(pos, state, level).stream().anyMatch(blockPos -> {
            if (level.getBlockEntity(blockPos) instanceof IMultiBlockEntity entity){
                return !(entity.getCenter().equals(pos) && !isCenter(level.getBlockState(blockPos)));
            }
            return true;
        });
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

    static boolean isCenter(LevelReader level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            return entity.getCenter().equals(pos);
        }
        return false;
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

    static int getXOffset(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            return pos.getX() - entity.getCenter().getX();
        }
        return 0;
    }

    static int getYOffset(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            return pos.getY() - entity.getCenter().getY();
        }
        return 0;
    }

    static int getZOffset(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            return pos.getZ() - entity.getCenter().getZ();
        }
        return 0;
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
            var x = entity.getCenter().getX() - pos.getX() + xOffset;
            var y = entity.getCenter().getY() - pos.getY() + yOffset;
            var z = entity.getCenter().getZ() - pos.getZ() + zOffset;

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

    /**
     * Increases age in each block at the same time
     * <p>
     * If used in the randomTick method, don't forget to check {@link #isCenter(BlockState)} first,
     * otherwise the block will grow significantly faster (each block tick separately)
     * @deprecated Use synced blockStates instead
     * */
    @Deprecated
    default void growHelper(Level level, BlockPos blockPos, IntegerProperty ageProperty){
        Block block = self();
            getFullBlockShape(blockPos, level.getBlockState(blockPos), level).forEach(pos -> {
                if(level.getBlockState(pos).is(block)) {

                    BlockState blockState = level.getBlockState(pos);
                    int age = blockState.getValue(ageProperty);
                    if (blockState.getValue(ageProperty) >= getMaxAge(ageProperty)) return;

                    level.setBlock(pos, blockState.setValue(ageProperty,age + 1), 2);

                }else {
                    level.destroyBlock(pos, false);
                }
            });
    }

    default int getMaxAge(IntegerProperty ageProperty) {
        return ageProperty.getPossibleValues().stream().toList().get(ageProperty.getPossibleValues().size() - 1);
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

}
