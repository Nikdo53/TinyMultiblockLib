package net.nikdo53.tinymultiblocklib.test;

import net.minecraft.core.Direction;
import net.nikdo53.tinymultiblocklib.block.*;
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
        return DirectionContext.horizontal();
    }

    @Override
    public void makeMultiblockShape(MultiblockShape.Builder builder, ShapeContext context) {
        builder.pushDirectionalOperation(context.getDirection());

        builder.toSymbolBuilder(Direction.UP)
                .nextAisle("xxx",
                           "xcx",
                           "xxx")

                .nextAisle("xxx",
                           "x x",
                           "xxx")


                .where('x', state -> state)
        ;
    }

}
