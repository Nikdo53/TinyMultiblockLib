package net.nikdo53.tinymultiblocklib.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor {
    @Mutable
    @Accessor("validBlocks")
    void tinymultiblocklib$setValidBlocks(Set<Block> validBlocks);

    @Accessor("validBlocks")
    Set<Block> tinymultiblocklib$getValidBlocks();

}
