package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.world.level.block.state.properties.Property;
import net.nikdo53.tinymultiblocklib.Constants;

import java.util.ArrayList;
import java.util.List;

public class SyncedStatePropertiesBuilder {
    private final List<PropertyWrapper<?>> PROPERTIES = new ArrayList<>();
    private boolean isInitialized = false;

    public <T extends Comparable<T>> void add(Property<T> property){
        if (isInitialized) {
            Constants.LOGGER.error("Tried to add a new synced BlockStateProperty, but SyncedStatePropertiesBuilder was already initialized");
            return;
        }

        this.PROPERTIES.add(PropertyWrapper.addProperty(property));
    }

    public List<PropertyWrapper<?>> getProperties(){
        return PROPERTIES;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(){
        this.isInitialized = true;
    }
}
