package net.nikdo53.tinymultiblocklib.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class ClientEvents {
    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ClientEvents::renderLevelStageEvent);
    }

    private static void renderLevelStageEvent(WorldRenderContext context) {
        MultiblockPreviewRenderer.renderMultiblockPreviews(context.tickDelta(), Minecraft.getInstance(), context.world(), context.camera(), context.matrixStack());
    }

}
