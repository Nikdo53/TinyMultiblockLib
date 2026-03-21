package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.platform.NeoForgePlatformHelper;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class TMBLClientEvents {

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent.AfterTranslucentBlocks event){
        if (!TMBLClientConfig.DISABLE_MULTIBLOCK_PREVIEWS.get()){
            MultiblockPreviewRenderer.renderMultiblockPreviews(
                    DeltaTracker.ONE.getGameTimeDeltaPartialTick(true),
                    Minecraft.getInstance(),
                    Minecraft.getInstance().level,
                    event.getLevelRenderState().cameraRenderState,
                    event.getPoseStack(),
                    event.getLevelRenderer(),
                    event.getLevelRenderState()
            );
        }
    }

}
