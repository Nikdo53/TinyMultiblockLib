package net.nikdo53.tinymultiblocklib.client;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.nikdo53.tinymultiblocklib.platform.FabricPlatformHelper;

public class TMBLClientEvents {
    public static void init() {
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(TMBLClientEvents::renderLevelStageEvent);
    }

    private static void renderLevelStageEvent(WorldRenderContext context) {
      //  MultiblockPreviewRenderer.renderMultiblockPreviews(context..getGameTimeDeltaPartialTick(true), Minecraft.getInstance(), context.world(), context.camera(), context.matrixStack(), new FabricPlatformHelper());
    }
}
