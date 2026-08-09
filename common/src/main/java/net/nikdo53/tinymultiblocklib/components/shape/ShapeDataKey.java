package net.nikdo53.tinymultiblocklib.components.shape;

import net.minecraft.world.level.block.state.BlockState;

public record ShapeDataKey<T>(String key) {
    public static final ShapeDataKey<BlockState> BLOCK_STATE = new ShapeDataKey<>("block_state");

    public record Pair<T>(ShapeDataKey<T> key, T value) { }
}
