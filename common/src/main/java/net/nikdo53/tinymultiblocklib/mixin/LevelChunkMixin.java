package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.nikdo53.tinymultiblocklib.block.IExpandingMultiblock;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0), cancellable = true)
    public void setBlockEntity(BlockEntity blockEntity, CallbackInfo ci, @Local BlockState state) {
        if (blockEntity instanceof IMultiBlockEntity multiBlockEntity && !multiBlockEntity.isPlaced()){
            ci.cancel();
        }
    }

}
