package net.nikdo53.tinymultiblocklib.platform.services;

import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IRegistrationUtils {
    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Set<Block> blocks);

    <T extends Block> Supplier<T> registerBlockWithItem(String name, Supplier<T> block) ;

    <T extends Block> Supplier<Item> registerBlockItem(String name, Supplier<T> block);

    <T extends BlockEntity> void addSupportedBEBlock(Supplier<BlockEntityType<T>> blockEntityType, Block block);
}
