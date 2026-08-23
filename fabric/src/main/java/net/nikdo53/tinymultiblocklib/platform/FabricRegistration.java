package net.nikdo53.tinymultiblocklib.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.mixin.BlockEntityTypeAccessor;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FabricRegistration implements IRegistrationUtils {
    public static final FabricRegistration INSTANCE = new FabricRegistration();

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Set<Block> blocks) {
        BlockEntityType<T> registered = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, BlockEntityType.Builder.of(function::apply, blocks.toArray(new Block[0])).build(null));
        return () -> registered;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> func, Supplier<BlockBehaviour.Properties> properties) {
        T register = Registry.register(BuiltInRegistries.BLOCK, Constants.loc(name), func.apply(properties.get()));
        return () -> register;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> func, UnaryOperator<Item.Properties> properties) {
        Item.Properties props = new Item.Properties();
        props = properties.apply(props);
        T register = Registry.register(BuiltInRegistries.ITEM, Constants.loc(name), func.apply(props));
        return () -> register;
    }

    @Override
    public  <T extends BlockEntity> void addSupportedBEBlock(Supplier<BlockEntityType<T>> blockEntityType, Block block){
        BlockEntityTypeAccessor accessor = (BlockEntityTypeAccessor) blockEntityType.get();
        Set<Block> blocks = new HashSet<>(accessor.tinymultiblocklib$getValidBlocks());
        blocks.add(block);

        accessor.tinymultiblocklib$setValidBlocks(blocks);
    }}
