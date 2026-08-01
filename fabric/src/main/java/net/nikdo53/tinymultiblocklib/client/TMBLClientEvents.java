package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.nikdo53.tinymultiblocklib.client.ghost.GhostRenderer;
import net.nikdo53.tinymultiblocklib.platform.FabricPlatformHelper;

public class TMBLClientEvents {
    public static void init() {
        LevelExtractionEvents.END_EXTRACTION.register(TMBLClientEvents::renderLevelStageEvent);
        ClientTickEvents.END_CLIENT_TICK.register(TMBLClientEvents::clientTick);
    }

    private static void renderLevelStageEvent(LevelExtractionContext event) {
        MultiblockPreviewRenderer.renderMultiblockPreviews(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(true),
                Minecraft.getInstance(),
                Minecraft.getInstance().level,
                event.levelState().cameraRenderState,
                new PoseStack(),
                event.levelRenderer().submitNodeStorage
        );

        GhostRenderer.renderAll(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(false),
                event.levelState().cameraRenderState,
                Minecraft.getInstance().level,
                new PoseStack(),
                event.levelRenderer().submitNodeStorage
        );
    }

    public static void clientTick(Minecraft minecraft){
        CommonClientEvents.onClientTick();
    }
}
