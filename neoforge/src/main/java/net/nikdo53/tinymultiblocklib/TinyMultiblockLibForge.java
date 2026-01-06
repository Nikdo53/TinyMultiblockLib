package net.nikdo53.tinymultiblocklib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;

import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public TinyMultiblockLibForge(IEventBus eventBus, Dist dist, ModContainer container) {
        if(dist.isClient()) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        container.registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        if (!FMLLoader.isProduction())
            TestRegistration.register(eventBus);

        eventBus.addListener(RegisterEvent.class, TinyMultiblockLibForge::registerAll);

        BLOCK_ENTITIES.register(eventBus);
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);


        CommonClass.init();
    }

    private static void registerAll(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.BLOCK)) return;
        
        for (CommonRegistration.Blocks.UnregisteredBlock block : CommonRegistration.Blocks.BLOCKS) {
            registerBlockItem(block.name(), block.block());
        }

        for (CommonRegistration.BlockEntities.UnregisteredBlockEntity<?> blockEntity : CommonRegistration.BlockEntities.BLOCK_ENTITIES) {
            BLOCK_ENTITIES.register(blockEntity.name(), () -> BlockEntityType.Builder.of(blockEntity.function()::apply, blockEntity.blocks()).build(null));
        }
    }


    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, Supplier<T> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties()));
    }

}