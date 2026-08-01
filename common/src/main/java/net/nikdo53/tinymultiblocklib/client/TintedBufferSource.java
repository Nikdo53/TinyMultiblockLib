package net.nikdo53.tinymultiblocklib.client;

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

// past 26.2 just contains util methods im too lazy to clean up
public class TintedBufferSource {

    public static final List<Pair<String, Function<Optional<Identifier>, RenderType>>> VALID_TYPES = getValidTypes();

    private static List<Pair<String, Function<Optional<Identifier>, @Nullable RenderType>>> getValidTypes() {
        List<Pair<String, Function<Optional<Identifier>, @Nullable RenderType>>> list = new ArrayList<>();
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
        Optional<Pair<String, Function<Optional<Identifier>, @Nullable RenderType>>> any = VALID_TYPES.stream()
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

    public static @Nullable RenderType renderTypeOrNull(Optional<Identifier> location, Function<Identifier, RenderType> function){
        return location.map(function).orElse(null);
    }

    public static String getName(RenderType renderType) {
        return ((RenderTypeAccessor) renderType).getName();
    }
}
