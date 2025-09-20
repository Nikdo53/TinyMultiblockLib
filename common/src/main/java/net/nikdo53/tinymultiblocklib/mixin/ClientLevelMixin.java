package net.nikdo53.tinymultiblocklib.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blocks.IMultiBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Shadow
    @Final
    private LevelRenderer levelRenderer;

    @Inject(method = "destroyBlockProgress", at = @At(value = "HEAD"), cancellable = true)
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
        ClientLevel level = (ClientLevel) (Object)this;
        BlockState blockState = level.getBlockState(pos);

        if (blockState.getBlock() instanceof IMultiBlock multiBlock && !blockState.getRenderShape().equals(RenderShape.MODEL)) {

            levelRenderer.destroyBlockProgress(breakerId, multiBlock.getCenter(level, pos), progress);
            ci.cancel();
        }
    }
}
