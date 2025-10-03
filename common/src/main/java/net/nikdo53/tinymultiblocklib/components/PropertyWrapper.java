package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class PropertyWrapper<T extends Comparable<T>> {
    public final Property<T> property;
    public T value;

    public PropertyWrapper(Property<T> property) {
        this.property = property;
        this.value = null;
    }

    public PropertyWrapper(Property<T> property, T value) {
        this.property = property;
        this.value = value;
    }

    public static <T extends Comparable<T>> PropertyWrapper<?> addProperty(Property<T> property){
        return new PropertyWrapper<>(property);
    }

    public BlockState applyTo(BlockState state) {
        return state.trySetValue(property, value);
    }

    public void captureValue(BlockState state) {
        this.value = state.getValue(property);
    }

}