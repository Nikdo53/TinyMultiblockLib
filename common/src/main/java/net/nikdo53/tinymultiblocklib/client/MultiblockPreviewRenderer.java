package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.block.IPreviewableMultiblock;
import net.nikdo53.tinymultiblocklib.compat.carryon.CarryOnPreviewHelper;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.mixin.ItemAccessor;
import net.nikdo53.tinymultiblocklib.platform.services.IPlatformHelper;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiblockPreviewRenderer {
    private static final Set<Block> PREVIEWED_BLOCKS = new HashSet<>();

    public static Set<Block> getPreviewedBlocks() {
        return new HashSet<>(PREVIEWED_BLOCKS);
    }

    /**
     * Should be used in some client init method
     */
    public static synchronized void registerPreviewedBlocks(Block... previewedBlocks) {
        PREVIEWED_BLOCKS.addAll(List.of(previewedBlocks));
    }

    public static void renderMultiblockPreviews(float partialTick, Minecraft minecraft, Level level, Camera camera, PoseStack poseStack, IPlatformHelper platformHelper) {
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        LocalPlayer player = minecraft.player;
        assert player != null;
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;


        if (platformHelper.isModLoaded("carryon") && CarryOnPreviewHelper.isValidMultiblock(player)) {
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

           // if (!(PREVIEWED_BLOCKS.contains(block) || block instanceof IPreviewableMultiblock)) return;

            BlockState state = block.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, blockHitResult));
            boolean hasNullState = false;

            if (state == null){
                hasNullState = true;
                state = block.defaultBlockState();

                if (block instanceof IPreviewableMultiblock multiblock){
                    multiblock.getDefaultStateForPreviews(player.getDirection());
                }
            }

            @Nullable
            BlockEntity entity = block instanceof EntityBlock entityBlock ? entityBlock.newBlockEntity(pos, state) : null;
            if (entity != null) {
                entity.setLevel(level);
            }

            PreviewMode previewMode = getPreviewMode(level, pos, state, player, hasNullState);

            boolean shouldShowPreview = level.getBlockState(pos).canBeReplaced()
                    && (!level.getBlockState(hitPos).isAir() || placeOnWater);

            if (level.getBlockState(hitPos).canBeReplaced() && !placeOnWater)
                pos = pos.relative(hitDirection.getOpposite());

            poseStack.pushPose();

            poseStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

            IOnBlockPreviewEvent event = IOnBlockPreviewEvent.firePreEvent(previewMode, !shouldShowPreview, state, pos, player, entity, partialTick, poseStack);

            if (!event.isCancelledInternal()) {
                previewMode = event.getPreviewMode();
                state = event.getBlockState();

                renderBlockEntity(minecraft, partialTick, poseStack, entity, buffer, previewMode);
                renderJsonModels(minecraft, level, poseStack, entity, pos, state, buffer, previewMode, stack);

                IOnBlockPreviewEvent.firePostEvent(previewMode, state, pos, player, entity, partialTick, poseStack);
            }

            poseStack.popPose();

        }
    }

    private static void renderBlockEntity(Minecraft minecraft, float partialTick, PoseStack poseStack, @Nullable BlockEntity entity, MultiBufferSource.BufferSource buffer, PreviewMode previewMode) {
        if (entity == null) return;

        if (entity instanceof IMultiBlockEntity multiBlockEntity) {
            multiBlockEntity.setPreviewMode(previewMode);
        }

        BlockEntityRenderer<BlockEntity> entityRender = minecraft.getBlockEntityRenderDispatcher().getRenderer(entity);

        if (entityRender != null)
            entityRender.render(entity, partialTick, poseStack, buffer, 0xFFFFFF, OverlayTexture.NO_OVERLAY);
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

    private static void renderJsonModels(Minecraft minecraft, Level level, PoseStack poseStack, BlockEntity blockEntity,
                                         BlockPos originalPos, BlockState stateOriginal, MultiBufferSource.BufferSource buffer, PreviewMode previewMode, ItemStack stack) {

        VertexConsumerWrapper tintedConsumer = new VertexConsumerWrapper(buffer.getBuffer(RenderType.translucent())) {
            @Override
            public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha) {
                super.putBulkData(pose, quad, brightness, red * previewMode.red, green * previewMode.green, blue * previewMode.blue, alpha * previewMode.alpha, lightmap, packedOverlay, readAlpha);
            }
        };

        assert minecraft.level != null;
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();

        //multiblock logic
        if (stateOriginal.getBlock() instanceof IPreviewableMultiblock multiBlock) {

            if (!multiBlock.skipJsonRendering()) {

                poseStack.translate(0.0001, 0.0001, 0.0001);

                multiBlock.prepareForPlace(
                                multiBlock.getFullBlockShapeNoCache(level, blockEntity, originalPos, stateOriginal),
                                level, originalPos, stateOriginal).forEach(pair -> {

                            BlockState state = pair.getSecond();
                            BlockPos pos = pair.getFirst().immutable();

                            if (!state.getRenderShape().equals(RenderShape.MODEL)) return;

                            poseStack.pushPose();
                             BlockPos offset = pos.subtract(originalPos).immutable();
                            poseStack.translate(offset.getX(), offset.getY(), offset.getZ());

                            blockRenderer.renderBatched(state, pos, level, poseStack, tintedConsumer, false, minecraft.level.getRandom());

                            poseStack.popPose();

                                });
            }
        } else { // regular block logic

            gatherBlockStates(originalPos, stateOriginal, minecraft.player, stack).forEach(blocklike -> {

                poseStack.pushPose();
                BlockPos offset = blocklike.pos.subtract(originalPos).immutable();
                poseStack.translate(offset.getX(), offset.getY(), offset.getZ());

                blockRenderer.renderBatched(blocklike.state, blocklike.pos, level, poseStack, tintedConsumer, false, minecraft.level.getRandom());

                poseStack.popPose();

            });
        }

        buffer.endLastBatch();
    }

    public static Set<BlockLike> gatherBlockStates(BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        assert Minecraft.getInstance().level != null;

        FakeClientLevel fakeLevel = new FakeClientLevel(Minecraft.getInstance().level);
        state.getBlock().setPlacedBy(fakeLevel, pos, state, placer, stack);

        return fakeLevel.blockLikeSet;
    }
}
