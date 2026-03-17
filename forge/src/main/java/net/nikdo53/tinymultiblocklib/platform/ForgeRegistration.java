package net.nikdo53.tinymultiblocklib.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.test.TestBlockItem;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ForgeRegistration implements IRegistrationUtils {
    public static final ForgeRegistration INSTANCE = new ForgeRegistration();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BiFunction<BlockPos, BlockState, T> function, Set<Block> blocks) {
        Block[] array = blocks.toArray(new Block[0]);
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(function::apply, array).build(null));
    }

    @Override
    public <T extends Block> Supplier<T> registerBlockWithItem(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;

    }

    @Override
    public <T extends Block> Supplier<Item> registerBlockItem(String name, Supplier<T> block) {
        return ITEMS.register(name, () -> new TestBlockItem(block.get(), new Item.Properties()));
    }

    @Override
    public  <T extends BlockEntity> void addSupportedBEBlock(Supplier<BlockEntityType<T>> blockEntityType, Block block){
        // this is only needed on fabric :/
    }
}
