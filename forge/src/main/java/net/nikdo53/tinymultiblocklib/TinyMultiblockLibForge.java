package net.nikdo53.tinymultiblocklib;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    
    public TinyMultiblockLibForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        if (!FMLLoader.isProduction()) TestRegistration.register(modEventBus);
        CommonClass.init();
    }
}