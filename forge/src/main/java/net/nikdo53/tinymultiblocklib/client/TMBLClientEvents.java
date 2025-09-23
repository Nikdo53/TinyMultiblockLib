package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.platform.ForgePlatformHelper;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TMBLClientEvents {

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event){
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) && !TMBLClientConfig.DISABLE_MULTIBLOCK_PREVIEWS.get()){
            MultiblockPreviewRenderer.renderMultiblockPreviews(event.getPartialTick(), Minecraft.getInstance(), Minecraft.getInstance().level, event.getCamera(), event.getPoseStack(), new ForgePlatformHelper());
        }
    }

}
