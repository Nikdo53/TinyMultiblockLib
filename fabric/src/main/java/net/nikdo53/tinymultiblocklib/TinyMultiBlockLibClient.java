package net.nikdo53.tinymultiblocklib;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.nikdo53.tinymultiblocklib.client.TMBLClientEvents;
import net.nikdo53.tinymultiblocklib.config.TMBLClientConfig;

public class TinyMultiBlockLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);
        ConfigScreenFactoryRegistry.INSTANCE.register(Constants.MOD_ID, ConfigurationScreen::new);
        TMBLClientEvents.init();
    }
}
