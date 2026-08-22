package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.block.IPreviewableMultiblock;
import net.nikdo53.tinymultiblocklib.compat.carryon.CarryOnPreviewHelper;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.config.TMBLClientConfig;
import net.nikdo53.tinymultiblocklib.data.TMBLTags;
import net.nikdo53.tinymultiblocklib.mixin.BlockItemAccessor;
import net.nikdo53.tinymultiblocklib.mixin.ItemAccessor;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class MultiblockPreviewRenderer {
    public static final TranslucentSubmitNodeStorage NODE_STORAGE = RenderUtils.createTranslucentNodeStorage();

    public static final Set<Block> PREVIEW_CRASHLIST = new HashSet<>();
    public static final Set<Block> SET_PLACED_BY_BLACKLIST = new HashSet<>();

    public static void tryRenderMultiblockPreviews(float partialTick, CameraRenderState camera, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (TMBLClientConfig.DISABLE_MULTIBLOCK_PREVIEWS.get()) return;
        FakeClientLevel.getOrThrow().clear();

        //funny passthrough cuz im not restructuring this whole thing
        AtomicReference<@Nullable Block> blockReference = new AtomicReference<>();

        try {
            renderMultiblockPreviews(partialTick, camera, poseStack, submitNodeCollector, blockReference);
        } catch (Exception e) {
            Block block = blockReference.get();
            if (block != null) {
                PREVIEW_CRASHLIST.add(block);
                Constants.LOGGER.error("Error rendering multiblock preview: " + e.getMessage() + " adding " + block + " to blacklist to prevent further errors", e);
            } else {
                Constants.LOGGER.error("Error rendering multiblock preview: " + e.getMessage() + " this should never happen unless its carryons fault ig", e);
            }

        }
    }

    public static void renderMultiblockPreviews(float partialTick, CameraRenderState camera, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, AtomicReference<@Nullable Block> blockReference) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;

        if (player == null || level == null) return;

        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();

        double camX = camera.pos.x;
        double camY = camera.pos.y;
        double camZ = camera.pos.z;

        if (Services.PLATFORM.isModLoaded("carryon")) {
           if (CarryOnPreviewHelper.isValidMultiblock(player)) item = CarryOnPreviewHelper.getMultiblockItem(player);
        }

        if (item instanceof BlockItem blockItem) {

            if (!(minecraft.hitResult instanceof BlockHitResult blockHitResult)) return;

            boolean placeOnWater = false;

            if (blockItem instanceof PlaceOnWaterBlockItem) {
                blockHitResult = ItemAccessor.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
                placeOnWater = level.isWaterAt(blockHitResult.getBlockPos());
            }

            Direction hitDirection = blockHitResult.getDirection();
            Block block = blockItem.getBlock();

            blockReference.set(block);
            BlockPos hitPos = blockHitResult.getBlockPos();
            BlockPos pos = hitPos.relative(hitDirection);

            // ⬇️⬇️ the line that disables previewing of everything
            if (TMBLClientConfig.PREVIEWS_FOR_EVERYTHING.isFalse()) {
                if (!canShowPreview(stack, block) || PREVIEW_CRASHLIST.contains(block))
                    return;
            }

            BlockState state = ((BlockItemAccessor) blockItem).tinyMultiblockLib$getPlacementState(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, blockHitResult));
            boolean hasNullState = false;

            if (state == null){
                hasNullState = true;
                state = block.defaultBlockState();

                if (block instanceof IPreviewableMultiblock multiblock){
                    state = multiblock.getDefaultStateForPreviews(player.getDirection());
                }

                if (block instanceof IMultiBlock multiBlock && multiBlock.makeDirectional() != null){
                    IMultiBlock.DirectionContext directionContext = multiBlock.makeDirectional();
                    assert directionContext != null;
                    Direction dir = directionContext.directionExtractor().apply(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, blockHitResult));

                    if (dir != null) {
                        state = state.setValue(directionContext.property(), dir);
                    }
                }
            }

            BlockEntity blockEntity = block instanceof EntityBlock entityBlock ? entityBlock.newBlockEntity(pos, state) : null;
            if (blockEntity != null) {
                blockEntity.setLevel(level);
            }

            PreviewMode previewMode = getPreviewMode(level, pos, state, player, blockEntity, hasNullState);

            boolean shouldShowPreview = level.getBlockState(pos).canBeReplaced()
                    && (!level.getBlockState(hitPos).isAir() || placeOnWater);

            if (level.getBlockState(hitPos).canBeReplaced() && !placeOnWater)
                pos = pos.relative(hitDirection.getOpposite());

            poseStack.pushPose();

            poseStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

            FakeClientLevel fakeLevel = FakeClientLevel.getOrThrow();
            Set<BlockLive> blockLiveSet = gatherBlockLives(fakeLevel, level, blockEntity, pos, state, minecraft.player, stack);

            BlockLive centerLive = BlockLive.Live.optionalBE(pos, state, blockEntity);
            IOnBlockPreviewEvent event = IOnBlockPreviewEvent.firePreEvent(previewMode, !shouldShowPreview, centerLive, blockLiveSet);

            if (!event.isCancelledInternal()) {
                blockLiveSet = event.getBlocksForPreview();
                fakeLevel.blockLiveSet = blockLiveSet;
                previewMode = event.getPreviewMode();

                for (BlockLive blockLive : blockLiveSet) {
                    renderJsonModels(blockLive, pos, poseStack, fakeLevel);
                }

                for (BlockLive blockLive : blockLiveSet) {
                    renderBlockEntity(blockLive, pos, poseStack, partialTick, minecraft, fakeLevel, camera);
                }

                RenderUtils.renderFromStorage(submitNodeCollector, NODE_STORAGE, previewMode, poseStack);

                IOnBlockPreviewEvent.firePostEvent(previewMode, centerLive, blockLiveSet, poseStack, partialTick, NODE_STORAGE);

            }

            poseStack.popPose();

        }
    }

    public static boolean canShowPreview(ItemStack stack, Block block) {
        String registeredName = block.builtInRegistryHolder().getRegisteredName();
        return (stack.is(TMBLTags.ItemTags.SHOW_PREVIEW)
                || block instanceof IPreviewableMultiblock
                || TMBLClientConfig.PREVIEW_WHITELIST.get().contains(registeredName)
        ) && !TMBLClientConfig.PREVIEW_BLACKLIST.get().contains(registeredName) ;
    }

    private static void renderBlockEntity(BlockLive blockLive, BlockPos originalPos, PoseStack poseStack, float partialTick
            , Minecraft minecraft, FakeClientLevel fakeClientLevel, CameraRenderState camera) {

        BlockState state = blockLive.state;
        BlockPos pos = blockLive.pos;

        if (state.getBlock() instanceof EntityBlock entityBlock) {

            BlockEntity entity = entityBlock.newBlockEntity(pos, state);
            if (entity == null) return;
            entity.setLevel(fakeClientLevel);

            if (entity instanceof IMultiBlockEntity multiBlockEntity) {
                multiBlockEntity.setCenter(originalPos);
            }

            BlockEntityRenderer<BlockEntity, BlockEntityRenderState> entityRender = minecraft.getBlockEntityRenderDispatcher().getRenderer(entity);

            if (entityRender != null) {
                BlockEntityRenderState renderState = entityRender.createRenderState();

                poseStack.pushPose();
                poseStack.translate(0.0001, 0.0001, 0.0001);

                BlockPos offset = blockLive.pos.subtract(originalPos).immutable();
                poseStack.translate(offset.getX(), offset.getY(), offset.getZ());

                entityRender.extractRenderState(entity, renderState, partialTick, camera.pos, null);
                entityRender.submit(renderState, poseStack, NODE_STORAGE, camera);

                poseStack.popPose();

            }
        }
    }

    private static PreviewMode getPreviewMode(Level level, BlockPos pos, BlockState state, LocalPlayer player, @Nullable BlockEntity blockEntity, boolean hasNullState) {
        if (hasNullState) return PreviewMode.INVALID;

        IMultiBlock multiBlock = null;
        MultiblockShape shape = null;
        if (state.getBlock() instanceof IMultiBlock mb) {
            multiBlock = mb;
            shape = mb.getFullBlockShapeNoCache(level, blockEntity, pos, state);
        }


        boolean multiBlockCanPlace = canPlace(level, pos, state, player, shape, multiBlock);
        boolean entityUnobstructed = isEntityUnobstructed(level, pos, state, player, shape, multiBlock);

        return multiBlockCanPlace ? (entityUnobstructed ? PreviewMode.PREVIEW : PreviewMode.ENTITY_BLOCKED) : PreviewMode.INVALID;
    }

    private static boolean isEntityUnobstructed(Level level, BlockPos pos, BlockState state, LocalPlayer player, @Nullable MultiblockShape shape, @Nullable IMultiBlock multiBlock) {
        if (shape != null && multiBlock != null)
            return multiBlock.entityUnobstructed(level, pos, state, player, shape);

        return level.isUnobstructed(state, pos, CollisionContext.of(player));
    }

    private static boolean canPlace(Level level, BlockPos pos, BlockState state, LocalPlayer player, @Nullable MultiblockShape shape, @Nullable IMultiBlock multiBlock) {
        if (shape != null && multiBlock != null)
            return multiBlock.canPlaceBlock(pos, level, pos, state,player, true, shape);

        return state.canSurvive(level, pos);
    }

    public static void renderJsonModels(BlockLive blockLive, BlockPos originalPos, PoseStack poseStack, FakeClientLevel fakeLevel) {

        if (!blockLive.state.getRenderShape().equals(RenderShape.MODEL)) return;

        poseStack.pushPose();
        poseStack.translate(0.0001, 0.0001, 0.0001);

        BlockPos offset = blockLive.pos.subtract(originalPos).immutable();
        poseStack.translate(offset.getX(), offset.getY(), offset.getZ());

        NODE_STORAGE.submitMovingBlock(poseStack,
                RenderUtils.createMovingBlockRenderState(fakeLevel, blockLive.pos, blockLive.state, true, Sheets.translucentBlockItemSheet(), null, null), 0);

        poseStack.popPose();
    }

    public static Set<BlockLive> gatherBlockLives(FakeClientLevel fakeLevel, Level level, @Nullable BlockEntity blockEntity, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Set<BlockLive> blockLiveSet = new HashSet<>();
        Block block = state.getBlock();

        if (block instanceof IMultiBlock multiBlock) {
            blockLiveSet.addAll(multiBlock.prepareForPlace(multiBlock.getFullBlockShapeNoCache(level, blockEntity, pos, state), level, pos, state));
        } else {
            blockLiveSet.add(BlockLive.Live.optionalBE(pos, state, blockEntity));
        }

        if (!SET_PLACED_BY_BLACKLIST.contains(block)) {
            try {
                block.setPlacedBy(fakeLevel, pos, state, placer, stack);
            } catch (Exception ignored) {
                Constants.LOGGER.warn("setPlacedBy failed for block " + block + "adding to ignore list.");
                SET_PLACED_BY_BLACKLIST.add(block);
            }
        }

        blockLiveSet.addAll(fakeLevel.blockLiveSet);

        return blockLiveSet;
    }
}
