package net.nikdo53.tinymultiblocklib.client;

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
    public static void renderLevelStage(RenderLevelStageEvent event){
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) && !TMBLClientConfig.DISABLE_MULTIBLOCK_PREVIEWS.get()){
            MultiblockPreviewRenderer.renderMultiblockPreviews(event.getPartialTick().getGameTimeDeltaPartialTick(true), Minecraft.getInstance(), Minecraft.getInstance().level, event.getCamera(), event.getPoseStack(), new NeoForgePlatformHelper());
        }
    }

}
