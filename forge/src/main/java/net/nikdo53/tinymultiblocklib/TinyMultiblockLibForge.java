package net.nikdo53.tinymultiblocklib;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
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
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;

import static net.nikdo53.tinymultiblocklib.platform.NeoForgeRegistration.*;

@Mod(Constants.MOD_ID)
public class TinyMultiblockLibForge {
    public TinyMultiblockLibForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TMBLClientConfig.CLIENT_CONFIG);

        if (!FMLLoader.isProduction()) ForgeEvents.register(eventBus);

        BLOCK_ENTITIES.register(eventBus);
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        CommonRegistration.init();


        eventBus.addListener(TinyMultiblockLibForge::addBEBlocks);

        CommonClass.init();
    }

    // A weird replacement for the neo event, even though its unrelated, it's posted at the same time
    public static void addBEBlocks(SpawnPlacementRegisterEvent event){
        ((BlockEntityTypeAccessor) CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY.get())
                .tinymultiblocklib$setValidBlocks(CommonRegistration.BlockEntities.VALID_BLOCKS_SIMPLE);

        ((BlockEntityTypeAccessor) CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY.get())
                .tinymultiblocklib$setValidBlocks(CommonRegistration.BlockEntities.VALID_BLOCKS_STRUCTURE);
    }

}