package net.nikdo53.tinymultiblocklib.block.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import org.jetbrains.annotations.Nullable;

public interface MultiblockBehaviour {
    /**
     * Extra requirements for the block to survive or be placed, runs for every single block in the multiblock
     * */
    default boolean extraSurviveRequirements(LevelReader level, BlockPos pos, BlockState state, BlockPos centerOffset, MultiblockShape shape){
        return true;
    }

    default boolean entityUnobstructed(CollisionGetter level, BlockPos pos, BlockState state, @Nullable Entity player, MultiblockShape shape) {
        CollisionContext context = player == null ? CollisionContext.empty() : CollisionContext.of(player);

        return level.isUnobstructed(state, pos, context);
    }

    /**
     * Returns true if multiblock can replace this original block, runs for the whole multiblock shape
     * */
    default boolean canReplaceBlock(LevelReader level, BlockPos blockPos, BlockState state, MultiblockShape shape) {
        return state.canBeReplaced();
    }

}
