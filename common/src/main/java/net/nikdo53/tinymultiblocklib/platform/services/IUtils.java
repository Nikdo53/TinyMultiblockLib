package net.nikdo53.tinymultiblocklib.platform.services;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public interface IUtils {

    Optional<Identifier> locFromRenderType(RenderType renderType);
}
