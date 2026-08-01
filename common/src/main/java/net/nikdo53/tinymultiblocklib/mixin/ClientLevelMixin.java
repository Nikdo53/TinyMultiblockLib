package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.client.FakeClientLevel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Shadow
    @Final
    private LevelExtractor levelExtractor;

    @Shadow
    public abstract void destroyBlockProgress(int id, BlockPos pos, int progress);

    /**
     * Transfers destroyProgress to the center block
     * */
    @Inject(method = "destroyBlockProgress", at = @At(value = "HEAD"), cancellable = true)
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
        ClientLevel level = tinyMultiblockLib$self();
        BlockState blockState = level.getBlockState(pos);
        BlockPos center = IMultiBlock.getCenter(level, pos);

        if (IMultiBlock.isMultiblock(blockState) && !blockState.getRenderShape().equals(RenderShape.MODEL) && !pos.equals(center)) {
            this.destroyBlockProgress(breakerId, center, progress);
            ci.cancel();
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void afterLoad(ClientPacketListener connection, ClientLevel.ClientLevelData levelData, ResourceKey dimension, Holder dimensionType, int serverChunkRadius, int serverSimulationDistance, LevelExtractor levelExtractor, boolean isDebug, long biomeZoomSeed, int seaLevel, CallbackInfo ci){
        if (!(tinyMultiblockLib$self() instanceof FakeClientLevel)) {
            FakeClientLevel.INSTANCE = new FakeClientLevel(tinyMultiblockLib$self());
        }
    }

    @Unique
    private @NotNull ClientLevel tinyMultiblockLib$self() {
        return (ClientLevel) (Object) this;
    }
}
