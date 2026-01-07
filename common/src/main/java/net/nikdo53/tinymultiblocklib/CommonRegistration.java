package net.nikdo53.tinymultiblocklib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.blockentities.SimpleStructureMultiBlockEntity;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.test.SimpleMultiBlock;
import net.nikdo53.tinymultiblocklib.test.TestBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CommonRegistration {
    public static class Blocks{
        public static final Set<UnregisteredBlock> BLOCKS = new HashSet<>();

        public static Supplier<Block> TEST_BLOCK = register("test_block", () -> new TestBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BLOCK)));
        public static Supplier<Block> DIAMOND_STRUCTURE_BLOCK = register("diamond_structure", () -> new DiamondStructureBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIRT)));
        public static Supplier<Block> SIMPLE_MULTIBLOCK = register("simple_multiblock", () -> new SimpleMultiBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIRT)));

        public record UnregisteredBlock(String name, Supplier<Block> block) {}

        private static Supplier<Block> register(String name, Supplier<Block> block){
          //  BLOCKS.add(new UnregisteredBlock(name, block));

            return block;
        }
    }

    public static class BlockEntities{
        public static final Set<UnregisteredBlockEntity<?>> BLOCK_ENTITIES = new HashSet<>();

        public static Supplier<BlockEntityType<SimpleMultiBlockEntity>> SIMPLE_MULTIBLOCK_ENTITY = register("simple_multiblock_entity", SimpleMultiBlockEntity::new);
        public static Supplier<BlockEntityType<SimpleStructureMultiBlockEntity>> SIMPLE_STRUCTURE_MULTIBLOCK_ENTITY = register("simple_structure_multiblock_entity", SimpleStructureMultiBlockEntity::new);



        public record UnregisteredBlockEntity<T extends BlockEntity>(String name, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {}

        private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name, BiFunction<BlockPos, BlockState, T> function, Block... blocks){
         //   BLOCK_ENTITIES.add(new UnregisteredBlockEntity<>(name, function, blocks));

            return () -> null;
        }

    }
}
