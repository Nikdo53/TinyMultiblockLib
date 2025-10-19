package net.nikdo53.tinymultiblocklib.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.nikdo53.tinymultiblocklib.platform.FabricPlatformHelper;

public class TMBLClientEvents {
    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(TMBLClientEvents::renderLevelStageEvent);
    }

    private static void renderLevelStageEvent(WorldRenderContext context) {
        MultiblockPreviewRenderer.renderMultiblockPreviews(context.tickCounter().getGameTimeDeltaPartialTick(true), Minecraft.getInstance(), context.world(), context.camera(), context.matrixStack(), new FabricPlatformHelper());
    }
}
