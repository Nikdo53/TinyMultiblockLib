package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.block.IPreviewableMultiblock;
import net.nikdo53.tinymultiblocklib.compat.carryon.CarryOnPreviewHelper;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.mixin.ItemAccessor;
import net.nikdo53.tinymultiblocklib.platform.services.IPlatformHelper;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;

public class MultiblockPreviewRenderer {

    public static void renderMultiblockPreviews(float partialTick, Minecraft minecraft, Level level, Camera camera, PoseStack poseStack, IPlatformHelper platformHelper) {
        LocalPlayer player = minecraft.player;
        assert player != null;
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();

        if (platformHelper.isModLoaded("carryon") && CarryOnPreviewHelper.isValidMultiblock(player)) {
            item = CarryOnPreviewHelper.getMultiblockItem(player);
        }

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof IPreviewableMultiblock multiBlock && blockItem.getBlock() instanceof EntityBlock block) {
            HitResult hitResult = minecraft.hitResult;

            if (hitResult instanceof BlockHitResult blockHitResult){
                boolean placeOnWater = false;

                if (blockItem instanceof PlaceOnWaterBlockItem) {
                    blockHitResult = ItemAccessor.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
                    placeOnWater = level.isWaterAt(blockHitResult.getBlockPos());
                };

                Direction hitDirection = blockHitResult.getDirection();
                BlockPos hitPos = blockHitResult.getBlockPos();
                BlockPos pos =  hitPos.relative(hitDirection);

                BlockState state = multiBlock.getDefaultStateForPreviews(player.getDirection());
                BlockEntity entity = block.newBlockEntity(pos, state);

                boolean shouldShowPreview = level.getBlockState(pos).canBeReplaced() && (!level.getBlockState(hitPos).isAir() || placeOnWater);
                if (entity instanceof IMultiBlockEntity multiBlockEntity && shouldShowPreview) {
                    entity.setLevel(level);

                    boolean multiBlockCanPlace = multiBlock.canPlace(level, pos, state, player, true);
                    boolean entityUnobstructed = multiBlock.entityUnobstructed(level, pos, state, player);

                    PreviewMode previewMode = multiBlockCanPlace ? (entityUnobstructed ? PreviewMode.PREVIEW : PreviewMode.ENTITY_BLOCKED) : PreviewMode.INVALID;
                    multiBlockEntity.setPreviewMode(previewMode);

                    if (level.getBlockState(hitPos).canBeReplaced() && !placeOnWater) pos = pos.relative(hitDirection.getOpposite());

                    poseStack.pushPose();

                    double camX = camera.getPosition().x;
                    double camY = camera.getPosition().y;
                    double camZ = camera.getPosition().z;
                    poseStack.translate(pos.getX() - camX,pos.getY() - camY,pos.getZ() - camZ);


                    MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
                    BlockEntityRenderer<BlockEntity> entityRender = minecraft.getBlockEntityRenderDispatcher().getRenderer(entity);

                    if (entityRender != null) entityRender.render(entity, partialTick, poseStack, buffer, 0xFFFFFF, OverlayTexture.NO_OVERLAY);

                    if (!multiBlock.skipJsonRendering()) renderJsonModels(minecraft, level, poseStack, multiBlock, pos, state, buffer, previewMode);

                    poseStack.popPose();

                }
            }
        }
    }

    private static void renderJsonModels(Minecraft minecraft, Level level, PoseStack poseStack, IPreviewableMultiblock multiBlock, BlockPos originalPos, BlockState stateOriginal, MultiBufferSource.BufferSource buffer, PreviewMode previewMode) {
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        poseStack.translate(0.0001,0.0001,0.0001);

        multiBlock.prepareForPlace(level, originalPos, stateOriginal).forEach(pair -> {

            BlockState state = pair.getSecond();
            BlockPos pos = pair.getFirst().immutable();

            if (!state.getRenderShape().equals(RenderShape.MODEL)) return;

            BlockPos offset = pos.subtract(originalPos).immutable();
            poseStack.translate(offset.getX(),  offset.getY(), offset.getZ());

            VertexConsumerWrapper tintedConsumer = new VertexConsumerWrapper(buffer.getBuffer(RenderType.translucent())) {
                @Override
                public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] colorMuls, float red, float green, float blue, int[] combinedLights, int combinedOverlay, boolean mulColor) {
                    super.putBulkData(pose, quad, colorMuls, red, green, blue, combinedLights, combinedOverlay, mulColor);
                }

                @Override
                public VertexConsumer color(float red, float green, float blue, float alpha) {
                    return super.color(red * previewMode.red, green * previewMode.green, blue * previewMode.blue, alpha * previewMode.alpha);
                }
            };

            blockRenderer.renderBatched(state, pos, level, poseStack, tintedConsumer, false, minecraft.level.getRandom());

            buffer.endLastBatch();

            poseStack.translate(-offset.getX(),  -offset.getY(), -offset.getZ());
        });
    }

}
