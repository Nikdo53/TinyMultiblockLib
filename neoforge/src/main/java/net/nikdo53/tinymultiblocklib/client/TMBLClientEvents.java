package net.nikdo53.tinymultiblocklib.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.client.ghost.GhostRenderer;
import net.nikdo53.tinymultiblocklib.platform.NeoForgePlatformHelper;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class TMBLClientEvents {

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event){
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        if (!TMBLClientConfig.DISABLE_MULTIBLOCK_PREVIEWS.get()){
            MultiblockPreviewRenderer.tryRenderMultiblockPreviews(
                    event.getPartialTick(),
                    event.getCamera(),
                    event.getPoseStack()
            );
        }

        GhostRenderer.renderAll(
                event.getPartialTick(),
                event.getCamera(),
                event.getPoseStack()
        );
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
       if (event.phase == TickEvent.Phase.END) CommonClientEvents.onClientTick();
    }


}
