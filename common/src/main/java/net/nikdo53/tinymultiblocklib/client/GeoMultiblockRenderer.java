package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Works just like the GeoBlockRenderer
 * but automatically changes the multiblock based on its preview mode
 * */
public abstract class GeoMultiblockRenderer<T extends BlockEntity & GeoAnimatable & IMultiBlockEntity> extends GeoBlockRenderer<T> implements IMultiblockRenderHelper {
    public GeoMultiblockRenderer(BlockEntityType<? extends T> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return getRenderType(animatable.getPreviewMode(), texture, super.getRenderType(animatable, texture, bufferSource, partialTick));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!animatable.isCenter()) return;

        switch (animatable.getPreviewMode()) {
            case PREVIEW -> alpha *= PreviewMode.PREVIEW.alpha;

            case INVALID -> {
                red *= PreviewMode.INVALID.red;
                green *= PreviewMode.INVALID.green;
                blue *= PreviewMode.INVALID.blue;
                alpha *= PreviewMode.INVALID.alpha;
            }
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
