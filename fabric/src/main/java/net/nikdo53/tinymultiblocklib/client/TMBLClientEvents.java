package net.nikdo53.tinymultiblocklib.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.nikdo53.tinymultiblocklib.client.ghost.GhostRenderer;

public class TMBLClientEvents {
    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(TMBLClientEvents::renderLevelStageEvent);
        ClientTickEvents.END_CLIENT_TICK.register(TMBLClientEvents::clientTick);
    }

    private static void renderLevelStageEvent(WorldRenderContext event) {
        MultiblockPreviewRenderer.tryRenderMultiblockPreviews(
                event.tickDelta(),
                event.camera(),
                event.matrixStack()
        );

        GhostRenderer.renderAll(
                event.tickDelta(),
                event.camera(),
                event.matrixStack()
        );
    }

    public static void clientTick(Minecraft minecraft){
        CommonClientEvents.onClientTick();
    }
}
