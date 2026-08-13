package net.nikdo53.tinymultiblocklib.block.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;


@NullMarked
public class ShapeContext {
    protected final Level level;
    protected final @Nullable BlockEntity blockEntity;
    protected final BlockState blockState;
    protected final BlockPos centerPos;
    protected final IMultiBlock multiBlock;

    // these might be used for performance optimization later on
    boolean usesStateContext = false;
    boolean usesLevelContext = false;

    public ShapeContext(@Nullable Level level, BlockPos centerPos, BlockState blockState, @Nullable BlockEntity blockEntity, IMultiBlock multiBlock) {
        this.level = level;
        this.blockEntity = blockEntity;
        this.blockState = blockState;
        this.centerPos = centerPos;
        this.multiBlock = multiBlock;
    }

    public Level getLevel() {
        if (getOldProperties() != null && !getOldProperties().usesLevelContext){
            throw new IllegalStateException("Shape properties changed unexpectedly, please include any getters from the context at the top of the makeMultiblockShape method.");
        }
        usesLevelContext = true;
        return level;
    }

    public @Nullable BlockEntity getBlockEntity() {
        if (getOldProperties() != null && !getOldProperties().usesLevelContext){
            throw new IllegalStateException("Shape properties changed unexpectedly, please include any getters from the context at the top of the makeMultiblockShape method.");
        }
        usesLevelContext = true;
        return blockEntity;
    }

    public BlockState getBlockState() {
        if (getOldProperties() != null && !getOldProperties().usesStateContext){
            throw new IllegalStateException("Shape properties changed unexpectedly, please include any getters from the context at the top of the makeMultiblockShape method.");
        }
        usesStateContext = true;
        return blockState;
    }

    public BlockPos getCenterPos() {
        return centerPos;
    }

    public Direction getDirection() {
        if (multiBlock.getDirectionProperty() == null) {
            throw new IllegalStateException("Multiblock does not have a direction property");
        }
        return Objects.requireNonNull(multiBlock.getDirection(getBlockState()));
    }

    protected ShapeContext.@Nullable Properties getOldProperties(){
        return multiBlock.getShapeProperties();
    }

    @ApiStatus.Internal
    public Properties getProperties() {
        return new Properties(usesStateContext, usesLevelContext);
    }


    public record Properties(boolean usesStateContext, boolean usesLevelContext){

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Properties that)) return false;
            return usesStateContext == that.usesStateContext && usesLevelContext == that.usesLevelContext;
        }
    }
}
