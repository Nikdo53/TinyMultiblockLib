package net.nikdo53.tinymultiblocklib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.tinymultiblocklib.block.entity.SimpleMultiblockEntity;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;
import net.nikdo53.tinymultiblocklib.init.TMBLBlockEntities;
import net.nikdo53.tinymultiblocklib.init.TMBLBlocks;

import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    
    public TinyMultiblockLibForge(IEventBus eventBus, Dist dist, ModContainer container) {
        if(dist.isClient()) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        container.registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        if (!FMLLoader.isProduction())
            TestRegistration.register(eventBus);

        BLOCK_ENTITIES.register(eventBus);

        CommonClass.init();
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final Supplier<BlockEntityType<SimpleMultiblockEntity>> SIMPLE_MULTIBLOCK_ENTITY = //allowed blocks get added later
            BLOCK_ENTITIES.register("simple_multiblock_entity", () -> BlockEntityType.Builder.of(SimpleMultiblockEntity::new).build(null));

}