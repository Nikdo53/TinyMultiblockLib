package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public interface IBlockPosOffsetEnum {
    /**
     * A cleaner way to signify an offset. Example:
     *
     * <pre>
     * ENUM_VALUE(BlockPos::north)
     * ENUM_VALUE2(pos -> pos.above().north())
     * </pre>
     *
     * */
    Function<BlockPos, BlockPos> getOffsetFunction();

    default BlockPos getOffset(){
        return getOffsetFunction().apply(BlockPos.ZERO);
    };

    /**
     * Returns an enum value of type E based on its BlockPos offset
     * @param enumClass The .class of the enum this is implemented on
     * @param fallback Default value for when the offset doesn't match any other value
     * @param <E> Your enum.Class, probably used in a BlockState
     * */
    static <E extends Enum<E> & IBlockPosOffsetEnum> E fromOffset(Class<E> enumClass, BlockPos offset, @Nullable Direction direction, E fallback) {
        for (E part : enumClass.getEnumConstants()) {
            BlockPos testOffset = part.getOffset();
            if (direction != null) {
                testOffset = testOffset.rotate(rotationFromDirection(direction));
            }
            if (testOffset.equals(offset)) {
                return part;
            }
        }
        return fallback;
    }

    /**
     * Rotates a BlockPos
     * */
    static Rotation rotationFromDirection(Direction direction){
        return switch (direction){
            case DOWN, NORTH, UP -> Rotation.NONE;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            case EAST -> Rotation.CLOCKWISE_90;
        };
    }
}
