package net.nikdo53.tinymultiblocklib;


import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
import net.nikdo53.tinymultiblocklib.mixin.BlockEntityTypeAccessor;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.test.SimpleMultiBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.function.BiFunction;
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
            ForgeEvents.register(eventBus);

        registerAll();

        BLOCK_ENTITIES.register(eventBus);
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);


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

    public static void registerAll(){
        CommonRegistration.Blocks.DIAMOND_STRUCTURE_BLOCK = registerBlockWithItem("diamond_structure", () -> new DiamondStructureBlock(BlockBehaviour.Properties.copy(Blocks.DIRT)));
        CommonRegistration.Blocks.TEST_BLOCK = registerBlockWithItem("test_block", () -> new TestBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
        CommonRegistration.Blocks.SIMPLE_MULTIBLOCK = registerBlockWithItem("simple_multiblock", () -> new SimpleMultiBlock(BlockBehaviour.Properties.copy(Blocks.DIRT)));

        CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY = registerBlockEntity("simple_multiblock_entity", SimpleMultiBlockEntity::new, CommonRegistration.BlockEntities.VALID_BLOCKS_SIMPLE.toArray(new Block[0]));
        CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY = registerBlockEntity("simple_structure_multiblock_entity", SimpleStructureMultiBlockEntity::new, CommonRegistration.BlockEntities.VALID_BLOCKS_STRUCTURE.toArray(new Block[0]));

    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(function::apply, blocks).build(null));
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block) {
        return ITEMS.register(name, () -> new TestBlockItem(block.get(), new Item.Properties()));
    }

}