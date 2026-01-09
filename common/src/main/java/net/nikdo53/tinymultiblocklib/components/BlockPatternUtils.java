package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.nikdo53.tinymultiblocklib.CommonRegistration;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import org.jetbrains.annotations.Nullable;

public class BlockPatternUtils {

    public static boolean findAndPlace(BlockPattern blockPattern, Level level, BlockPos pos, BlockState stateToPlace, BlockPos placementOffset, Corner corner){
        BlockPattern.BlockPatternMatch blockPatternMatch = blockPattern.find(level, pos);
        if (blockPatternMatch == null) return false;

       // Direction.WEST, Direction.DOWN, Direction.SOUTH
        BlockPos center = BlockPatternUtils.getCorner(blockPatternMatch, corner);
        level.setBlock(center.offset(placementOffset), stateToPlace.trySetValue(AbstractMultiBlock.CENTER, true), 3);

        return true;
    }

    public static BlockPos getBottomNorthWest(BlockPattern.BlockPatternMatch match) {
        int width = match.getWidth();
        int height = match.getHeight();
        int depth = match.getDepth();

        BlockPos result = null;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {

                    BlockInWorld biw = match.getBlock(x, y, z);
                    BlockPos worldPos = biw.getPos();

                    if (result == null){
                        result =  worldPos;
                        continue;
                    }

                    if (worldPos.getX() < result.getX() || worldPos.getY() < result.getY() || worldPos.getZ() < result.getZ()) {
                        result = worldPos;
                    }
                }
            }
        }

        return result;
    }

    public static BlockPos getCorner(BlockPattern.BlockPatternMatch match, Corner corner){
        return getCorner(match, corner.x, corner.y, corner.z);
    }

    public static BlockPos getCorner(BlockPattern.BlockPatternMatch match, Direction xAxis, Direction yAxis, Direction zAxis) {
        BlockPos result = getBottomNorthWest(match);
        int width = match.getWidth();
        int height = match.getHeight();
        int depth = match.getDepth();

        if (xAxis.getAxis() != Direction.Axis.X || yAxis.getAxis() != Direction.Axis.Y || zAxis.getAxis() != Direction.Axis.Z) {
            Constants.LOGGER.error("Tried getting BlockPattern corner for an invalid direction");
            return result;
        }

        if (xAxis == Direction.EAST) result = result.east(width - 1);
        if (yAxis == Direction.UP)  result = result.above(height - 1);
        if (zAxis == Direction.SOUTH)  result = result.south(depth - 1);

        return result;
    }




}
