package net.nikdo53.tinymultiblocklib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.client.TMBLClientConfig;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.test.SimpleMultiBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.function.BiFunction;
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
            ForgeEvents.register(eventBus);

        registerAll();

        BLOCK_ENTITIES.register(eventBus);
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);

        eventBus.addListener(BlockEntityTypeAddBlocksEvent.class, TinyMultiblockLibForge::addBEBlocks);


        CommonClass.init();
    }

    public static void addBEBlocks(BlockEntityTypeAddBlocksEvent event){
        event.modify(CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY.get(), CommonRegistration.BlockEntities.VALID_BLOCKS_SIMPLE.toArray(new Block[0]));
        event.modify(CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY.get(), CommonRegistration.BlockEntities.VALID_BLOCKS_STRUCTURE.toArray(new Block[0]));
    }

    public static void registerAll(){
        CommonRegistration.Blocks.DIAMOND_STRUCTURE_BLOCK = registerBlockWithItem("diamond_structure", () -> new DiamondStructureBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIRT)));
        CommonRegistration.Blocks.TEST_BLOCK = registerBlockWithItem("test_block", () -> new TestBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BLOCK)));
        CommonRegistration.Blocks.SIMPLE_MULTIBLOCK = registerBlockWithItem("simple_multiblock", () -> new SimpleMultiBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIRT)));

        CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY = registerBlockEntity("simple_multiblock_entity", SimpleMultiBlockEntity::new, CommonRegistration.BlockEntities.VALID_BLOCKS_SIMPLE.toArray(new Block[0]));
        CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY = registerBlockEntity("simple_structure_multiblock_entity", SimpleStructureMultiBlockEntity::new, CommonRegistration.BlockEntities.VALID_BLOCKS_STRUCTURE.toArray(new Block[0]));

    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(function::apply, blocks).build(null));
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, Supplier<T> block) {
        return ITEMS.register(name, () -> new TestBlockItem(block.get(), new Item.Properties()));
    }

}