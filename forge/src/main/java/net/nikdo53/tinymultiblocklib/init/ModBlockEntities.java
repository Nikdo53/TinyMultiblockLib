package net.nikdo53.tinymultiblocklib.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.entity.TestMultiblockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<TestMultiblockEntity>> TEST_BE = BLOCK_ENTITIES.register("test_be", () -> BlockEntityType.Builder.of(TestMultiblockEntity::new, ModBlocks.TEST.get()).build(null));

}
