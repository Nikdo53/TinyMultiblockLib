package net.nikdo53.tinymultiblocklib.platform.services;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IUtils {

    Optional<ResourceLocation> locFromRenderType(RenderType renderType);
}
