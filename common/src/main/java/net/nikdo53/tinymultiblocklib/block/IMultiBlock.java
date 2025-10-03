package net.nikdo53.tinymultiblocklib.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.IBlockPosOffsetEnum;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock.CENTER;

public interface IMultiBlock extends IMBStateSyncer {

    /** Returns a BlockPos Stream of every block in this multiblock.
     * <p>
     * Should only be used for overriding
     * @see #fullBlockShape(BlockPos, BlockState)
     * */
    Stream<BlockPos> fullBlockShape(@Nullable Direction direction, BlockPos center, BlockState state);


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

    default Block getBlock(){
        if (this instanceof Block block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block");
        }
    }

    default Stream<BlockPos> fullBlockShape(BlockPos center, BlockState state){
        if (getDirectionProperty() == null || state == null)
            return fullBlockShape(null, center, state);

        return fullBlockShape(state.getValue(getDirectionProperty()), center, state);
    }

    static Stream<BlockPos> getFullShape(Level level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof IMultiBlockEntity multiBlockEntity
                && level.getBlockState(pos).getBlock() instanceof IMultiBlock multiBlock){

            return multiBlock.fullBlockShape(multiBlockEntity.getCenter(), level.getBlockState(multiBlockEntity.getCenter()));
        }
        else return Stream.of(pos);
    }


    /**
     * Changes the BlockState for each Block based on its offset from center
     * <p>
     * Code Example:
     * <p>
     * ((state, pos) -> state.setValue(yourStateProperty, IBlockPosOffsetEnum.fromOffset(params)))
     * @see IBlockPosOffsetEnum#fromOffset(Class, BlockPos, Direction, Enum)
     * */
    default @Nullable BiFunction<BlockState, BlockPos, BlockState> getStateFromOffset() {
        return null;
    };

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
        fullBlockShape(centerPos, stateOriginal).forEach(posNew -> {
            int flags = level.isClientSide ? 0 : 3;

            BlockState stateNew = stateOriginal.setValue(CENTER, centerPos.equals(posNew));
            if (getStateFromOffset() != null) stateNew = getStateFromOffset().apply(stateNew, posNew.subtract(centerPos));

            level.setBlock(posNew, stateNew, flags);
            if(level.getBlockEntity(posNew) instanceof IMultiBlockEntity entity) {
                entity.setCenter(centerPos);
                entity.getBlockEntity().setChanged();
            }
        });
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
        BlockState state = getBlock().defaultBlockState().setValue(CENTER, true);

        if (getDirectionProperty() != null){
            state = state.setValue(getDirectionProperty(), direction);
        }

        return canPlace(level, pos, state) ? state : null;
    }

    default boolean canPlace(LevelReader level, BlockPos center, BlockState state) {
        return fullBlockShape(center, state).allMatch(blockPos -> level.getBlockState(blockPos).canBeReplaced() && extraSurviveRequirements(level, blockPos, state));
    }

    default void destroy(BlockPos center, Level level, BlockState state){
        if (level.isClientSide()) return;
        fullBlockShape(center, state).forEach(pos ->{
            BlockState blockState = level.getBlockState(pos);
            Block block = state.getBlock();
            if (blockState.is(block)) {
                level.destroyBlock(pos, true);
            }
        });
    }

    default boolean allBlocksPresent(LevelReader level, BlockPos pos, BlockState state){
        if (level.isClientSide()) return true;
        BlockPos center = getCenter(level, pos);

        boolean ret = fullBlockShape(center, state).allMatch(blockPos -> level.getBlockState(blockPos).is(getBlock()));

        if (ret && level.getBlockEntity(pos) instanceof IMultiBlockEntity entity && !entity.isPlaced()) {
            fullBlockShape(center, state).forEach(blockPos -> IMultiBlockEntity.setPlaced(level, blockPos, true));
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
                destroy(entity.getCenter(), (Level) level, state);
                return Blocks.AIR.defaultBlockState();
            }
        }else {
            level.destroyBlock(pos, true);
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
            boolean extraSurvive = fullBlockShape(entity.getCenter(), state).allMatch(blockPos -> extraSurviveRequirements(level, blockPos, state));
            return (allBlocksPresent(level, pos, state) || !entity.isPlaced()) && extraSurvive;
        } else {
            //placement logic
            return canPlace(level, pos, state);
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
        if (player.isCreative() && level.getBlockEntity(pos) instanceof IMultiBlockEntity entity) {
            level.destroyBlock(entity.getCenter(), false);
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

            fullBlockShape(pos, state).forEach(posNew -> {
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

        return fullBlockShape(pos, state).anyMatch(blockPos -> {
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
     * */
    default void growHelper(Level level, BlockPos blockPos, IntegerProperty ageProperty){
        Block block = getBlock();
        if(level.getBlockEntity(blockPos) instanceof IMultiBlockEntity entity) {
            fullBlockShape(entity.getCenter(), level.getBlockState(blockPos)).forEach(pos -> {
                if(level.getBlockState(pos).is(block)) {

                    BlockState blockState = level.getBlockState(pos);
                    int age = blockState.getValue(ageProperty);
                    if (blockState.getValue(ageProperty) >= getMaxAge(ageProperty)) return;

                    level.setBlock(pos, blockState.setValue(ageProperty,age + 1), 2);

                }else {
                    level.destroyBlock(pos, false);
                }
            });
        } else level.destroyBlock(blockPos, true);
    }

    default int getMaxAge(IntegerProperty ageProperty) {
        return ageProperty.getPossibleValues().stream().toList().get(ageProperty.getPossibleValues().size() - 1);
    }
}
