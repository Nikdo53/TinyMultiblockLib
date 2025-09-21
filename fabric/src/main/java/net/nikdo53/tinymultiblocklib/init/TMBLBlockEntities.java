package net.nikdo53.tinymultiblocklib.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.entity.TestBlockEntity;

public class TMBLBlockEntities {
    public static final BlockEntityType<TestBlockEntity> TEST_BE =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.loc("test_be"),
                    FabricBlockEntityTypeBuilder.create(TestBlockEntity::new,
                            TMBLBlocks.TEST_BLOCK).build());

    public static void register(){
    }

}
