package net.nikdo53.tinymultiblocklib.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;

import java.util.function.Supplier;

public class TMBLBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final Supplier<BlockEntityType<TestMultiblockEntity>> TEST_BE = BLOCK_ENTITIES.register("test_be", () -> BlockEntityType.Builder.of(TestMultiblockEntity::new, TMBLBlocks.TEST_BLOCK.get(), TMBLBlocks.DIAMOND_STRUCTURE_BLOCK.get()).build(null));

}
