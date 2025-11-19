package net.nikdo53.tinymultiblocklib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.DiamondStructureBlock;
import net.nikdo53.tinymultiblocklib.components.BlockPatternUtils;
import net.nikdo53.tinymultiblocklib.init.TMBLBlockEntities;
import net.nikdo53.tinymultiblocklib.init.TMBLBlocks;
import net.nikdo53.tinymultiblocklib.init.TMBLItems;

public class TestRegistration {
    public static void register(IEventBus modEventBus) {
        TMBLBlocks.BLOCKS.register(modEventBus);
        TMBLItems.ITEMS.register(modEventBus);
        TMBLBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(TestRegistration::testRightClickEvent);
    }

    public static void testRightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(Blocks.DIAMOND_BLOCK)) return;

        BlockPattern blockPattern = DiamondStructureBlock.getBlockPattern();
        BlockPattern.BlockPatternMatch blockPatternMatch = blockPattern.find(level, pos);
        if (blockPatternMatch != null) {
            BlockPos center = BlockPatternUtils.getCorner(blockPatternMatch, Direction.WEST, Direction.DOWN, Direction.SOUTH);
            level.setBlock(center, TMBLBlocks.DIAMOND_STRUCTURE_BLOCK.get().defaultBlockState().setValue(AbstractMultiBlock.CENTER, true), 3);
        }
    }
}
