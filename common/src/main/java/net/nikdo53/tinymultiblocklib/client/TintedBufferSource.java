package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.nikdo53.tinymultiblocklib.mixin.RenderTypeAccessor;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

// past 26.2 just contains util methods im too lazy to clean up
public class TintedBufferSource {

    public static final List<Pair<String, Function<Optional<Identifier>, RenderType>>> VALID_TYPES = getExtraTypes();

    private static List<Pair<String, Function<Optional<Identifier>, @Nullable RenderType>>> getExtraTypes() {
        List<Pair<String, Function<Optional<Identifier>, @Nullable RenderType>>> list = new ArrayList<>();
        list.add(new Pair<>("entity_cutout_no_cull",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));
        list.add(new Pair<>("entity_cutout_no_cull_z_offset",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));
        list.add(new Pair<>("armor_cutout_no_cull",
                loc -> renderTypeOrNull(loc, RenderTypes::entityTranslucent)));

        return list;
    }

    public static RenderType getTranslucent(RenderType renderType) {
        Optional<Pair<String, Function<Optional<Identifier>, @Nullable RenderType>>> any = VALID_TYPES.stream()
                .filter(pair -> pair.getFirst().equals(getName(renderType)))
                .findAny();

        if (any.isPresent()) {
            RenderType translucent = getRenderTypeFromFunction(renderType, any.get().getSecond());
            if (translucent != null)
                return translucent;
        }

        PrimitiveTopology mode = renderType.state.pipeline.getPrimitiveTopology();
        VertexFormat format = renderType.state.pipeline.getVertexFormatBinding(0);
        if (mode == PrimitiveTopology.QUADS){
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

    public static @Nullable RenderType renderTypeOrNull(Optional<Identifier> location, Function<Identifier, RenderType> function){
        return location.map(function).orElse(null);
    }

    public static String getName(RenderType renderType) {
        return ((RenderTypeAccessor) renderType).getName();
    }
}
