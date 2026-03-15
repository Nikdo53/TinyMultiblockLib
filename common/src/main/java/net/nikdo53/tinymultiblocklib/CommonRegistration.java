package net.nikdo53.tinymultiblocklib;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.platform.Services;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.test.SimpleMultiBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlock;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public interface CommonRegistration {
    IRegistrationUtils REGISTRATION = Services.PLATFORM.getRegistration();

    static void init(){
        Blocks.init();
        BlockEntities.init();
    }

    interface Blocks{
        Supplier<Block> TEST_BLOCK =
                REGISTRATION.registerBlockWithItem("test_block", () -> new TestBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.DIRT)));

         Supplier<Block> DIAMOND_STRUCTURE_BLOCK =
                REGISTRATION.registerBlockWithItem("diamond_structure", () -> new DiamondStructureBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BLOCK)));

         Supplier<Block> SIMPLE_MULTIBLOCK =
                REGISTRATION.registerBlockWithItem("simple_multiblock", () -> new SimpleMultiBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.DIRT)));

        static void init(){

        }
    }

    interface BlockEntities{
         Set<Block> VALID_BLOCKS_SIMPLE = new HashSet<>();
         Set<Block> VALID_BLOCKS_STRUCTURE = new HashSet<>();

         Supplier<BlockEntityType<SimpleMultiBlockEntity>> SIMPLE_MULTIBLOCK_ENTITY =
                REGISTRATION.registerBlockEntity("simple_multiblock_entity", SimpleMultiBlockEntity::new, CommonRegistration.BlockEntities.VALID_BLOCKS_SIMPLE);

         Supplier<BlockEntityType<SimpleStructureMultiBlockEntity>> SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY =
                REGISTRATION.registerBlockEntity("simple_structure_multiblock_entity", SimpleStructureMultiBlockEntity::new, CommonRegistration.BlockEntities.VALID_BLOCKS_STRUCTURE);

        static void init(){

        }
    }
}
