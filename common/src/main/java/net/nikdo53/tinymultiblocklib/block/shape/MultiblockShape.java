package net.nikdo53.tinymultiblocklib.block.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.logic.DelegatingMultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class MultiblockShape {
    final Map<BlockPos, Entry> shape;
    final BlockPos center;

    public MultiblockShape(Map<BlockPos, Entry> shape, BlockPos center) {
        this.shape = shape;
        this.center = center;
    }

    public Map<BlockPos, Entry> getShape() {
        return shape;
    }

    public Set<BlockPos> getGlobalPositions() {
        return shape.keySet().stream().map(pos -> pos.offset(center)).collect(Collectors.toSet());
    }

    public Entry getEntryForGlobalPos(BlockPos pos){
        return shape.get(pos.subtract(center));
    }

    public record Entry(MultiblockLogic logic, Function<BlockState, BlockState> stateModifier, Map<ShapeDataKey<?>, Object> data) {
        public Entry(MultiblockLogic logic, UnaryOperator<BlockState> stateModifier) {
            this(logic, stateModifier, new HashMap<>());
        }

        public <T> T getData(ShapeDataKey<T> key) {
            @SuppressWarnings("unchecked")
            T value = (T) data.get(key);
            return value;
        }
    }

    public BlockPos getCenter() {
        return center;
    }

    public static class Builder {
        final BlockPos center;
        Map<BlockPos, Entry> map = new HashMap<>();
        List<Operation> operations = new ArrayList<>();
        List<SymbolShapeBuilder> symbolShapeBuilders = new ArrayList<>();

        public Builder(BlockPos center) {
            this.center = center;
        }

        public Builder addGlobal(BlockPos pos, MultiblockLogic logic, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(pos.subtract(center), logic, stateModifier, extraData);
            return this;
        }

        public SymbolShapeBuilder toSymbolBuilder(Direction fowardDirection){
            SymbolShapeBuilder symbolShapeBuilder = new SymbolShapeBuilder(fowardDirection);
            symbolShapeBuilders.add(symbolShapeBuilder);
            return symbolShapeBuilder;
        }

        // add() zone:

        public Builder add(Vec3i offset, MultiblockLogic logic, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(offset, logic, stateModifier, extraData);
            return this;
        }

        public Builder add(int x, int y, int z, MultiblockLogic logic, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(new Vec3i(x, y, z), logic, stateModifier, extraData);
            return this;
        }

        public Builder add(Vec3i offset, MultiblockLogic logic, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(offset, logic, UnaryOperator.identity(), extraData);
            return this;
        }

        public Builder add(int x, int y, int z, MultiblockLogic logic, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(new Vec3i(x, y, z), logic, UnaryOperator.identity(), extraData);
            return this;
        }



        public Builder addDelegating(Vec3i offset, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(offset, DelegatingMultiblockLogic.INSTANCE, UnaryOperator.identity(), extraData);
            return this;
        }

        public Builder addDelegating(int x, int y, int z, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(new Vec3i(x, y, z), DelegatingMultiblockLogic.INSTANCE, UnaryOperator.identity(), extraData);
            return this;
        }

        public Builder addDelegating(Vec3i offset, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(offset, DelegatingMultiblockLogic.INSTANCE, stateModifier, extraData);
            return this;
        }

        public Builder addDelegating(int x, int y, int z, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(new Vec3i(x, y, z), DelegatingMultiblockLogic.INSTANCE, stateModifier, extraData);
            return this;
        }



        public Builder addNoLogic(Vec3i offset, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(offset, MultiblockLogic.EMPTY, UnaryOperator.identity(), extraData);
            return this;
        }

        public Builder addNoLogic(int x, int y, int z, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(new Vec3i(x, y, z), MultiblockLogic.EMPTY, UnaryOperator.identity(), extraData);
            return this;
        }

        public Builder addNoLogic(Vec3i offset, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(offset, MultiblockLogic.EMPTY, stateModifier, extraData);
            return this;
        }

        public Builder addNoLogic(int x, int y, int z, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(new Vec3i(x, y, z), MultiblockLogic.EMPTY, stateModifier, extraData);
            return this;
        }




        protected void putOrThrow(Vec3i offset, MultiblockLogic logic, Function<BlockState, BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData){
            if (localPosSuspiciouslyGlobalLooking(offset)){
                throw new IllegalArgumentException("Offset " + offset + " looks like a global position. If you want to add a global position, use addGlobal() instead");
            }
            Map<ShapeDataKey<?>, Object> dataMap = new HashMap<>();
            for (ShapeDataKey.Pair<?> pair : extraData) {
                dataMap.put(pair.key(), pair.value());
            }
            
            for (Operation operation : operations) {
                offset = operation.posModifier().apply(offset);
                logic = operation.logicModifier().apply(logic);
                stateModifier = stateModifier.andThen(operation.stateModifier()); // this doesnt work with unary operators
            }

            map.put(new BlockPos(offset), new Entry(logic, stateModifier, dataMap));
        }

        public Builder pushOperation(UnaryOperator<Vec3i> posModifier, UnaryOperator<MultiblockLogic> logicModifier, UnaryOperator<BlockState> stateModifier) {
            operations.add(new Operation(posModifier, logicModifier, stateModifier));
            return this;
        }

        public Builder pushDirectionalOperation(Direction direction, Vec3i pivotPoint) {
            return pushOperation(offset -> {
                        Vec3i rotatedOffset = rotateVector(direction, offset);
                        Vec3i rotatedPivotPoint = rotateVector(direction, pivotPoint);
                        return rotatedOffset.offset(rotatedPivotPoint);
                    },
                    UnaryOperator.identity(),
                    UnaryOperator.identity()
            );
        }

        public Builder pushDirectionalOperation(Direction direction) {
            return pushDirectionalOperation(direction, Vec3i.ZERO);
        }

        private static @NotNull Vec3i rotateVector(Direction direction, Vec3i offset) {
            return switch (direction) {
                case NORTH -> new Vec3i(offset.getX(), offset.getY(), offset.getZ());
                case SOUTH -> new Vec3i(-offset.getX(), offset.getY(), -offset.getZ());

                case EAST -> new Vec3i(-offset.getZ(), offset.getY(), offset.getX());
                case WEST -> new Vec3i(offset.getZ(), offset.getY(), -offset.getX());

                case UP -> new Vec3i(-offset.getY(), offset.getX(), offset.getZ());
                case DOWN -> new Vec3i(offset.getY(), -offset.getX(), offset.getZ());
            };
        }


        public Builder popOperation() {
            operations.removeLast();
            return this;
        }

        /**
         * Builds the multiblock shape. This method is called automatically
         * @return the built multiblock shape
         */
        @ApiStatus.Internal
        public MultiblockShape build(){
            for (SymbolShapeBuilder builder : symbolShapeBuilders) {
                builder.build(this);
            }
            return new MultiblockShape(map, center);
        }

        public boolean localPosSuspiciouslyGlobalLooking(Vec3i offset){
            boolean offsetCloseToGlobal = offset.distManhattan(center) < 3;
            boolean centerFarFromLocal = center.distManhattan(BlockPos.ZERO) > 30;
            boolean offsetDetached = offset.distManhattan(BlockPos.ZERO) > 8 + map.size();
            return offsetCloseToGlobal && centerFarFromLocal && offsetDetached;
        }


        public record Operation(UnaryOperator<Vec3i> posModifier, UnaryOperator<MultiblockLogic> logicModifier, UnaryOperator<BlockState> stateModifier) {}
    }
}
