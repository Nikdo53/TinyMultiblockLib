package net.nikdo53.tinymultiblocklib.platform;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.nikdo53.tinymultiblocklib.platform.services.IUtils;

import java.util.Optional;

public class FabricUtils implements IUtils {
    public static final FabricUtils INSTANCE = new FabricUtils();


    @Override
    public Optional<Identifier> locFromRenderType(RenderType renderType) {
        RenderSetup.TextureBinding sampler0 = renderType.state.textures.get("Sampler0");
        if (sampler0 != null) {
            return Optional.of(sampler0.location());
        }
        return Optional.empty();
    }
}
