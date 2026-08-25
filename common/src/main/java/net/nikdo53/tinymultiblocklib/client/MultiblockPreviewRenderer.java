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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@NullMarked
public class MultiblockPreviewRenderer {
    public static final TranslucentSubmitNodeStorage NODE_STORAGE = RenderUtils.createTranslucentNodeStorage();

    public static final Set<Block> PREVIEW_CRASHLIST = new HashSet<>();
    public static final Set<Block> SET_PLACED_BY_BLACKLIST = new HashSet<>();

    final Minecraft minecraft;
    final LocalPlayer player;
    final ClientLevel level;
    FakeClientLevel fakeLevel = FakeClientLevel.getOrThrow();

    ItemStack stack;
    Item item;

    @Nullable Block currentBlock = null;
    boolean placeOnWater = false;
    @Nullable BlockHitResult blockHitResult = null;

    @Nullable MultiblockShape multiblockShape = null;
    PreviewMode previewMode = PreviewMode.PREVIEW;

    public MultiblockPreviewRenderer(Minecraft minecraft, LocalPlayer player, ClientLevel level) {
        this.minecraft = minecraft;
        this.player = player;
        this.level = level;

        stack = player.getMainHandItem();
        item = stack.getItem();

        if (Services.PLATFORM.isModLoaded("carryon")) {
            if (CarryOnPreviewHelper.isValidMultiblock(player))
                item = CarryOnPreviewHelper.getMultiblockItem(player);
        }
    }


    public static void tryRenderMultiblockPreviews(float partialTick, CameraRenderState camera, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (TMBLClientConfig.DISABLE_MULTIBLOCK_PREVIEWS.get()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        FakeClientLevel.getOrThrow().clear();


        MultiblockPreviewRenderer renderer = new MultiblockPreviewRenderer(mc, mc.player, mc.level);
        try {
            renderer.renderMultiblockPreviews(partialTick, camera, poseStack, submitNodeCollector);
        } catch (Exception e) {
            Block block = renderer.currentBlock;
            if (block != null) {
                PREVIEW_CRASHLIST.add(block);
                Constants.LOGGER.error("Error rendering multiblock preview: " + e.getMessage() + ". Adding " + block + " to blacklist to prevent further errors", e);
            } else {
                Constants.LOGGER.error("Error rendering multiblock preview: " + e.getMessage() + " this should never happen unless its carryons fault ig", e);
            }

        }
    }

    public void renderMultiblockPreviews(float partialTick, CameraRenderState camera, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (!(item instanceof BlockItem blockItem)) return;
        if (!(minecraft.hitResult instanceof BlockHitResult)) return;

        blockHitResult = (BlockHitResult) minecraft.hitResult;
        currentBlock = blockItem.getBlock();

        if (!canShowPreview(stack, currentBlock))
            return;

        checkPlaceOnWater();
        centerPos = getCenterPos(blockHitResult.getBlockPos());
        centerState = getCenterBlockState();
        centerBlockEntity = getCenterBlockEntity();
        multiblockShape = getMultiblockShape();

        fakeLevel.blockLiveSet = gatherBlockLives();
        IOnBlockPreviewEvent event = IOnBlockPreviewEvent.firePreEvent(checkPreviewMode(hasNullState), shouldHidePreview(), getCenterBlockLive(), gatherBlockLives());

        if (!event.isCancelledInternal()) {
            blockLiveSet = event.getBlocksForPreview();
            fakeLevel.blockLiveSet = blockLiveSet;
            previewMode = event.getPreviewMode();
            poseStack.pushPose();

            poseStack.translate(-camera.pos.x, -camera.pos.y, -camera.pos.z);


            for (BlockLive blockLive : blockLiveSet) {
                renderJsonModels(blockLive, poseStack);
            }

            for (BlockLive blockLive : blockLiveSet) {
                renderBlockEntity(blockLive, poseStack, partialTick, camera);
            }

            IOnBlockPreviewEvent.firePostEvent(previewMode, getCenterBlockLive(), blockLiveSet, poseStack, partialTick, NODE_STORAGE);

            RenderUtils.renderFromStorage(submitNodeCollector, NODE_STORAGE, previewMode, poseStack);

            poseStack.popPose();
        }


    }

    private BlockLive getCenterBlockLive() {
        return BlockLive.Live.optionalBE(centerPos, getCenterBlockState(), getCenterBlockEntity());
    }

    private void checkPlaceOnWater() {
        if (item instanceof PlaceOnWaterBlockItem) {
            blockHitResult = ItemAccessor.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            placeOnWater = level.isWaterAt(blockHitResult.getBlockPos());
        }
    }

    private boolean shouldHidePreview() {
        assert centerPos != null;
        assert blockHitResult != null;
        return !(level.getBlockState(centerPos).canBeReplaced()
                && (!level.getBlockState(blockHitResult.getBlockPos()).isAir() || placeOnWater));
    }

    @Nullable BlockPos centerPos = null;
    private BlockPos getCenterPos(BlockPos hitPos) {
        if (centerPos != null) {
            return centerPos;
        }

        assert blockHitResult != null;
        return !(level.getBlockState(hitPos).canBeReplaced() && !placeOnWater) ? hitPos.relative(blockHitResult.getDirection()) : hitPos;
    }

    private @Nullable MultiblockShape getMultiblockShape() {
        if (multiblockShape != null) {
            return multiblockShape;
        }

        assert centerPos != null;
        return currentBlock instanceof IMultiBlock multiBlock
                ? multiBlock.getMultiblockShapeNoCache(centerPos, getCenterBlockState(), level, getCenterBlockEntity())
                : null;
    }

    @Nullable BlockEntity centerBlockEntity = null;
    private @Nullable BlockEntity getCenterBlockEntity() {
        if (centerBlockEntity != null) {
            return centerBlockEntity;
        }

        assert centerPos != null;
        BlockEntity centerBlockEntity = currentBlock instanceof EntityBlock entityBlock
                ? entityBlock.newBlockEntity(centerPos, getCenterBlockState())
                : null;
        if (centerBlockEntity != null) {
            centerBlockEntity.setLevel(level);
        }
        return centerBlockEntity;
    }

    @Nullable BlockState centerState = null;
    boolean hasNullState = false;
    private BlockState getCenterBlockState() {
        if (centerState != null) {
            return centerState;
        }

        assert blockHitResult != null;
        assert currentBlock != null;

        BlockPlaceContext blockPlaceContext = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, blockHitResult);

        BlockState centerState = ((BlockItemAccessor) item).tinyMultiblockLib$getPlacementState(blockPlaceContext);
        if (centerState == null) {
            centerState = currentBlock.getStateForPlacement(blockPlaceContext);
        }

        hasNullState = centerState == null;
        if (hasNullState){
            centerState = currentBlock.defaultBlockState();

            if (currentBlock instanceof IMultiBlock multiBlock && multiBlock.makeDirectional() != null){
                IMultiBlock.DirectionContext directionContext = multiBlock.makeDirectional();
                assert directionContext != null;
                Direction dir = directionContext.directionExtractor().apply(blockPlaceContext);

                if (dir != null) {
                    centerState = centerState.setValue(directionContext.property(), dir);
                }
            }
        }

        if (currentBlock instanceof IPreviewableMultiblock multiblock){
            centerState = multiblock.getDefaultStateForPreviews(centerState, blockPlaceContext);
        }
        return centerState;
    }

    public boolean canShowPreview(ItemStack stack, Block block) {
        if (TMBLClientConfig.PREVIEWS_FOR_EVERYTHING.get())
            return true;
        if (PREVIEW_CRASHLIST.contains(block))
            return false;

        String registeredName = block.builtInRegistryHolder().getRegisteredName();

        return (stack.is(TMBLTags.ItemTags.SHOW_PREVIEW)
                || block instanceof IPreviewableMultiblock
                || TMBLClientConfig.PREVIEW_WHITELIST.get().contains(registeredName)
        ) && !TMBLClientConfig.PREVIEW_BLACKLIST.get().contains(registeredName);
    }

    private void renderBlockEntity(BlockLive blockLive, PoseStack poseStack, float partialTick, CameraRenderState camera) {
        BlockState state = blockLive.state;
        BlockPos pos = blockLive.pos;

        if (state.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }

        if (state.getBlock() instanceof EntityBlock entityBlock) {

            BlockEntity entity = entityBlock.newBlockEntity(pos, state);
            if (entity == null) return;
            entity.setLevel(fakeLevel);

            if (entity instanceof IMultiBlockEntity multiBlockEntity) {
                multiBlockEntity.setCenter(pos);
                multiBlockEntity.setPreviewMode(previewMode);
            }

            BlockEntityRenderer<BlockEntity, BlockEntityRenderState> entityRender = minecraft.getBlockEntityRenderDispatcher().getRenderer(entity);

            if (entityRender != null) {
                BlockEntityRenderState renderState = entityRender.createRenderState();

                poseStack.pushPose();
                poseStack.translate(0.0001, 0.0001, 0.0001);

                poseStack.translate(blockLive.pos.getX(), blockLive.pos.getY(), blockLive.pos.getZ());

                entityRender.extractRenderState(entity, renderState, partialTick, camera.pos, null);
                entityRender.submit(renderState, poseStack, NODE_STORAGE, camera);

                poseStack.popPose();

            }
        }
    }

    private PreviewMode checkPreviewMode(boolean hasNullState) {
        boolean canPlace = true;
        boolean entityUnobstructed = true;
        for (BlockLive blockLive : gatherBlockLives()) {
           if (canPlace) canPlace = canPlace(blockLive);
           if (entityUnobstructed) entityUnobstructed = isEntityUnobstructed(blockLive);
        }

        PreviewMode ret = canPlace ? (entityUnobstructed ? PreviewMode.PREVIEW : PreviewMode.ENTITY_BLOCKED) : PreviewMode.INVALID;
        if (ret == PreviewMode.PREVIEW && hasNullState){
            return PreviewMode.INVALID;
        }
        return ret;
    }

    private boolean isEntityUnobstructed(BlockLive blockLive) {
        BlockState state = blockLive.state;
        BlockPos pos = blockLive.pos;
        if (state.getBlock() instanceof IMultiBlock multiBlock && multiblockShape != null) {
            return multiBlock.entityUnobstructed(level, pos, state, player, multiblockShape);
        }

        return level.isUnobstructed(state, pos, CollisionContext.of(player));
    }

    private boolean canPlace(BlockLive blockLive) {
        BlockState state = blockLive.state;
        BlockPos pos = blockLive.pos;
        if (state.getBlock() instanceof IMultiBlock multiBlock && multiblockShape != null) {
            return multiBlock.canPlaceBlock(pos, level, pos, state, player, true, multiblockShape);
        };

        return state.canSurvive(fakeLevel, pos);
    }

    private void renderJsonModels(BlockLive blockLive, PoseStack poseStack) {
        if (!blockLive.state.getRenderShape().equals(RenderShape.MODEL)) return;
        assert centerPos != null;

        poseStack.pushPose();
        poseStack.translate(0.0001, 0.0001, 0.0001);

        poseStack.translate(blockLive.pos.getX(), blockLive.pos.getY(), blockLive.pos.getZ());

        NODE_STORAGE.submitMovingBlock(poseStack,
                RenderUtils.createMovingBlockRenderState(fakeLevel, blockLive.pos, blockLive.state, true, Sheets.translucentBlockItemSheet(), null, null), 0);

        poseStack.popPose();
    }

    @Nullable Set<BlockLive> blockLiveSet = null;
    private Set<BlockLive> gatherBlockLives() {
        if (blockLiveSet != null) {
            return blockLiveSet;
        }

        assert currentBlock != null;
        assert centerPos != null;
        assert centerState != null;

        Set<BlockLive> blockLiveSet = new HashSet<>();

        if (currentBlock instanceof IMultiBlock multiBlock) {
            blockLiveSet.addAll(multiBlock.prepareForPlace(multiBlock.getMultiblockShapeNoCache(centerPos, centerState, level, centerBlockEntity), level, centerPos, centerState));
        } else {
            blockLiveSet.add(BlockLive.Live.optionalBE(centerPos, centerState, centerBlockEntity));
        }

        if (!SET_PLACED_BY_BLACKLIST.contains(currentBlock)) {
            try {
                currentBlock.setPlacedBy(fakeLevel, centerPos, centerState, player, stack);
            } catch (Exception ignored) {
                Constants.LOGGER.warn("setPlacedBy failed for block {} adding to ignore list...", currentBlock);
                SET_PLACED_BY_BLACKLIST.add(currentBlock);
            }
        }

        for (BlockLive toAdd : fakeLevel.blockLiveSet) {
            if (blockLiveSet.stream().map(b -> b.pos).noneMatch(p -> p.equals(toAdd.pos))) {
                blockLiveSet.add(toAdd);
            }
        }
        this.blockLiveSet = blockLiveSet;
        return blockLiveSet;
    }
}
