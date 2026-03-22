package net.nikdo53.tinymultiblocklib.client;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.nikdo53.tinymultiblocklib.platform.FabricPlatformHelper;

public class TMBLClientEvents {
    public static void init() {
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(TMBLClientEvents::renderLevelStageEvent);
    }

    private static void renderLevelStageEvent(WorldRenderContext event) {
        MultiblockPreviewRenderer.renderMultiblockPreviews(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(true),
                Minecraft.getInstance(),
                Minecraft.getInstance().level,
                event.worldState().cameraRenderState,
                event.matrices(),
                event.worldRenderer(),
                event.worldState()
        );
    }
}
