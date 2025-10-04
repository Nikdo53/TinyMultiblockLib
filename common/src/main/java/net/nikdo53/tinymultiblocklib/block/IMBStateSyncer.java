package net.nikdo53.tinymultiblocklib.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.nikdo53.tinymultiblocklib.components.PropertyWrapper;
import net.nikdo53.tinymultiblocklib.components.SyncedStatePropertiesBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.nikdo53.tinymultiblocklib.block.IMultiBlock.*;

public interface IMBStateSyncer {

    SyncedStatePropertiesBuilder getSyncedStatePropertiesBuilder();

    default List<PropertyWrapper<?>> getSyncedStateProperties(){
        SyncedStatePropertiesBuilder syncedStatePropertiesBuilder = getSyncedStatePropertiesBuilder();

        if (!syncedStatePropertiesBuilder.isInitialized()){
            createSyncedBlockStates(syncedStatePropertiesBuilder);

            syncedStatePropertiesBuilder.setInitialized();
        }

        return syncedStatePropertiesBuilder.getProperties();
    };

    default void createSyncedBlockStates(SyncedStatePropertiesBuilder builder){
        DirectionProperty directionProperty = getMultiBlock().getDirectionProperty();
        if (directionProperty != null){
            builder.add(directionProperty);
        }
    }

    /**
     * Updates the provided BlockStateProperties for each block in the multiblock
     * */
    static <T extends Comparable<T>> void setBlockStates(Level level, BlockPos pos, List<PropertyWrapper<?>> stateValuePairs){
        setBlockStates(level, pos, 3, stateValuePairs);
    }

    /**
     * Updates the provided BlockStateProperty for each block in the multiblock
     * */
    static <T extends Comparable<T>> void setBlockState(Level level, BlockPos pos, Property<T> property, T value){
        setBlockStates(level, pos, 3, List.of(new PropertyWrapper<>(property, value)));
    }

    /**
     * Updates the provided BlockStateProperties for each block in the multiblock
     * */
    static <T extends Comparable<T>> void setBlockStates(Level level, BlockPos pos, int flags, List<PropertyWrapper<?>> properties){

        getFullShape(level, pos).forEach(pos1 -> {
            BlockState state = level.getBlockState(pos1);

            for (PropertyWrapper<?> propertyWrapper : properties) {
                state = propertyWrapper.applyTo(state);
            }

            level.setBlock(pos1, state, flags);
        });
    }

    default void syncBlockStates(Level level, BlockPos pos, BlockState state){
        List<PropertyWrapper<?>> list = new ArrayList<>(getSyncedStateProperties());

        BlockPos centerPos = getCenter(level, pos);
        BlockState centerState = level.getBlockState(centerPos);
        boolean isCenter = isCenter(state);

        for (PropertyWrapper<?> propertyWrapper : list) {
            propertyWrapper.captureValue(state);

            if (!isCenter) {
                centerState = propertyWrapper.applyTo(centerState);
            }
        }


        if (!isCenter){
            level.setBlock(centerPos, centerState, 2);
            return;
        }


        setBlockStates(level, pos, list);
    }

    default IMultiBlock getMultiBlock(){
        if (this instanceof IMultiBlock block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on an IMultiBlock");
        }
    }
}
