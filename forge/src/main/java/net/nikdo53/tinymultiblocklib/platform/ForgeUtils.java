package net.nikdo53.tinymultiblocklib.platform;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.tinymultiblocklib.platform.services.IUtils;

import java.util.Optional;

public class ForgeUtils implements IUtils {
    public static final ForgeUtils INSTANCE = new ForgeUtils();

    @Override
    public Optional<ResourceLocation> locFromRenderType(RenderType renderType) {
        if (renderType instanceof RenderType.CompositeRenderType compositeState)
            if (compositeState.state().textureState instanceof RenderStateShard.TextureStateShard textureStateShard){
                return textureStateShard.cutoutTexture();
            }
        return Optional.empty();
    }
}
