package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class MultiblockShape {
    final Map<BlockPos, Entry> shape;

    public MultiblockShape(Map<BlockPos, Entry> shape) {
        this.shape = shape;
    }

    public Map<BlockPos, Entry> getShape() {
        return shape;
    }

    public Set<BlockPos> getPositions() {
        return shape.keySet();
    }

    public Stream<BlockPos> getPosStream() {
        return shape.keySet().stream();
    }

    public record Entry(MultiblockLogic logic, UnaryOperator<BlockState> stateModifier) {}

    public static class Builder {
        final BlockPos center;
        Map<BlockPos, Entry> map = new HashMap<>();

        public Builder(BlockPos center) {
            this.center = center;
        }

        @ApiStatus.Internal
        public Builder addGlobal(BlockPos pos, MultiblockLogic logic) {
            putOrThrow(pos, logic, UnaryOperator.identity());
            return this;
        }

        public Builder add(Vec3i offset, MultiblockLogic logic, UnaryOperator<BlockState> stateModifier) {
            putOrThrow(center.offset(offset), logic, stateModifier);
            return this;
        }

        public Builder add(Vec3i offset, MultiblockLogic logic) {
            putOrThrow(center.offset(offset), logic,UnaryOperator.identity());
            return this;
        }

        public Builder add(int x, int y, int z, MultiblockLogic logic) {
            putOrThrow(center.offset(x, y, z), logic, UnaryOperator.identity());
            return this;
        }

        protected void putOrThrow(BlockPos pos, MultiblockLogic logic, UnaryOperator<BlockState> stateModifier){
            map.put(pos, new Entry(logic, stateModifier));
        }

        public Builder.Directional pushDirectional(Direction direction) {
            Directional builder = new Directional(center, direction);
            builder.map = this.map;
            return builder;
        }

        public MultiblockShape build(){
            return new MultiblockShape(map);
        }

        public static class Directional extends Builder{
            final Direction direction;
            public Directional(BlockPos center, Direction direction) {
                super(center);
                this.direction = direction;
            }

            @Override
            protected void putOrThrow(BlockPos pos, MultiblockLogic logic, UnaryOperator<BlockState> stateModifier) {
                Vec3i normal = direction.getUnitVec3i();
                pos.multiply(normal.getX(), normal.getY(), normal.getZ());
                super.putOrThrow(pos, logic, stateModifier);
            }

            public Builder popDirectional() {
                Builder builder = new Builder(center);
                builder.map = this.map;
                return builder;
            }

        }
    }
}
