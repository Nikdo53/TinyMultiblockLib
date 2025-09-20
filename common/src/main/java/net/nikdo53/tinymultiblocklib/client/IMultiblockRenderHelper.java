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
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import java.util.function.Function;

public interface IMultiblockRenderHelper {

    default Function<ResourceLocation, RenderType> getRenderTypeFunction(PreviewMode previewMode) {
       return previewMode.equals(PreviewMode.PLACED) ? RenderType::entityCutout : RenderType::entityTranslucentCull;
    }

    default RenderType getRenderType(PreviewMode previewMode, ResourceLocation location) {
        return previewMode.equals(PreviewMode.PLACED) ? RenderType.entityCutout(location) : RenderType.entityTranslucentCull(location);
    }

    default VertexConsumer getConsumer(MultiBufferSource buffer, IMultiBlockEntity blockEntity, Material materialBase, Material materialCorrupted, Block blockCorrupted) {
        PreviewMode previewMode = blockEntity.getPreviewMode();

        RenderType renderTypeBase = getRenderType(previewMode, materialBase.atlasLocation());
        RenderType renderTypeCorrupted = getRenderType(previewMode, materialCorrupted.atlasLocation());

        VertexConsumer baseConsumer = materialBase.sprite().wrap(buffer.getBuffer(renderTypeBase));
        VertexConsumer corruptedConsumer = materialCorrupted.sprite().wrap(buffer.getBuffer(renderTypeCorrupted));

        return blockEntity.getBlockEntity().getBlockState().is(blockCorrupted) ? corruptedConsumer : baseConsumer;
    }


    default Level level(){
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null){
            throw new IllegalStateException("Blockentity Level is null");
        }
        return level;
    }

    default void render(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, PreviewMode previewMode) {
        float r = 1f;
        float g = 1f;
        float b = 1f;
        float alpha = 1f;

        render(modelPart, poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, alpha, previewMode);
    }

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
