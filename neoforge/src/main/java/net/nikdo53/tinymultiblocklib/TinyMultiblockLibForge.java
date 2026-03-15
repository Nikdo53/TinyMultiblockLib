package net.nikdo53.tinymultiblocklib;

import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;

import static net.nikdo53.tinymultiblocklib.platform.NeoForgeRegistration.*;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    public TinyMultiblockLibForge(IEventBus eventBus, Dist dist, ModContainer container) {
        if(dist.isClient()) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        container.registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        if (!FMLLoader.isProduction()) ForgeEvents.register(eventBus);

        BLOCK_ENTITIES.register(eventBus);
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        CommonRegistration.init();

        eventBus.addListener(BlockEntityTypeAddBlocksEvent.class, TinyMultiblockLibForge::addBEBlocks);

        CommonClass.init();
    }

    public static void addBEBlocks(BlockEntityTypeAddBlocksEvent event){
        event.modify(CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY.get(), CommonRegistration.BlockEntities.VALID_BLOCKS_SIMPLE.toArray(new Block[0]));
        event.modify(CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY.get(), CommonRegistration.BlockEntities.VALID_BLOCKS_STRUCTURE.toArray(new Block[0]));
    }

}