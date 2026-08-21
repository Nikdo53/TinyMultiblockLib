package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.nikdo53.tinymultiblocklib.CommonRegistration;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeContext;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public abstract class LogicMultiblock extends BaseMultiblock implements IMovableMultiblock {
    public LogicMultiblock(Properties properties) {
        super(properties);
    }

    public MultiblockLogic getLogicForPos(BlockGetter level, BlockPos pos, BlockState state, @Nullable MultiblockShape shape){
        if (shape == null) {
            shape = getFullBlockShape(level, pos, state);
        }
        MultiblockShape.Entry entry = shape.getEntryForGlobalPos(pos);
        if (entry == null) {
            return MultiblockLogic.EMPTY;
        }
        return entry.logic();
    }

    public abstract MultiblockLogic getCenterLogic(ShapeContext context);


    @Override
    public MultiblockLogic getDefaultCenterLogic(ShapeContext context) {
        return getCenterLogic(context);
    }

    public MultiblockLogic getLogicForPos(BlockGetter level, BlockPos pos, BlockState state){
        return getLogicForPos(level, pos, state, null);
    }

    //TMBL delegations (these do not use super since that's already a part of the logic)
    @Override
    public boolean canReplaceBlock(LevelReader level, BlockPos blockPos, BlockState state, MultiblockShape shape) {
        return getLogicForPos(level, blockPos, state, shape).canReplaceBlock(level, blockPos, state, shape);
    }

    @Override
    public boolean entityUnobstructed(CollisionGetter level, BlockPos pos, BlockState state, @Nullable Entity player, MultiblockShape shape) {
        return getLogicForPos(level, pos, state, shape).entityUnobstructed(level, pos, state, player, shape);
    }

    @Override
    public boolean extraSurviveRequirements(LevelReader level, BlockPos pos, BlockState state, BlockPos centerOffset, MultiblockShape shape) {
        return getLogicForPos(level, pos, state, shape).extraSurviveRequirements(level, pos, state, centerOffset, shape);
    }


    //vanilla logic delegations:
    @Override
    public void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int updateFlags, int updateLimit) {
        getLogicForPos(level, pos, state).updateIndirectNeighbourShapes(state, level, pos, updateFlags, updateLimit);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState logicState = getLogicForPos(level, pos, state).updateShape(state, direction, neighborState, level, pos, neighborPos);
        return super.updateShape(logicState, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        getLogicForPos(level, pos, state).onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        getLogicForPos(level, pos, state).neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return getLogicForPos(level, pos, state).use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1) {
        return getLogicForPos(level, pos, state).triggerEvent(state, level, pos, b0, b1);
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return getLogicForPos(level, pos, state).getMenuProvider(state, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos) && getLogicForPos(level, pos, state).canSurvive(state, level, pos);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return getLogicForPos(level, pos, state).getAnalogOutputSignal(state, level, pos);
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        getLogicForPos(level, pos, state).spawnAfterBreak(state, level, pos, tool, dropExperience);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        getLogicForPos(level, pos, state).attack(state, level, pos, player);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getLogicForPos(level, pos, state).getSignal(state, level, pos, direction);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        getLogicForPos(level, pos, state).entityInside(state, level, pos, entity);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getLogicForPos(level, pos, state).getDirectSignal(state, level, pos, direction);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile) {
        getLogicForPos(level, blockHit.getBlockPos(), state).onProjectileHit(level, state, blockHit, projectile);
    }
}

