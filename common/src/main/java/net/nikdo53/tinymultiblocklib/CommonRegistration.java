package net.nikdo53.tinymultiblocklib;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class CommonRegistration {
    public static class Blocks{
        public static Supplier<Block> TEST_BLOCK;
        public static Supplier<Block> DIAMOND_STRUCTURE_BLOCK;
        public static Supplier<Block> SIMPLE_MULTIBLOCK;
    }

    public static class BlockEntities{
        public static final Set<Block> VALID_BLOCKS_SIMPLE = new HashSet<>();
        public static final Set<Block> VALID_BLOCKS_STRUCTURE = new HashSet<>();

        public static Supplier<BlockEntityType<SimpleMultiBlockEntity>> SIMPLE_MULTIBLOCK_ENTITY;
        public static Supplier<BlockEntityType<SimpleStructureMultiBlockEntity>> SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY;

    }
}
