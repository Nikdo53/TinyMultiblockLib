package net.nikdo53.tinymultiblocklib.test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.nikdo53.tinymultiblocklib.block.*;
import net.nikdo53.tinymultiblocklib.block.logic.MultiblockLogic;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.block.shape.ShapeContext;
import org.jetbrains.annotations.Nullable;

public class SimpleMultiblock extends LogicMultiblock implements IPreviewableMultiblock {
    public SimpleMultiblock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable DirectionContext makeDirectional() {
        return DirectionContext.horizontal();
    }


    @Override
    public void makeMultiblockShape(MultiblockShape.Builder builder, ShapeContext context) {
        builder.toSymbolBuilder(Direction.UP)
                .nextAisle("xxx",
                           "xcx",
                           "xxx")
                .nextAisle("xxx",
                           "x x",
                           "xxx")
                .where('x', MultiblockShape.Builder::addNoLogic);
    }

    @Override
    public MultiblockLogic getCenterLogic() {
        return new MultiblockLogic(){
            @Override
            public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
                return InteractionResult.SUCCESS;
            }
        };
    }


}
