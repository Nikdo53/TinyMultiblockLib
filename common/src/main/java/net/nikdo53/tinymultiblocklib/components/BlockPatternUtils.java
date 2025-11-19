package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.nikdo53.tinymultiblocklib.Constants;

public class BlockPatternUtils {

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
