package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.mixin.BufferSourceAccessor;
import net.nikdo53.tinymultiblocklib.mixin.RenderTypeAccessor;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TintedBufferSource extends MultiBufferSource.BufferSource{
    PreviewMode previewMode;
    BufferSource originalBuffer;

    protected TintedBufferSource(BufferSource bufferSource, PreviewMode previewMode) {
        super(((BufferSourceAccessor)bufferSource).getSharedBuffer(), ((BufferSourceAccessor)bufferSource).getFixedBuffers());
        this.previewMode = previewMode;
        this.originalBuffer = bufferSource;
    }

    @Override
    public void endLastBatch() {
        originalBuffer.endLastBatch();
    }

    @Override
    public void endBatch() {
        originalBuffer.endBatch();
    }

    @Override
    public void endBatch(RenderType renderType) {
        originalBuffer.endBatch(renderType);
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        VertexConsumer original = originalBuffer.getBuffer(getTranslucent(renderType));

        return new VertexConsumerWrapper(original) {

            @Override
            public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
                float[] colors = previewMode.applyColorsFloat(red, green, blue, alpha);
                super.vertex(x, y, z, colors[0], colors[1], colors[2], colors[3], texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
            }

            @Override
            public VertexConsumer color(int r, int g, int b, int a) {
                float[] colors = previewMode.applyColorsFloat(r, g, b, a);
                return super.color(((int) colors[0]), (int) colors[1], (int) colors[2], (int) colors[3]);
            }

            @Override
            public VertexConsumer color(float red, float green, float blue, float alpha) {
                float[] colors = previewMode.applyColorsFloat(red, green, blue, alpha);
                return super.color(colors[0], colors[1], colors[2], colors[3]);
            }

            @Override
            public VertexConsumer color(int colorARGB) {
                return super.color(previewMode.applyColors(colorARGB));
            }
        };
    }

    public static final List<Pair<String, Function<Optional<ResourceLocation>, RenderType>>> VALID_TYPES = getValidTypes();

    private static @NotNull List<Pair<String, Function<Optional<ResourceLocation>, RenderType>>> getValidTypes() {
        List<Pair<String, Function<Optional<ResourceLocation>, RenderType>>> list = new ArrayList<>();
        list.add(new Pair<>("entity_solid",
                loc -> renderTypeOrNull(loc, RenderType::entityTranslucentCull)));
        list.add(new Pair<>("entity_cutout",
                loc -> renderTypeOrNull(loc, RenderType::entityTranslucentCull)));
        list.add(new Pair<>("entity_cutout_no_cull",
                loc -> renderTypeOrNull(loc, RenderType::entityTranslucent)));
        list.add(new Pair<>("entity_cutout_no_cull_z_offset",
                loc -> renderTypeOrNull(loc, RenderType::entityTranslucent)));
        list.add(new Pair<>("entity_smooth_cutout",
                loc -> renderTypeOrNull(loc, RenderType::entityTranslucentCull)));
        list.add(new Pair<>("solid",
                loc -> RenderType.translucent()));
        list.add(new Pair<>("cutout_mipped",
                loc -> RenderType.translucent()));
        list.add(new Pair<>("cutout",
                loc -> RenderType.translucent()));



        return list;
    }

    public static RenderType getTranslucent(RenderType renderType) {
        Optional<Pair<String, Function<Optional<ResourceLocation>, RenderType>>> any = VALID_TYPES.stream()
                .filter(pair -> pair.getFirst().equals(getName(renderType)))
                .findAny();

        if (any.isPresent()) {
            Optional<ResourceLocation> resourceLocation = Services.PLATFORM.getUtils().locFromRenderType(renderType);
            RenderType translucent = any.get().getSecond().apply(resourceLocation);

            if (translucent != null) {
                return translucent;
            }
        }

        return renderType;
    }

    public static RenderType renderTypeOrNull(Optional<ResourceLocation> location, Function<ResourceLocation, RenderType> function){
        return location.map(function).orElse(null);
    }

    public static String getName(RenderType renderType) {
        return ((RenderTypeAccessor) renderType).getName();
    }
}
