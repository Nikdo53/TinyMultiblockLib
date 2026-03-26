package net.nikdo53.tinymultiblocklib.platform;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricRegistration implements IRegistrationUtils {
    public static final FabricRegistration INSTANCE = new FabricRegistration();

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Set<Block> blocks) {
        BlockEntityType<T> registered = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, FabricBlockEntityTypeBuilder.create(function::apply).build());
        return () -> registered;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlockWithItem(String name, Function<BlockBehaviour.Properties, ? extends T> func, Supplier<BlockBehaviour.Properties> properties) {
        T register = Registry.register(BuiltInRegistries.BLOCK, Constants.loc(name), func.apply(properties.get().setId(blockId(name))));
        registerBlockItem(name, () -> register);
        return () -> register;
    }

    @Override
    public <T extends Block> Supplier<Item> registerBlockItem(String name, Supplier<T> block) {
        TestBlockItem reg = Registry.register(BuiltInRegistries.ITEM, Constants.loc(name), new TestBlockItem(block.get(), new Item.Properties().setId(itemId(name))));
        return () -> reg;
    }

    @Override
    public  <T extends BlockEntity> void addSupportedBEBlock(Supplier<BlockEntityType<T>> blockEntityType, Block block){
        blockEntityType.get().addValidBlock(block);
    }

    private static ResourceKey<Block> blockId(String name) {
        return ResourceKey.create(Registries.BLOCK, Constants.loc(name));
    }

    private static ResourceKey<Item> itemId(String name) {
        return ResourceKey.create(Registries.ITEM, Constants.loc(name));
    }


}
