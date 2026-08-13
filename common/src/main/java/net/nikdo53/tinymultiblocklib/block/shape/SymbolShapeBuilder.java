package net.nikdo53.tinymultiblocklib.block.shape;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public class SymbolShapeBuilder {
    public static final char CENTER_CHAR = 'c';
    protected final Int2ObjectArrayMap<String[]> pattern = new Int2ObjectArrayMap<>();
    protected final Map<Character, BiConsumer<MultiblockShape.Builder, Vec3i>> lookup = Maps.newHashMap();
    protected int height;
    protected int width;
    protected final CharSet unknownCharacters = new CharOpenHashSet();
    protected final Direction direction;
    protected @Nullable Vec3i centerPos = null;
    protected int currentDepth = 0;

    public SymbolShapeBuilder(Direction direction) {
        this.direction = direction;

        this.lookup.put(' ', (a, b) -> {});
        this.lookup.put(CENTER_CHAR, (a, b) -> {});
    }

    public SymbolShapeBuilder nextAisle(String... aisle) {
        return this.aisle(this.currentDepth, aisle);
    }

    public SymbolShapeBuilder aisle(int depth, String... aisle) {
        if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
            if (this.pattern.isEmpty()) {
                this.height = aisle.length;
                this.width = aisle[0].length();
            }

            if (aisle.length != this.height) {
                throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + aisle.length + ")");
            } else {
                int y = 0;
                for(String row : aisle) {
                    if (row.length() != this.width) {
                        int var10002 = this.width;
                        throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + var10002 + ", found one with " + row.length() + ")");
                    }

                    int z = 0;
                    for(char c : row.toCharArray()) {
                        if (!this.lookup.containsKey(c)) {
                            this.unknownCharacters.add(c);
                        }
                        if (c == CENTER_CHAR) {
                            if (this.centerPos != null) {
                                throw new IllegalArgumentException("Multiple center positions found");
                            }
                            this.centerPos = new Vec3i(depth, y, z);
                        }
                        z++;
                    }

                    y++;
                }

                this.pattern.put(this.currentDepth, aisle);
                this.currentDepth++;
                return this;
            }
        } else {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
    }

    public SymbolShapeBuilder where(char character, BiConsumer<MultiblockShape.Builder, Vec3i> predicate) {
        if (character == CENTER_CHAR) {
            throw new IllegalArgumentException("Cannot use character 'C' as a predicate, it is already set and reserved for the center position");
        }
        this.lookup.put(character, predicate);
        this.unknownCharacters.remove(character);
        return this;
    }

    /**
     * No need to call this method, it is called by the multiblock builder
     */
    @ApiStatus.Internal
    public void build(MultiblockShape.Builder multiblockBuilder){
        if (!this.unknownCharacters.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + this.unknownCharacters + " are missing");
        }

        if (this.centerPos == null) {
            throw new IllegalArgumentException("No center position (Character 'C') defined");
        }

        multiblockBuilder.pushDirectionalOperation(direction);

        pattern.forEach((depth, aisle) -> {
            for(int y = 0; y < aisle.length; y++) {
                for(int z = 0; z < aisle[y].length(); z++) {
                    char c = aisle[y].charAt(z);
                    BiConsumer<MultiblockShape.Builder, Vec3i> consumer = lookup.get(c);
                    consumer.accept(multiblockBuilder, new Vec3i(depth, y, z).subtract(this.centerPos));
                }
            }
        });

        multiblockBuilder.popOperation();

    }

}
