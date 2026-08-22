package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.client.ghost.GhostRenderer;
import net.nikdo53.tinymultiblocklib.config.TMBLClientConfig;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class TMBLClientEvents {

    @SubscribeEvent
    public static void renderLevelStage(SubmitCustomGeometryEvent event){
        MultiblockPreviewRenderer.tryRenderMultiblockPreviews(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(true),
                event.getLevelRenderState().cameraRenderState,
                new PoseStack(),
                event.getSubmitNodeCollector()
        );

        GhostRenderer.renderAll(
                DeltaTracker.ONE.getGameTimeDeltaPartialTick(true),
                event.getLevelRenderState().cameraRenderState,
                new PoseStack(),
                event.getSubmitNodeCollector()
        );
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event){
        CommonClientEvents.onClientTick();
    }


}
