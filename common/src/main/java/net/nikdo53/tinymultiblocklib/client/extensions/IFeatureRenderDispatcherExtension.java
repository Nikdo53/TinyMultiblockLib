package net.nikdo53.tinymultiblocklib.client.extensions;

import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;

// Implemented via mixin
public interface IFeatureRenderDispatcherExtension {
    void toggleUploading(boolean uploading);

    // Stops StagedVertexBuffer from uploading, used with fake FeatureRenderDispatchers that run under the main one
    boolean isUploading();

    FeatureRenderDispatcher.PreparedFrame tinyMultiblockLib$prepareFrameWithContextPublic(FeatureFrameContext context, SubmitNodeStorage submitNodeStorage);

}