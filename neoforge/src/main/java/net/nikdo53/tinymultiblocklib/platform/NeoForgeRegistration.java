package net.nikdo53.tinymultiblocklib.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeRegistration implements IRegistrationUtils {
    public static final NeoForgeRegistration INSTANCE = new NeoForgeRegistration();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Set<Block> blocks) {
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(function::apply, Set.of()));
    }

    @Override
    public <T extends Block> Supplier<T> registerBlockWithItem(String name, Function<BlockBehaviour.Properties, ? extends T> func, Supplier<BlockBehaviour.Properties> properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, func, properties);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    @Override
    public <T extends Block> Supplier<Item> registerBlockItem(String name, Supplier<T> block) {
        return ITEMS.registerItem(name, (props) -> new TestBlockItem(block.get(), props));
    }

    @Override
    public  <T extends BlockEntity> void addSupportedBEBlock(Supplier<BlockEntityType<T>> blockEntityType, Block block){
        // this is only needed on fabric :/
    }
}
