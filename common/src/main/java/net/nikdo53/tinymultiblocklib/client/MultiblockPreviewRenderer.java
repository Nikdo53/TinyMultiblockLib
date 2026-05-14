package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
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
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.block.IPreviewableMultiblock;
import net.nikdo53.tinymultiblocklib.compat.carryon.CarryOnPreviewHelper;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.NotRandomSource;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.data.TMBLTags;
import net.nikdo53.tinymultiblocklib.mixin.ItemAccessor;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultiblockPreviewRenderer {
    public static final SubmitNodeStorage NODE_STORAGE = RenderUtils.createTranslucentNodeStorage();

    public static final RandomSource NOT_RANDOM = new NotRandomSource();

    public static void renderMultiblockPreviews(float partialTick, Minecraft minecraft, Level level, CameraRenderState camera, PoseStack poseStack, LevelRenderer levelRenderer, LevelRenderState renderState) {
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        LocalPlayer player = minecraft.player;
        assert player != null;
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();

        double camX = camera.pos.x;
        double camY = camera.pos.y;
        double camZ = camera.pos.z;

        if (Services.PLATFORM.isModLoaded("carryon") && CarryOnPreviewHelper.isValidMultiblock(player)) {
            item = CarryOnPreviewHelper.getMultiblockItem(player);
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
            BlockPos hitPos = blockHitResult.getBlockPos();
            BlockPos pos = hitPos.relative(hitDirection);

             if (!(stack.is(TMBLTags.ItemTags.SHOW_PREVIEW) || block instanceof IPreviewableMultiblock)) return;

            BlockState state = block.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, blockHitResult));
            boolean hasNullState = false;

            if (state == null){
                hasNullState = true;
                state = block.defaultBlockState();

                if (block instanceof IPreviewableMultiblock multiblock){
                    multiblock.getDefaultStateForPreviews(player.getDirection());
                }
            }

            BlockEntity blockEntity = block instanceof EntityBlock entityBlock ? entityBlock.newBlockEntity(pos, state) : null;
            if (blockEntity != null) {
                blockEntity.setLevel(level);
            }

            PreviewMode previewMode = getPreviewMode(level, pos, state, player, hasNullState);

            boolean shouldShowPreview = level.getBlockState(pos).canBeReplaced()
                    && (!level.getBlockState(hitPos).isAir() || placeOnWater);

            if (level.getBlockState(hitPos).canBeReplaced() && !placeOnWater)
                pos = pos.relative(hitDirection.getOpposite());

            poseStack.pushPose();

            poseStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

            FakeClientLevel fakeLevel = FakeClientLevel.getOrThrow();
            Set<BlockLive> blockLiveSet = gatherBlockLikes(fakeLevel, level, blockEntity, pos, state, minecraft.player, stack);

            IOnBlockPreviewEvent event = IOnBlockPreviewEvent.firePreEvent(previewMode, !shouldShowPreview, state, pos, player, blockEntity, partialTick, poseStack, blockLiveSet);

            if (!event.isCancelledInternal()) {
                blockLiveSet = event.getBlocksForPreview();
                fakeLevel.blockLiveSet = blockLiveSet;
                previewMode = event.getPreviewMode();

                MultiBufferSource.BufferSource tintedBuffer = new TintedBufferSource(buffer, previewMode);
                VertexConsumer vertexConsumer = tintedBuffer.getBuffer(RenderTypes.translucentMovingBlock());

                for (BlockLive blockLive : blockLiveSet) {
                    renderJsonModels(blockLive, pos, poseStack, vertexConsumer, minecraft, fakeLevel);
                }

                tintedBuffer.endLastBatch();

                for (BlockLive blockLive : blockLiveSet) {
                    renderBlockEntity(blockLive, pos, poseStack, partialTick,  tintedBuffer, minecraft, fakeLevel, camera, levelRenderer);
                }

                RenderUtils.renderFromStorage(NODE_STORAGE, tintedBuffer);
                IOnBlockPreviewEvent.firePostEvent(previewMode, state, pos, player, blockEntity, partialTick, poseStack, blockLiveSet);

            }

            poseStack.popPose();

        }
    }

    private static void renderBlockEntity(BlockLive blockLive, BlockPos originalPos, PoseStack poseStack, float partialTick,
                                          MultiBufferSource.BufferSource buffer, Minecraft minecraft, FakeClientLevel fakeClientLevel, CameraRenderState camera, LevelRenderer levelRenderer) {

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

    private static @NotNull PreviewMode getPreviewMode(Level level, BlockPos pos, BlockState state, LocalPlayer player, boolean hasNullState) {
        if (hasNullState) return PreviewMode.INVALID;

        boolean multiBlockCanPlace = canPlace(level, pos, state, player);
        boolean entityUnobstructed = isEntityUnobstructed(level, state.getBlock(), pos, state, player);

        return multiBlockCanPlace ? (entityUnobstructed ? PreviewMode.PREVIEW : PreviewMode.ENTITY_BLOCKED) : PreviewMode.INVALID;
    }

    private static boolean isEntityUnobstructed(Level level, Block block, BlockPos pos, BlockState state, LocalPlayer player) {
        if (block instanceof IPreviewableMultiblock multiBlock) {
            return multiBlock.entityUnobstructed(level, pos, state, player);
        }
        return level.isUnobstructed(state, pos, CollisionContext.of(player));
    }

    private static boolean canPlace(Level level, BlockPos pos, BlockState state, LocalPlayer player) {
        if (state.getBlock() instanceof IPreviewableMultiblock multiBlock) {
            return multiBlock.canPlace(level, pos, state, player, true);
        }

        return state.canSurvive(level, pos);
    }

    private static void renderJsonModels(BlockLive blockLive, BlockPos originalPos, PoseStack poseStack, VertexConsumer vertexConsumer, Minecraft minecraft, FakeClientLevel fakeLevel) {

        if (!blockLive.state.getRenderShape().equals(RenderShape.MODEL)) return;

        var blockRenderer = minecraft.getModelManager();
        Level level = minecraft.level;

        poseStack.pushPose();
        poseStack.translate(0.0001, 0.0001, 0.0001);

        BlockPos offset = blockLive.pos.subtract(originalPos).immutable();
        poseStack.translate(offset.getX(), offset.getY(), offset.getZ());

        ArrayList<BlockStateModelPart> parts = new ArrayList<>();
        blockRenderer.getBlockStateModelSet().get(blockLive.state).collectParts(NOT_RANDOM, parts);

        NODE_STORAGE.submitMovingBlock(poseStack,
                RenderUtils.createMovingBlockRenderState(fakeLevel, blockLive.pos, blockLive.state, true, Sheets.translucentBlockSheet(), null, null));

        poseStack.popPose();
    }

    public static Set<BlockLive> gatherBlockLikes(FakeClientLevel fakeLevel, Level level, BlockEntity mbEntity, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Set<BlockLive> blockLiveSet = new HashSet<>();

        if (state.getBlock() instanceof IMultiBlock multiBlock) {
            blockLiveSet.addAll(multiBlock.prepareForPlace(multiBlock.getFullBlockShapeNoCache(level, mbEntity, pos, state), level, pos, state));
        } else {
            blockLiveSet.add(new BlockLive(pos, state));        }

        state.getBlock().setPlacedBy(fakeLevel, pos, state, placer, stack);

        blockLiveSet.addAll(fakeLevel.blockLiveSet);

        return blockLiveSet;
    }
}
