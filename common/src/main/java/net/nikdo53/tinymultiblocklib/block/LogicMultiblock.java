package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.nikdo53.tinymultiblocklib.CommonRegistration;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeContext;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

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
    protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, @Block.UpdateFlags int updateFlags, int updateLimit) {
        getLogicForPos(level, pos, state).updateIndirectNeighbourShapes(state, level, pos, updateFlags, updateLimit);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockState logicState = getLogicForPos(level, pos, state).updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        return super.updateShape(logicState, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        getLogicForPos(level, pos, state).neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        getLogicForPos(level, pos, state).onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        getLogicForPos(level, pos, state).affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> onHit) {
        getLogicForPos(level, pos, state).onExplosionHit(state, level, pos, explosion, onHit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return getLogicForPos(level, pos, state).useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return getLogicForPos(level, pos, state).useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1) {
        return getLogicForPos(level, pos, state).triggerEvent(state, level, pos, b0, b1);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return getLogicForPos(level, pos, state).getMenuProvider(state, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos) && getLogicForPos(level, pos, state).canSurvive(state, level, pos);
    }


    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return getLogicForPos(level, pos, state).getAnalogOutputSignal(state, level, pos, direction);
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        getLogicForPos(level, pos, state).spawnAfterBreak(state, level, pos, tool, dropExperience);
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        getLogicForPos(level, pos, state).attack(state, level, pos, player);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getLogicForPos(level, pos, state).getSignal(state, level, pos, direction);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        getLogicForPos(level, pos, state).entityInside(state, level, pos, entity, effectApplier, isPrecise);
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getLogicForPos(level, pos, state).getDirectSignal(state, level, pos, direction);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile) {
        getLogicForPos(level, blockHit.getBlockPos(), state).onProjectileHit(level, state, blockHit, projectile);
    }
}

