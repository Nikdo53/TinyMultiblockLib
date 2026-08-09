package net.nikdo53.tinymultiblocklib.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.nikdo53.tinymultiblocklib.client.ghost.GhostRenderer;
import net.nikdo53.tinymultiblocklib.platform.FabricPlatformHelper;

public class TMBLClientEvents {
    public static void init() {
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(TMBLClientEvents::renderLevelStageEvent);
        ClientTickEvents.END_CLIENT_TICK.register(TMBLClientEvents::clientTick);
    }

    private static void renderLevelStageEvent(LevelRenderContext event) {
        MultiblockPreviewRenderer.tryRenderMultiblockPreviews(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(true),
                event.levelState().cameraRenderState,
                event.poseStack()
        );

        GhostRenderer.renderAll(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(false),
                event.levelState().cameraRenderState,
                event.poseStack()
        );
    }

    public static void clientTick(Minecraft minecraft){
        CommonClientEvents.onClientTick();
    }
}
