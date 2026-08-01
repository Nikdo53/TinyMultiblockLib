package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.nikdo53.tinymultiblocklib.client.extensions.IFeatureRenderDispatcherExtension;
import net.nikdo53.tinymultiblocklib.client.extensions.IStagedVertexBufferWrapExtension;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewFeatureRenderer extends RenderTypeFeatureRenderer<PreviewFeatureRenderer.Submit> {
    public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.create("TinyMultiBlockLib Preview");
    public final Map<Submit, FeatureRenderDispatcher.PreparedFrame> preparedFrames = new HashMap<>();

    @Override
    protected void buildGroup(FeatureFrameContext context, List<Submit> submits) {
        Minecraft minecraft = Minecraft.getInstance();
        IStagedVertexBufferWrapExtension buffer = (IStagedVertexBufferWrapExtension) context.stagedVertexBuffer();

        for (Submit submit : submits) {
            FeatureRenderDispatcher fakeFeatureRenderDispatcher = new FeatureRenderDispatcher(
                    minecraft.gameRenderer.renderBuffers(), minecraft.getModelManager(), minecraft.getAtlasManager(), minecraft.font, minecraft.gameRenderer.gameRenderState());

            IFeatureRenderDispatcherExtension extension = (IFeatureRenderDispatcherExtension) fakeFeatureRenderDispatcher;
            extension.toggleUploading(false);

            buffer.setVertexConsumerWrapper(v -> new TintedVertexConsumer(v, submit.color()));
            preparedFrames.put(submit,
                    extension.tinyMultiblockLib$prepareFrameWithContextPublic(
                        new FeatureFrameContext(
                                context.options(),
                                context.font(),
                                context.blockStateModelSet(),
                                context.blockColors(),
                                context.textureManager(),
                                context.atlasManager(),
                                context.lightmap(),
                                context.stagedVertexBuffer()
                        ),
                        submit.submitNodeCollector()
            ));
        }

        buffer.setVertexConsumerWrapper(null);
    }

    @Override
    public void executeGroup(FeatureFrameContext context, int groupIndex, List<Submit> submits, boolean strictlyOrdered) {
        super.executeGroup(context, groupIndex, submits, strictlyOrdered);

        for (Submit submit : submits) {
            FeatureRenderDispatcher.PreparedFrame preparedFrame = preparedFrames.get(submit);
            if (preparedFrame != null) {
                preparedFrame.executeSolid();
                preparedFrame.executeTranslucent();
                preparedFrame.executeTranslucentAfterTerrain();
                preparedFrame.executeAlwaysOnTop();
            }
        }
    }

    @Override
    public void finishExecute(FeatureFrameContext context) {
        super.finishExecute(context);
        preparedFrames.clear();
    }

    public record Submit(@Nullable Matrix4fc pose, SubmitNodeStorage submitNodeCollector, IColorSupplier color) implements TranslucentSubmit {

        @Override
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq(this.pose != null ? this.pose : new Matrix4f(), 0.5F, 0.5F, 0.5F);
        }

        @Override
        public FeatureRendererType<? extends TranslucentSubmit> featureType() {
            return TYPE;
        }
    }
}
