package net.nikdo53.tinymultiblocklib;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.test.SimpleMultiBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlock;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TinyMultiblockLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();

        registerAll();
    }

    public static void registerAll(){
        CommonRegistration.Blocks.DIAMOND_STRUCTURE_BLOCK = registerBlockWithItem("diamond_structure", () -> new DiamondStructureBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIRT)));
        CommonRegistration.Blocks.TEST_BLOCK = registerBlockWithItem("test_block", () -> new TestBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BLOCK)));
        CommonRegistration.Blocks.SIMPLE_MULTIBLOCK = registerBlockWithItem("simple_multiblock", () -> new SimpleMultiBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIRT)));

        CommonRegistration.BlockEntities.SIMPLE_MULTIBLOCK_ENTITY = registerBlockEntity("simple_multiblock_entity", SimpleMultiBlockEntity::new, CommonRegistration.Blocks.TEST_BLOCK.get());
        CommonRegistration.BlockEntities.SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY = registerBlockEntity("simple_structure_multiblock_entity", SimpleStructureMultiBlockEntity::new, CommonRegistration.Blocks.SIMPLE_MULTIBLOCK.get());

    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
        BlockEntityType<T> registered = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, BlockEntityType.Builder.of(function::apply, blocks).build(null));
        return () -> registered;
    }

    private static Supplier<Block> registerBlockWithItem(String name, Supplier<Block> block) {
        Block block1 = block.get();
        registerBlockItem(name, block1);
        Block registered = Registry.register(BuiltInRegistries.BLOCK, Constants.loc(name), block1);
        return () -> registered;
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(BuiltInRegistries.ITEM, Constants.loc(name), new BlockItem(block, new Item.Properties()));
    }

}
