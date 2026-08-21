package net.nikdo53.tinymultiblocklib.block.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.logic.DelegatingMultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.util.TMBLUtils;
import net.nikdo53.tinymultiblocklib.util.VoxelShapeUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class MultiblockShape {
    final Map<BlockPos, Entry> shape;
    final BlockPos center;
    VoxelShape jointVoxelShape = Shapes.empty();

    public MultiblockShape(Map<BlockPos, Entry> shape, BlockPos center) {
        this.shape = shape;
        this.center = center;
    }

    public static MultiblockShape empty(){
        return new MultiblockShape(Collections.emptyMap(), BlockPos.ZERO);
    }

    public Map<BlockPos, Entry> getShape() {
        return shape;
    }

    public Entry getEntry(BlockPos offset){
        return shape.get(offset);
    }

    public Entry getEntryGlobal(BlockPos globalPos){
        return shape.get(getOffset(globalPos));
    }

    public BlockPos getOffset(BlockPos globalPos){
        return globalPos.subtract(center);
    }

    public Set<BlockPos> getGlobalPositions() {
        return shape.keySet().stream().map(pos -> pos.offset(center)).collect(Collectors.toSet());
    }

    public Entry getEntryForGlobalPos(BlockPos pos){
        return shape.get(pos.subtract(center));
    }

    public VoxelShape getJointVoxelShape(Level level, BlockState state) {
        if (state.getBlock() instanceof AbstractMultiBlock){
            AABB shape = state.getShape(level, getCenter()).bounds();
            AABB block = AABB.unitCubeFromLowerCorner(new Vec3(0, 0, 0));
            // should cancel fancy outlines for blocks that already have them done manually
            if (TMBLUtils.isShapeBiggerThan(shape, block)) {
                return Shapes.empty();
            }

        }
        if (jointVoxelShape.isEmpty()) {
            shape.forEach((pos, entry) -> {
                //cut out only the block so it isn't huge
                VoxelShape shape1 = Shapes.join(state.getShape(level, pos.offset(getCenter())), Shapes.block(), BooleanOp.AND);

                jointVoxelShape = Builder.joinMoved(jointVoxelShape, shape1, BooleanOp.OR, pos);
            });
        }
        return jointVoxelShape;
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

        public <T> T getDataOrDefault(ShapeDataKey<T> key, T defaultValue) {
            @SuppressWarnings("unchecked")
            T value = (T) data.get(key);
            return value != null ? value : defaultValue;
        }

        public boolean hasData(ShapeDataKey<?> key) {
            return data.containsKey(key);
        }

        @ApiStatus.Internal
        public <T> void putData(ShapeDataKey<T> key, T value) {
            data.put(key, value);
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
        VoxelShape jointVoxelShape = Shapes.empty();

        public Builder(BlockPos center, MultiblockLogic centerLogic) {
            this.center = center;
            map.put(BlockPos.ZERO, new Entry(centerLogic, UnaryOperator.identity()));
        }

        public Builder addGlobal(BlockPos pos, MultiblockLogic logic, UnaryOperator<BlockState> stateModifier, ShapeDataKey.Pair<?>... extraData) {
            putOrThrow(pos.subtract(center), logic, stateModifier, extraData);
            return this;
        }

        /**
         * Creates a new symbol shape builder
         * @param fowardDirection direction where the builder is facing, each .nextAisle() moves the builder in that direction
         *                      ❗THIS IS NOT THE MULTIBLOCKS DIRECTION❗
         */
        public SymbolShapeBuilder toSymbolBuilder(Direction fowardDirection){
            SymbolShapeBuilder symbolShapeBuilder = new SymbolShapeBuilder(fowardDirection);
            symbolShapeBuilders.add(symbolShapeBuilder);
            return symbolShapeBuilder;
        }

        public Builder setJointVoxelShape(VoxelShape shape){
            jointVoxelShape = shape;
            return this;
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

                dataMap.replaceAll((key, value) -> {
                    ShapeDataKey.Operation dataModifier = operation.getDataModifier(key);
                    if (dataModifier != null) {
                        return dataModifier.operation().apply(value);
                    }
                    return value;
                });
            }

            if (!offset.equals(Vec3i.ZERO)) {
                map.put(new BlockPos(offset), new Entry(logic, stateModifier, dataMap));
            } else {
                Entry centerEntry = map.get(BlockPos.ZERO); // center always exists and uses its own logic
                map.put(BlockPos.ZERO, new Entry(centerEntry.logic(), stateModifier, dataMap));
            }
        }

        public Builder pushOperation(UnaryOperator<Vec3i> posModifier, UnaryOperator<MultiblockLogic> logicModifier,
                                     UnaryOperator<BlockState> stateModifier, ShapeDataKey.Operation<?>... dataModifiers) {
            operations.add(new Operation(posModifier, logicModifier, stateModifier, dataModifiers));
            return this;
        }

        public Builder pushDirectionalOperation(Direction direction, Vec3i pivotPoint) {
            return pushOperation(offset -> {
                        Vec3i rotatedOffset = rotateVector(direction, offset);
                        Vec3i rotatedPivotPoint = rotateVector(direction, pivotPoint);
                        return rotatedOffset.offset(rotatedPivotPoint);
                    },
                    UnaryOperator.identity(),
                    UnaryOperator.identity(),
                    new ShapeDataKey.Operation<>(ShapeDataKey.VOXEL_SHAPE, shape -> VoxelShapeUtils.rotateAll(direction, shape))
            );
        }

        public Builder pushDirectionalOperation(Direction direction) {
            return pushDirectionalOperation(direction, Vec3i.ZERO);
        }

        protected static @NotNull Vec3i rotateVector(Direction direction, Vec3i offset) {
            return switch (direction) {
                case NORTH -> new Vec3i(offset.getX(), offset.getY(), offset.getZ());
                case SOUTH -> new Vec3i(-offset.getX(), offset.getY(), -offset.getZ());

                case EAST -> new Vec3i(-offset.getZ(), offset.getY(), offset.getX());
                case WEST -> new Vec3i(offset.getZ(), offset.getY(), -offset.getX());

                case UP -> new Vec3i(-offset.getY(), -offset.getZ(),  -offset.getX());
                case DOWN -> new Vec3i(offset.getY(), offset.getZ(), -offset.getX());
            };
        }

        protected void buildVoxelShapes(){

            if (!jointVoxelShape.isEmpty()) {
                map.forEach((pos, entry) -> {
                    if (!entry.hasData(ShapeDataKey.VOXEL_SHAPE)) {
                        // cut the joint shape into pieces and store them in the entry
                        entry.putData(ShapeDataKey.VOXEL_SHAPE, joinMoved(Shapes.block(), jointVoxelShape, BooleanOp.AND, pos.multiply(-1)));
                    }
                });

            }

        }

        static protected VoxelShape joinMoved(VoxelShape first, VoxelShape second, BooleanOp op, BlockPos pos){
            return Shapes.join(first, second.move(pos.getX(), pos.getY(), pos.getZ()), op);
        }

        public Builder popOperation() {
            operations.remove(operations.size() - 1);
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
            buildVoxelShapes();
            return new MultiblockShape(map, center);
        }

        public boolean localPosSuspiciouslyGlobalLooking(Vec3i offset){
            boolean offsetCloseToGlobal = offset.distManhattan(center) < 3;
            boolean centerFarFromLocal = center.distManhattan(BlockPos.ZERO) > 30;
            boolean offsetDetached = offset.distManhattan(BlockPos.ZERO) > 8 + map.size();
            return offsetCloseToGlobal && centerFarFromLocal && offsetDetached;
        }


        public record Operation(UnaryOperator<Vec3i> posModifier, UnaryOperator<MultiblockLogic> logicModifier,
                                UnaryOperator<BlockState> stateModifier, ShapeDataKey.Operation<?>[] dataModifiers ) {
            @SuppressWarnings("unchecked")
            public <T> @Nullable ShapeDataKey.Operation<T> getDataModifier(ShapeDataKey<T> key) {
                for (ShapeDataKey.Operation<?> dataModifier : dataModifiers) {
                   if (dataModifier.key().equals(key)) return (ShapeDataKey.Operation<T>) dataModifier;
                }
                return null;
            }
        }
    }
}
