package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.Direction;

/**
 * Contains all corners of a cube
 */
public enum Corner {
    FORWARD_LOWER_LEFT(Direction.WEST, Direction.DOWN, Direction.SOUTH),
    FORWARD_LOWER_RIGHT(Direction.EAST, Direction.DOWN, Direction.SOUTH),
    FORWARD_UPPER_LEFT(Direction.WEST, Direction.UP, Direction.SOUTH),
    FORWARD_UPPER_RIGHT(Direction.EAST, Direction.UP, Direction.SOUTH),

    BACK_LOWER_LEFT(Direction.WEST, Direction.DOWN, Direction.NORTH),
    BACK_LOWER_RIGHT(Direction.EAST, Direction.DOWN, Direction.NORTH),
    BACK_UPPER_LEFT(Direction.WEST, Direction.UP, Direction.NORTH),
    BACK_UPPER_RIGHT(Direction.EAST, Direction.UP, Direction.NORTH);

    public final Direction x;
    public final Direction y;
    public final Direction z;

    Corner(Direction xDirection, Direction yDirection, Direction zDirection){
        this.x = xDirection;
        this.y = yDirection;
        this.z = zDirection;
    }

}
