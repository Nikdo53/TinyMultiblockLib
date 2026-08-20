package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.nikdo53.nikdocolor.IColorSupplier;
import net.nikdo53.tinymultiblocklib.mixin.BufferSourceAccessor;
import net.nikdo53.tinymultiblocklib.mixin.RenderTypeAccessor;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class TintedBufferSource extends MultiBufferSource.BufferSource{
    public IColorSupplier color;
    public BufferSource originalBuffer;
    public UnaryOperator<RenderType> renderTypeTransformer;

    public TintedBufferSource(BufferSource bufferSource, IColorSupplier color, UnaryOperator<RenderType> renderTypeTransformer) {
        super(((BufferSourceAccessor)bufferSource).getSharedBuffer(), ((BufferSourceAccessor)bufferSource).getFixedBuffers());
        this.color = color;
        this.originalBuffer = bufferSource;
        this.renderTypeTransformer = renderTypeTransformer;
    }

    public TintedBufferSource(BufferSource bufferSource, IColorSupplier color) {
        this(bufferSource, color, TintedBufferSource::getTranslucent);
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
        VertexConsumer original = originalBuffer.getBuffer(renderTypeTransformer.apply(renderType));

        return new TintedVertexConsumer(original, color);
    }

    public static final List<Pair<String, Function<Optional<Identifier>, RenderType>>> VALID_TYPES = getExtraTypes();

    private static @NotNull List<Pair<String, Function<Optional<Identifier>, RenderType>>> getExtraTypes() {
        List<Pair<String, Function<Optional<Identifier>, RenderType>>> list = new ArrayList<>();
        list.add(new Pair<>("entity_cutout_no_cull",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));
        list.add(new Pair<>("entity_cutout_no_cull_z_offset",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));
        list.add(new Pair<>("armor_cutout_no_cull",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));

        return list;
    }

    public static RenderType getTranslucent(RenderType renderType) {
        Optional<Pair<String, Function<Optional<Identifier>, RenderType>>> any = VALID_TYPES.stream()
                .filter(pair -> pair.getFirst().equals(getName(renderType)))
                .findAny();

        if (any.isPresent()) {
            RenderType translucent = getRenderTypeFromFunction(renderType, any.get().getSecond());
            if (translucent != null)
                return translucent;
        }

        VertexFormat.Mode mode = ((RenderTypeAccessor) renderType).getMode();
        VertexFormat format = ((RenderTypeAccessor) renderType).getFormat();
        if (mode == VertexFormat.Mode.QUADS){
            if (format == DefaultVertexFormat.BLOCK){
                return RenderTypes.translucentMovingBlock();
            } else if (format == DefaultVertexFormat.ENTITY){
                Optional<Identifier> resourceLocation = Services.PLATFORM.getUtils().locFromRenderType(renderType);
                if (resourceLocation.isPresent()){
                    return RenderTypes.entityTranslucentCullItemTarget(resourceLocation.get());
                }
            }
        }

        return renderType;
    }

    private static @Nullable RenderType getRenderTypeFromFunction(RenderType renderType, Function<Optional<Identifier>, RenderType> func) {
        Optional<Identifier> loc = Services.PLATFORM.getUtils().locFromRenderType(renderType);

        return func.apply(loc);
    }

    public static RenderType renderTypeOrNull(Optional<Identifier> location, Function<Identifier, RenderType> function){
        return location.map(function).orElse(null);
    }

    public static String getName(RenderType renderType) {
        return ((RenderTypeAccessor) renderType).getName();
    }
}
