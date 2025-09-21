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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import java.util.function.Function;

/**
 * A helper interface for making previewable multiblock entities.
 * */
public interface IMultiblockRenderHelper {

    /**
     * Returns a translucent RenderType for previews. Use instead of specifying the RenderType directly.
     * */
    default Function<ResourceLocation, RenderType> getRenderTypeFunction(PreviewMode previewMode) {
       return previewMode.equals(PreviewMode.PLACED) ? RenderType::entityCutout : RenderType::entityTranslucentCull;
    }

    default RenderType getRenderType(PreviewMode previewMode, ResourceLocation location) {
        return previewMode.equals(PreviewMode.PLACED) ? RenderType.entityCutout(location) : RenderType.entityTranslucentCull(location);
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
        float r = 1f;
        float g = 1f;
        float b = 1f;
        float alpha = 1f;

        render(modelPart, poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, alpha, previewMode);
    }

    /**
     * Should be used instead of {@link ModelPart#render(PoseStack, VertexConsumer, int, int)}
     * <p>
     * Applies the correct color + alpha according to the supplied PreviewMode
     * */
    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float r, float g, float b, float alpha, PreviewMode previewMode) {

        switch (previewMode) {
            case PREVIEW -> alpha *= PreviewMode.PREVIEW.alpha;

            case INVALID -> {
                r *= PreviewMode.INVALID.red;
                g *= PreviewMode.INVALID.green;
                b *= PreviewMode.INVALID.blue;
                alpha *= PreviewMode.INVALID.alpha;
            }
        }

        modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, alpha);
    }
}
