package net.nikdo53.tinymultiblocklib.block.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class DelegatingMultiblockLogic extends MultiblockLogic{
    public static final DelegatingMultiblockLogic INSTANCE = new DelegatingMultiblockLogic();

    public static BlockLive getCenterBlock(BlockGetter level, BlockPos pos) {
        BlockPos center = IMultiBlock.getCenter(level, pos);
        return new BlockLive(level, center);
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int updateFlags, int updateLimit) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        centerBlock.state.updateIndirectNeighbourShapes(level, centerBlock.pos, updateFlags, updateLimit);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        centerBlock.state.onRemove(level, centerBlock.pos, newState, movedByPiston);
    }

    @Override
    public void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        centerBlock.state.onExplosionHit(level, centerBlock.pos, explosion, dropConsumer);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.useWithoutItem(level, player, hitResult.withPosition(centerBlock.pos));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.useItemOn(itemStack, level, player, hand, hitResult.withPosition(centerBlock.pos));
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.triggerEvent(level, centerBlock.pos, b0, b1);
    }

    //TODO: multiblock properties
    //TODO: cache and move voxelshapes
    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getBlockSupportShape(level, centerBlock.pos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getInteractionShape(level, centerBlock.pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getShape(level, centerBlock.pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getCollisionShape(level, centerBlock.pos, context);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getOcclusionShape(level, centerBlock.pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getVisualShape(level, centerBlock.pos, context);
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getMenuProvider(level, centerBlock.pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.canSurvive(level, centerBlock.pos);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getShadeBrightness(level, centerBlock.pos);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getAnalogOutputSignal(level, centerBlock.pos);
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        centerBlock.state.spawnAfterBreak(level, centerBlock.pos, tool, dropExperience);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        centerBlock.state.attack(level, centerBlock.pos, player);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getSignal(level, centerBlock.pos, direction);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        centerBlock.state.entityInside(level, centerBlock.pos, entity);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockLive centerBlock = getCenterBlock(level, pos);
        return centerBlock.state.getDirectSignal(level, centerBlock.pos, direction);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile) {
        BlockLive centerBlock = getCenterBlock(level, blockHit.getBlockPos());
        centerBlock.state.onProjectileHit(level, state, blockHit.withPosition(centerBlock.pos), projectile);
    }

    //TODO: this
    /*@Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return super.getCloneItemStack(level, pos, state, includeData);
    }*/
}
