package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.UvMapping;
import org.jspecify.annotations.Nullable;

public class TranslucentSubmitNodeStorage extends SubmitNodeStorage {
    public TranslucentSubmitNodeStorage() {
        super();
    }
    @Override
    public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, int tintedColor, @Nullable UvMapping uvMapping, int outlineColor) {
        super.submitModel(model, state, poseStack, TintedBufferSource.getTranslucent(renderType), lightCoords, overlayCoords, tintedColor, uvMapping, outlineColor);
    }
}
