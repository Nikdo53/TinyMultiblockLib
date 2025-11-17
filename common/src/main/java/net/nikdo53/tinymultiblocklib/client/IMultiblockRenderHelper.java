package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import java.util.function.Function;

/**
 * A helper interface for making previewable multiblock entities.
 * */
public interface IMultiblockRenderHelper {

    /**
     * Returns a translucent RenderType for when rendered as a preview. Use instead of specifying the RenderType directly.
     * */
    default Function<ResourceLocation, RenderType> getRenderTypeFunction(PreviewMode previewMode) {
       return getRenderTypeFunction(previewMode, RenderType::entityCutout);
    }

    default Function<ResourceLocation, RenderType> getRenderTypeFunction(PreviewMode previewMode, Function<ResourceLocation, RenderType> defaultRenderType) {
        return previewMode.equals(PreviewMode.PLACED) ? defaultRenderType : RenderType::entityTranslucentCull;
    }

    default RenderType getRenderType(PreviewMode previewMode, ResourceLocation texture) {
        return getRenderType(previewMode, texture, RenderType.entityCutout(texture));
    }

    default RenderType getRenderType(PreviewMode previewMode, ResourceLocation texture, RenderType defaultRenderType) {
        return previewMode.equals(PreviewMode.PLACED) ? defaultRenderType : RenderType.entityTranslucentCull(texture);
    }


    /**
     * For when you use the same BER for 2 different blocks and just swap out the textures.
     * <p>
     * It's necessary to make multiple VertexConsumers instead of just 1, otherwise the RenderTypes won't apply correctly
     * */
    default VertexConsumer getConsumer(MultiBufferSource buffer, IMultiBlockEntity blockEntity, Material materialBase, Material materialSecondary, Block blockSecondary) {
        PreviewMode previewMode = blockEntity.getPreviewMode();

        RenderType renderTypeBase = getRenderType(previewMode, materialBase.atlasLocation());
        RenderType renderTypeCorrupted = getRenderType(previewMode, materialSecondary.atlasLocation());

        VertexConsumer baseConsumer = materialBase.sprite().wrap(buffer.getBuffer(renderTypeBase));
        VertexConsumer corruptedConsumer = materialSecondary.sprite().wrap(buffer.getBuffer(renderTypeCorrupted));

        return blockEntity.getBlockEntity().getBlockState().is(blockSecondary) ? corruptedConsumer : baseConsumer;
    }


    /**
     * A somewhat cleaner way to get the level
     * */
    default Level level(){
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null){
            throw new IllegalStateException("BlockEntityRenderer Level is null");
        }
        return level;
    }

    /**
     * Should be used instead of {@link ModelPart#render(PoseStack, VertexConsumer, int, int)}
     * <p>
     * Applies the correct color + alpha according to the supplied PreviewMode
     * */
    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, PreviewMode previewMode) {
        render(modelPart, poseStack, vertexConsumer, packedLight, packedOverlay, 0xffffffff, previewMode);
    }

    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int r, int g, int b, int alpha, PreviewMode previewMode) {
        render(modelPart, poseStack, vertexConsumer, packedLight, packedOverlay, FastColor.ARGB32.color(alpha, r, g, b), previewMode);
    }

    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color, PreviewMode previewMode) {
        modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, previewMode.applyColors(color));
    }
}
