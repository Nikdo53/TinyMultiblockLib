package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
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
            public void putBakedQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance) {
                instance.multiplyColor(previewMode.packedARGB());
                super.putBakedQuad(pose, quad, instance);
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

    public static final List<Pair<String, Function<Optional<Identifier>, RenderType>>> VALID_TYPES = getValidTypes();

    private static @NotNull List<Pair<String, Function<Optional<Identifier>, RenderType>>> getValidTypes() {
        List<Pair<String, Function<Optional<Identifier>, RenderType>>> list = new ArrayList<>();
        list.add(new Pair<>("entity_solid",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucentCullItemTarget)));
        list.add(new Pair<>("entity_cutout",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucentCullItemTarget)));
        list.add(new Pair<>("entity_cutout_no_cull",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));
        list.add(new Pair<>("entity_cutout_no_cull_z_offset",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));
        list.add(new Pair<>("entity_smooth_cutout",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucentCullItemTarget)));
        list.add(new Pair<>("solid",
                loc -> RenderTypes.translucentMovingBlock()));
        list.add(new Pair<>("cutout_mipped",
                loc -> RenderTypes.translucentMovingBlock()));
        list.add(new Pair<>("cutout",
                loc -> RenderTypes.translucentMovingBlock()));

        return list;
    }

    public static RenderType getTranslucent(RenderType renderType) {
        Optional<Pair<String, Function<Optional<Identifier>, RenderType>>> any = VALID_TYPES.stream()
                .filter(pair -> pair.getFirst().equals(getName(renderType)))
                .findAny();

        if (any.isPresent()) {
            Optional<Identifier> Identifier = Services.PLATFORM.getUtils().locFromRenderType(renderType);
            RenderType translucent = any.get().getSecond().apply(Identifier);

            if (translucent != null) {
                return translucent;
            }
        }

        return renderType;
    }

    public static RenderType renderTypeOrNull(Optional<Identifier> location, Function<Identifier, RenderType> function){
        return location.map(function).orElse(null);
    }

    public static String getName(RenderType renderType) {
        return ((RenderTypeAccessor) renderType).getName();
    }
}
