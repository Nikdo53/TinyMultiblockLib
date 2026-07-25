package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.logic.DelegatingMultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.MultiblockShape;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class LogicMultiBlock extends BaseMultiBlock implements IMovableMultiblock {
    public LogicMultiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void makeMultiblockShape(MultiblockShape.Builder builder, Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        builder.add(new Vec3i(0, 1, 0), DelegatingMultiblockLogic.INSTANCE, s -> s);
        builder.add(0, 2, 0, DelegatingMultiblockLogic.INSTANCE);
    }

    @Override
    public MultiblockLogic getCenterLogic() {
        return new MultiblockLogic(){
            @Override
            public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
                return InteractionResult.SUCCESS;
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    //Logic delegations:

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
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getLogicForPos(level, pos, state).getBlockSupportShape(state, level, pos);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getLogicForPos(level, pos, state).getInteractionShape(state, level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getLogicForPos(level, pos, state).getShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getLogicForPos(level, pos, state).getCollisionShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return getLogicForPos(level, pos, state).getEntityInsideCollisionShape(state, level, pos, entity);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getLogicForPos(level, pos, state).getVisualShape(state, level, pos, context);
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
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getLogicForPos(level, pos, state).getShadeBrightness(state, level, pos);
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

