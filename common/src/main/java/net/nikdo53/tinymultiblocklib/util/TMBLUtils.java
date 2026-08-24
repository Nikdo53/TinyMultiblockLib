package net.nikdo53.tinymultiblocklib.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class TMBLUtils {
    public static <X, Y, Z, R> TriFunction<X, Y, Z, R> memoize(final TriFunction<X, Y, Z, R> memoTriFunction) {
        return new TriFunction<>() {
            private final Map<Triple<X, Y, Z>, R> cache = new ConcurrentHashMap<>();

            public R apply(X p_214700_, Y p_214701_, Z p_214702_) {
                return (R) this.cache.computeIfAbsent(Triple.of(p_214700_, p_214701_, p_214702_), (p_214698_) -> memoTriFunction.apply(p_214698_.getLeft(), p_214698_.getMiddle(), p_214698_.getRight()));
            }

            public String toString() {
                String var10000 = String.valueOf(memoTriFunction);
                return "memoize/3[function=" + var10000 + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static boolean isShapeBiggerThan(AABB shape, AABB otherShape){
        return shape.getXsize() > otherShape.getXsize() || shape.getYsize() > otherShape.getYsize() || shape.getZsize() > otherShape.getZsize();
    }

}
