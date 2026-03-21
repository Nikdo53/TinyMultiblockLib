package net.nikdo53.tinymultiblocklib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.block.AbstractStructureMultiBlock;
import net.nikdo53.tinymultiblocklib.components.BlockPatternUtils;
import net.nikdo53.tinymultiblocklib.components.Corner;
import net.nikdo53.tinymultiblocklib.test.DiamondStructureBlock;

public class CommonEvents {

    /**
     * Made as an example implementation of the diamond structure block, only runs in dev env!
     * <p>
     * Places a Diamond structure when right-clicking on a 2x2x2 cube of diamond blocks
     */
    public static void testRightClickBlock(Level level, BlockPos pos, Player player) {
        if (!level.getBlockState(pos).is(Blocks.DIAMOND_BLOCK)) return;

        BlockPatternUtils.findAndPlace(
                DiamondStructureBlock.getBlockPattern(),
                level,
                pos,
                CommonRegistration.BlockReg.DIAMOND_STRUCTURE_BLOCK.get().defaultBlockState(),
                BlockPos.ZERO,
                Corner.FORWARD_LOWER_LEFT);
    }

    public static void onVanillaEvent(ServerLevel level, Holder<GameEvent> gameEventHolder, Vec3 pos, GameEvent.Context context){
        if (gameEventHolder.is(GameEvent.BLOCK_DESTROY)){
            BlockState state = context.affectedState();
            if (state != null && state.getBlock() instanceof AbstractStructureMultiBlock) {
                AbstractStructureMultiBlock.onRemove(state, level, BlockPos.containing(pos.x(), pos.y(), pos.z()), false);
            }
        }

    }
}
