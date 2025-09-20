package net.nikdo53.tinymultiblocklib;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nikdo53.tinymultiblocklib.client.ClientConfig;
import net.nikdo53.tinymultiblocklib.init.ModBlockEntities;
import net.nikdo53.tinymultiblocklib.init.ModBlocks;
import net.nikdo53.tinymultiblocklib.init.ModItems;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    
    public TinyMultiblockLibForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        CommonClass.init();
    }
}