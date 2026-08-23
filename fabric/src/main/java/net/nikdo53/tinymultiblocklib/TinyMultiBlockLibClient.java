package net.nikdo53.tinymultiblocklib;


import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.fml.config.ModConfig;
import net.nikdo53.tinymultiblocklib.client.TMBLClientEvents;
import net.nikdo53.tinymultiblocklib.config.TMBLClientConfig;

public class TinyMultiBlockLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);
        TMBLClientEvents.init();
    }
}
