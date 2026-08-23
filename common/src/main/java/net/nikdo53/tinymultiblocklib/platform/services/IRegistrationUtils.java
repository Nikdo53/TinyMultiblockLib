package net.nikdo53.tinymultiblocklib.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface IRegistrationUtils {
    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Set<Block> blocks);

    <T extends Block> Supplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> func, Supplier<BlockBehaviour.Properties> properties);

    default <T extends Block> Supplier<T> registerBlockWithItem(String name, Function<BlockBehaviour.Properties, ? extends T> func, Supplier<BlockBehaviour.Properties> properties){
        Supplier<T> block = registerBlock(name, func, properties);
        registerBlockItem(name, block);
        return block;
    }

    default <T extends Block> void registerBlockItem(String name, Supplier<T> block){
        registerItem(name, (props) -> new TestBlockItem(block.get(), props), UnaryOperator.identity());
    }

    <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> func, UnaryOperator<Item.Properties> properties);

    default <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> func){
       return registerItem(name, func, UnaryOperator.identity());
    }


    <T extends BlockEntity> void addSupportedBEBlock(Supplier<BlockEntityType<T>> blockEntityType, Block block);}
