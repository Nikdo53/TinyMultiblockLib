package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.tinymultiblocklib.color.IColorSupplier;
import net.nikdo53.tinymultiblocklib.mixin.BufferSourceAccessor;
import net.nikdo53.tinymultiblocklib.mixin.RenderTypeAccessor;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TintedBufferSource extends MultiBufferSource.BufferSource{
    public IColorSupplier color;
    BufferSource originalBuffer;

    public TintedBufferSource(BufferSource bufferSource, IColorSupplier color) {
        super(((BufferSourceAccessor)bufferSource).getSharedBuffer(), ((BufferSourceAccessor)bufferSource).getFixedBuffers());
        this.color = color;
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

        return new TintedVertexConsumer(original, color);
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
                loc -> RenderType.translucentMovingBlock()));
        list.add(new Pair<>("cutout_mipped",
                loc -> RenderType.translucentMovingBlock()));
        list.add(new Pair<>("cutout",
                loc -> RenderType.translucentMovingBlock()));

        return list;
    }

    public static RenderType getTranslucent(RenderType renderType) {
        Optional<Pair<String, Function<Optional<ResourceLocation>, RenderType>>> any = VALID_TYPES.stream()
                .filter(pair -> pair.getFirst().equals(getName(renderType)))
                .findAny();

        if (any.isPresent()) {
            Optional<ResourceLocation> ResourceLocation = Services.PLATFORM.getUtils().locFromRenderType(renderType);
            RenderType translucent = any.get().getSecond().apply(ResourceLocation);

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
