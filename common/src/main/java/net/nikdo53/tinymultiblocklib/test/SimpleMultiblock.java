package net.nikdo53.tinymultiblocklib.test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.*;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeContext;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeDataKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class SimpleMultiblock extends BaseMultiblock implements IPreviewableMultiblock {
    public SimpleMultiblock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable DirectionContext makeDirectional() {
        return DirectionContext.allAxis();
    }

    @Override
    public void makeMultiblockShape(MultiblockShape.Builder builder, ShapeContext context) {
        Direction direction = context.getDirection();
        builder.pushDirectionalOperation(direction);

        builder.toSymbolBuilder(Direction.NORTH)
                .nextAisle(" x",
                           "xc")
                .nextAisle("  ",
                           " x")
                .where('x', UnaryOperator.identity())
        ;
    }

}
