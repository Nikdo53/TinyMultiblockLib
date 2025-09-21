package net.nikdo53.tinymultiblocklib;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;
import net.nikdo53.tinymultiblocklib.init.TMBLBlockEntities;
import net.nikdo53.tinymultiblocklib.init.TMBLBlocks;
import net.nikdo53.tinymultiblocklib.init.TMBLItems;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    
    public TinyMultiblockLibForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        TMBLBlocks.BLOCKS.register(modEventBus);
        TMBLItems.ITEMS.register(modEventBus);
        TMBLBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        CommonClass.init();
    }
}