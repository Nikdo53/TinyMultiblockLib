package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jspecify.annotations.Nullable;

public class RenderUtils {
    public static TranslucentSubmitNodeStorage createTranslucentNodeStorage() {
        return new TranslucentSubmitNodeStorage();
    }

    public static void renderFromStorage(OrderedSubmitNodeCollector realCollector, SubmitNodeStorage previewCollector, IColorSupplier color, @Nullable PoseStack poseStack) {
        Services.PLATFORM.getUtils().submitPreview(realCollector, new PreviewFeatureRenderer.Submit(poseStack != null ? poseStack.last().pose() : null, previewCollector, color));
    }

    public static MovingBlockRenderState createMovingBlockRenderState(BlockAndTintGetter level, BlockPos pos, BlockState state, boolean cull, RenderType renderType, @Nullable Integer packedLight, @Nullable Holder<Biome> biome) {
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
