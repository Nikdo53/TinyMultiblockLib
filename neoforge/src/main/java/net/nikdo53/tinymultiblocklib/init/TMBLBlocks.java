package net.nikdo53.tinymultiblocklib.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.TestBlock;

import java.util.function.Supplier;

public class TMBLBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final DeferredBlock<Block> TEST_BLOCK = registerBlockWithItem("test_block", () -> new TestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noCollission().randomTicks().noTerrainParticles()));

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block) {
        return TMBLItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties()));
    }

}
