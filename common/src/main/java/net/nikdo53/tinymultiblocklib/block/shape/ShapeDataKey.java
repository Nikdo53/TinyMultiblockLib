package net.nikdo53.tinymultiblocklib.block.shape;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.UnaryOperator;

public record ShapeDataKey<T>(String name) {
    public static final ShapeDataKey<BlockState> BLOCK_STATE = new ShapeDataKey<>("block_state");
    public static final ShapeDataKey<VoxelShape> VOXEL_SHAPE = new ShapeDataKey<>("voxel_shape");
    public static final ShapeDataKey<Boolean> STANDALONE_VOXEL_SHAPE = new ShapeDataKey<>("standalone_voxel_shape");

    @Override
    public boolean equals(Object o) {
        if (o instanceof ShapeDataKey<?> key1) {
            return key1.name().equals(name);
        }
        return false;
    }

    public record Pair<T>(ShapeDataKey<T> key, T value) { }

    public record Operation<T>(ShapeDataKey<T> key, UnaryOperator<T> operation) { }
}
