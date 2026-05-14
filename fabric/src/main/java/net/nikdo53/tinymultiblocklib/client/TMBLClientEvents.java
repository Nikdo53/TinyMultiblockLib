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
        MultiblockPreviewRenderer.renderMultiblockPreviews(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(true),
                Minecraft.getInstance(),
                Minecraft.getInstance().level,
                event.levelState().cameraRenderState,
                event.poseStack(),
                event.levelRenderer(),
                event.levelState()
        );

        GhostRenderer.renderAll(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(false),
                event.levelState().cameraRenderState,
                Minecraft.getInstance().level,
                event.poseStack()
        );
    }

    public static void clientTick(Minecraft minecraft){
        CommonClientEvents.onClientTick();
    }
}
