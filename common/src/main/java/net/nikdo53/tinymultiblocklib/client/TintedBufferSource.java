package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
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
            public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha) {
                original.putBulkData(pose, quad, brightness, red * previewMode.red, green * previewMode.green, blue * previewMode.blue, alpha * previewMode.alpha, lightmap, packedOverlay, readAlpha);
            }

            @Override
            public void addVertex(float x, float y, float z, int color, float u, float v, int packedOverlay, int packedLight, float normalX, float normalY, float normalZ) {
                original.addVertex(x, y, z, previewMode.applyColors(color), u, v, packedOverlay, packedLight, normalX, normalY, normalZ);
            }

            @Override
            public VertexConsumer setColor(int r, int g, int b, int a) {
                return original.setColor(r * previewMode.red, g * previewMode.green, b * previewMode.blue, a * previewMode.alpha);
            }

            @Override
            public VertexConsumer setColor(float red, float green, float blue, float alpha) {
                return original.setColor(red * previewMode.red, green * previewMode.green, blue * previewMode.blue, alpha * previewMode.alpha);
            }

            @Override
            public VertexConsumer setColor(int color) {
                return original.setColor(previewMode.applyColors(color));
            }
        };
    }

    public static final List<Pair<String, Boolean>> VALID_TYPES = getValidTypes();

    private static @NotNull List<Pair<String, Boolean>> getValidTypes() {
        List<Pair<String, Boolean>> list = new ArrayList<>();
        list.add(new Pair<>("entity_solid", true));
        list.add(new Pair<>("entity_cutout", true));
        list.add(new Pair<>("entity_cutout_no_cull", false));
        list.add(new Pair<>("entity_cutout_no_cull_z_offset", false));
        list.add(new Pair<>("entity_smooth_cutout", true));
        list.add(new Pair<>("entity_no_outline", true));

        return list;
    }

    public static RenderType getTranslucent(RenderType renderType) {
        Optional<Pair<String, Boolean>> any = VALID_TYPES.stream()
                .filter(pair -> pair.getFirst().equals(getName(renderType)))
                .findAny();

        if (any.isPresent()) {
            Optional<ResourceLocation> resourceLocation = Services.PLATFORM.getUtils().locFromRenderType(renderType);

            if (resourceLocation.isPresent()) {
               if (any.get().getSecond()){
                   return RenderType.entityTranslucentCull(resourceLocation.get());
               }else {
                   return RenderType.entityTranslucent(resourceLocation.get());
               }
            }
        }

        return renderType;
    }

    public static String getName(RenderType renderType) {
        return ((RenderTypeAccessor) renderType).getName();
    }
}
