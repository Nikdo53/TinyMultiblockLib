package net.nikdo53.tinymultiblocklib.platform.services;

import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.nikdo53.tinymultiblocklib.client.PreviewFeatureRenderer;

import java.util.Optional;

public interface IUtils {

    Optional<Identifier> locFromRenderType(RenderType renderType);

    void submitPreview(OrderedSubmitNodeCollector storage, PreviewFeatureRenderer.Submit submit);
}
