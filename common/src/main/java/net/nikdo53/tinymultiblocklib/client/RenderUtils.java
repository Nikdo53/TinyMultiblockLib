package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.mixin.BlockModelRenderStateAccessor;
import net.nikdo53.tinymultiblocklib.mixin.MinecraftAccessor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RenderUtils {
    public static @NonNull SubmitNodeStorage createTranslucentNodeStorage() {
        return new SubmitNodeStorage() {
            @Override
            public <S> void submitModel(Model<? super S> p_433938_, S p_434123_, PoseStack p_434445_, RenderType renderType, int p_433912_, int p_435238_, int p_433959_, @Nullable TextureAtlasSprite p_433439_, int p_435627_, ModelFeatureRenderer.@Nullable CrumblingOverlay p_439709_) {
                super.submitModel(p_433938_, p_434123_, p_434445_, TintedBufferSource.getTranslucent(renderType), p_433912_, p_435238_, p_433959_, p_433439_, p_435627_, p_439709_);
            }

            @Override
            public <S> void submitModel(Model<? super S> model, S renderState, PoseStack poseStack, RenderType renderType, int packedLight, int packedOverlay, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
                super.submitModel(model, renderState, poseStack, TintedBufferSource.getTranslucent(renderType), packedLight, packedOverlay, outlineColor, crumblingOverlay);
            }

        };
    }

    public static void renderFromStorage(SubmitNodeStorage nodeStorage, MultiBufferSource.BufferSource buffer){
        Minecraft minecraft = Minecraft.getInstance();

        FeatureRenderDispatcher featureRenderDispatcher = new FeatureRenderDispatcher(
                nodeStorage,
                minecraft.getModelManager(),
                buffer,
                minecraft.getAtlasManager(),
                minecraft.renderBuffers().outlineBufferSource(),
                minecraft.renderBuffers().crumblingBufferSource(),
                minecraft.font,
                minecraft.gameRenderer.getGameRenderState()
        );

        featureRenderDispatcher.renderAllFeatures();

    }

    // this is bullshit
    public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    public static @NonNull BlockModelRenderState getBlockModelRenderState(BlockState state, RenderType renderType) {
        BlockModelRenderState renderState = new BlockModelRenderState();

        MinecraftAccessor minecraft = (MinecraftAccessor) Minecraft.getInstance();
        minecraft.getBlockModelResolver().update(renderState, state, BLOCK_DISPLAY_CONTEXT);
        ((BlockModelRenderStateAccessor) renderState).setRenderType(renderType);
        return renderState;
    }

    public static @NonNull MovingBlockRenderState createMovingBlockRenderState(BlockAndTintGetter level, BlockPos pos, BlockState state, boolean cull, RenderType renderType, @Nullable Integer packedLight, @Nullable Holder<Biome> biome) {
        MovingBlockRenderStateAdvanced renderState = new MovingBlockRenderStateAdvanced(level, cull, renderType);
        renderState.randomSeedPos = pos;
        renderState.blockPos = pos;
        renderState.blockState = state;
        renderState.biome = biome;
        renderState.cardinalLighting = level.cardinalLighting();
        renderState.lightEngine = level.getLightEngine();
        return renderState;
    }

    public static int getPackedLight(Level level, BlockPos blockPos) {
        return LightCoordsUtil.pack(level.getBrightness(LightLayer.BLOCK, blockPos), level.getBrightness(LightLayer.SKY, blockPos));
    }


}
