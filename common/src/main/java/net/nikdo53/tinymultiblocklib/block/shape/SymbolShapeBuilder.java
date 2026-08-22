package net.nikdo53.tinymultiblocklib.block.shape;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class SymbolShapeBuilder {
    public static final char CENTER_CHAR = 'C';
    protected final Int2ObjectArrayMap<String[]> pattern = new Int2ObjectArrayMap<>();
    protected final Map<Character, BiConsumer<MultiblockShape.Builder, Vec3i>> lookup = Maps.newHashMap();
    protected int height;
    protected int width;
    protected final CharSet unknownCharacters = new CharOpenHashSet();
    protected final Direction direction;
    protected @Nullable Vec3i centerPos = null;
    protected int currentDepth = 0;

    /**
     * Creates a new symbol shape builder
     * @param direction direction where the builder is facing, each .nextAisle() moves the builder in that direction ❗❗ DO NOT USE THE MULTIBLOCKS DIRECTION❗❗
     */
    public SymbolShapeBuilder(Direction direction) {
        this.direction = direction;

        this.lookup.put(' ', (a, b) -> {});
        this.lookup.put(CENTER_CHAR, (a, b) -> {});
    }

    /**
     * creates the next aisle 1 block forwards in the direction of the builder
     * @param aisle the aisle to be added
     * @return this builder for chaining
     */
    public SymbolShapeBuilder nextAisle(String... aisle) {
        return this.aisle(this.currentDepth, aisle);
    }

    /**
     * creates the next aisle in the given depth
     * @param depth the depth of the aisle, 0 is always the aisle the center ('C') is located, moves in the diretion of the builder
     * @param aisle the aisle to be added
     * @return this builder for chaining
     */
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

                    int x = 0;
                    for(char c : row.toCharArray()) {
                        c = Character.toUpperCase(c);
                        if (!this.lookup.containsKey(c)) {
                            this.unknownCharacters.add(c);
                        }
                        if (c == CENTER_CHAR) {
                            if (this.centerPos != null) {
                                throw new IllegalArgumentException("Multiple center positions found");
                            }
                            this.centerPos = getCorrectedVector(x, y, depth);
                        }
                        x++;
                    }

                    y--;
                }

                this.pattern.put(this.currentDepth, aisle);
                this.currentDepth++;
                return this;
            }
        } else {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
    }

    /**
     * Adds a predicate for a specific character. All characters are converted to uppercase before being stored, so 'a' and 'A' are treated the same.
     * This is so people don't accidentally assume lowercase == uppercase
     * @param character the character to be added
     * @param predicate operation to be performed on the multiblock builder
     * @return this builder for chaining
     */
    public SymbolShapeBuilder where(char character, BiConsumer<MultiblockShape.Builder, Vec3i> predicate) {
        character = Character.toUpperCase(character);

        this.lookup.put(character, predicate);
        this.unknownCharacters.remove(character);
        return this;
    }

    /**
     * Adds a predicate for a specific character. All characters are converted to uppercase before being stored, so 'a' and 'A' are treated the same.
     * This is so people don't accidentally assume lowercase == uppercase
     * <p>
     * simplified version of {@link #where(char, BiConsumer)}
     * @param character the character to be added
     * @param stateOperator operation to be performed on the block state
     * @param extraData extra data to be passed to the shape entry
     * @return this builder for chaining
     */
    public SymbolShapeBuilder where(char character, UnaryOperator<BlockState> stateOperator, ShapeDataKey.Pair<?>... extraData) {
        character = Character.toUpperCase(character);

        this.lookup.put(character, (b,v) -> b.addNoLogic(v, stateOperator, extraData));
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

        //adds this rotation to the first place in a cursed way cuz I doubt this will be needed again
        MultiblockShape.Builder.Operation last = multiblockBuilder.operations.get(multiblockBuilder.operations.size() - 1);
        multiblockBuilder.popOperation();
        multiblockBuilder.operations.add(0, last);

        pattern.forEach((depth, aisle) -> {
            for(int y = 0; y < aisle.length; y++) {
                for(int x = 0; x < aisle[y].length(); x++) {
                    char c = aisle[y].charAt(x);
                    c = Character.toUpperCase(c);
                    BiConsumer<MultiblockShape.Builder, Vec3i> consumer = lookup.get(c);
                    assert this.centerPos != null;
                    consumer.accept(multiblockBuilder, getCorrectedVector(x, -y, -depth).subtract(this.centerPos));
                }
            }
        });


        multiblockBuilder.operations.remove(0);
    }

    private @NotNull Vec3i getCorrectedVector(int x, int y, int depth) {
        return new Vec3i( this.direction == Direction.UP ? -x : x, this.direction == Direction.DOWN ? -y : y, depth);
    }

}
