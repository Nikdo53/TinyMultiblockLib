package net.nikdo53.tinymultiblocklib;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.platform.Services;
import net.nikdo53.tinymultiblocklib.platform.services.IRegistrationUtils;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.test.GhostBlockItem;
import net.nikdo53.tinymultiblocklib.test.SimpleMultiblock;
import net.nikdo53.tinymultiblocklib.test.TestBlock;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public interface CommonRegistration {
    IRegistrationUtils REGISTRATION = Services.PLATFORM.getRegistration();

    static void init(){
        BlockReg.init();
        BlockEntities.init();
        ItemReg.init();
    }

    interface BlockReg {
        Supplier<Block> TEST_BLOCK =
                REGISTRATION.registerBlockWithItem("test_block", TestBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT));

        Supplier<Block> DIAMOND_STRUCTURE_BLOCK =
                REGISTRATION.registerBlockWithItem("diamond_structure", DiamondStructureBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK));

        Supplier<Block> SIMPLE_MULTIBLOCK =
                REGISTRATION.registerBlockWithItem("simple_multiblock", SimpleMultiblock::new,() -> BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT));

        static void init(){

        }
    }

    interface ItemReg{
        Supplier<Item> GHOST_BLOCK_ITEM = REGISTRATION.registerItem("ghost_block_item", GhostBlockItem::new);

        static void init(){}
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
