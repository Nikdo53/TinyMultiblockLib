package net.nikdo53.tinymultiblocklib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    
    public TinyMultiblockLibForge(IEventBus eventBus, Dist dist, ModContainer container) {
        if(dist.isClient()) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        container.registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        if (!FMLLoader.isProduction()) TestRegistration.register(eventBus);
        CommonClass.init();
    }
}