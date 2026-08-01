package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.nikdo53.tinymultiblocklib.client.*;
import net.nikdo53.tinymultiblocklib.client.extensions.IFeatureRenderDispatcherExtension;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public abstract class FeatureRenderDispatcherMixin implements IFeatureRenderDispatcherExtension {
    @Unique
    boolean tinyMultiblockLib$isUploading = true;

    @Shadow
    @Final
    private StagedVertexBuffer stagedVertexBuffer;

    @Shadow
    @Final
    private FeatureRendererMap featureRenderers;

    @Shadow
    protected abstract FeatureRenderDispatcher.PreparedFrame prepareFrameWithContext(FeatureFrameContext context, SubmitNodeStorage submitNodeStorage);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addPreviewType(RenderBuffers renderBuffers, ModelManager modelManager, AtlasManager atlasManager, Font font, GameRenderState gameRenderState, CallbackInfo ci) {
        this.featureRenderers.put(PreviewFeatureRenderer.TYPE, new PreviewFeatureRenderer());
    }

    @WrapOperation(method = "prepareFrameWithContext", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/StagedVertexBuffer;upload()V"))
    private void cancelUploadingWrap(StagedVertexBuffer instance, Operation<Void> original){
        if (isUploading()){
            original.call(instance);
        }
    }

    @Override
    public void toggleUploading(boolean uploading) {
        tinyMultiblockLib$isUploading = uploading;
    }

    @Override
    public boolean isUploading() {
        return tinyMultiblockLib$isUploading;
    }

    @Override
    public FeatureRenderDispatcher.@NonNull PreparedFrame tinyMultiblockLib$prepareFrameWithContextPublic(FeatureFrameContext context, SubmitNodeStorage submitNodeStorage) {
        return prepareFrameWithContext(context, submitNodeStorage);
    }
}
