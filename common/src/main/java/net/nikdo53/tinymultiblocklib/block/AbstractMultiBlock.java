package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeContext;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;


/**
 * @deprecated Use {@link LogicMultiblock} instead
 */
@Deprecated
public abstract class AbstractMultiBlock extends BaseMultiblock implements IMovableMultiblock, EntityBlock {
    /**
     * The BlockState of the multiblocks center block, ideally you should forward all logic to this block
     * <p>
     * Note that even though it's called "CENTER", it isn't necessarily the actual center.
     * The center is just where the block would be placed if it were just a single block
     * @see #isCenter(BlockState)
     * @see #getCenter(BlockGetter, BlockPos)
     * */
    public static final BooleanProperty CENTER = BaseMultiblock.CENTER;

    public AbstractMultiBlock(Properties properties) {
        super(properties);
    }

    public abstract List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction);

    @Override
    public void makeMultiblockShape(MultiblockShape.Builder builder, ShapeContext context) {
        BlockState state = context.getBlockState();
        EnumProperty<Direction> directionProperty = getDirectionProperty();
        List<BlockPos> list = makeFullBlockShape(context.getLevel(), context.getCenterPos(), state, context.getBlockEntity(), directionProperty != null ? state.getValue(directionProperty) : null);
        Set<BlockPos> set = new HashSet<>(list);
        if (set.size() < list.size()) {
            Constants.LOGGER.error("Multiblock {} at {} has overlapping blocks in it's shape,"
                            + " this is likely caused by the BlockPos being mutable."
                            + " Either map them to BlockPos::immutable or use IMultiBlock.posStreamToList()",
                    state.toString(), context.getCenterPos());
        }

        list.forEach(pos -> builder.addGlobal(pos, getCenterLogic(), UnaryOperator.identity()));

    }

    @Override
    public MultiblockLogic getCenterLogic() {
        return MultiblockLogic.EMPTY;
    }
}
