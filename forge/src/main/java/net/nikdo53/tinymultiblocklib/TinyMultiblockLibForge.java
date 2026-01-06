package net.nikdo53.tinymultiblocklib;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;

import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public TinyMultiblockLibForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        if (!FMLLoader.isProduction())
            TestRegistration.register(eventBus);

        eventBus.addListener(TinyMultiblockLibForge::registerAll);

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


    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties()));
    }

}