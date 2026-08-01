package net.nikdo53.tinymultiblocklib.platform;

import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.submit.RenderPhaseKeys;
import net.nikdo53.tinymultiblocklib.client.PreviewFeatureRenderer;
import net.nikdo53.tinymultiblocklib.platform.services.IUtils;

import java.util.Optional;

public class NeoForgeUtils implements IUtils {
    public static final NeoForgeUtils INSTANCE = new NeoForgeUtils();

    @Override
    public Optional<Identifier> locFromRenderType(RenderType renderType) {
        RenderSetup.TextureBinding sampler0 = renderType.state.textures.get("Sampler0");
        if (sampler0 != null) {
            return Optional.of(sampler0.location());
        }
        return Optional.empty();
    }

    @Override
    public void submitPreview(OrderedSubmitNodeCollector storage, PreviewFeatureRenderer.Submit submit) {
        storage.submitSpecial(RenderPhaseKeys.TRANSLUCENT_BLOCKS_AND_ITEMS, submit);
    }
}
