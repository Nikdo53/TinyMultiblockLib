package net.nikdo53.tinymultiblocklib.block.logic;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class MultiblockLogic extends BlockBehaviour implements MultiblockBehaviour {
    public static final MultiblockLogic EMPTY = new MultiblockLogic();

    public MultiblockLogic() {
        super(MultiblockLogic.Properties.of());
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return null;
    }

    @Override
    public Item asItem() {
        return this.asBlock().asItem();
    }

    @Override
    protected Block asBlock() {
        Constants.LOGGER.warn("as block was called on a logic block, this is not supported, returning air instead");
        return Blocks.AIR;
    }


    //Public land:
    @Override
    public void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int updateFlags, int updateLimit) {
        super.updateIndirectNeighbourShapes(state, level, pos, updateFlags, updateLimit);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    public void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1) {
        return super.triggerEvent(state, level, pos, b0, b1);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return super.canBeReplaced(state, context);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getOcclusionShape(state, level, pos);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getBlockSupportShape(state, level, pos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getInteractionShape(state, level, pos);
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return super.getMenuProvider(state, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getShadeBrightness(state, level, pos);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return super.getAnalogOutputSignal(state, level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return super.isCollisionShapeFullBlock(state, level, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getVisualShape(state, level, pos, context);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return super.getSignal(state, level, pos, direction);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return super.getDirectSignal(state, level, pos, direction);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile) {
        super.onProjectileHit(level, state, blockHit, projectile);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getLightBlock(state, level, pos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return super.propagatesSkylightDown(state, level, pos);
    }


    public static class Properties extends BlockBehaviour.Properties {
        public Properties() {
            super();
        }

        public static Properties of() {
            return new Properties();
        }
    }
}
