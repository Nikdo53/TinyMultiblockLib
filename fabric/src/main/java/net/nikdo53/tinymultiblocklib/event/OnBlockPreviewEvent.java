package net.nikdo53.tinymultiblocklib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public class OnBlockPreviewEvent implements IOnBlockPreviewEvent {
    private PreviewMode previewMode;
    BlockLive center;
    Set<BlockLive> blockLiveSet;

    public OnBlockPreviewEvent(PreviewMode previewMode, BlockLive center, Set<BlockLive> blockLiveSet) {
        this.previewMode = previewMode;
        this.blockLiveSet = blockLiveSet;
        this.center = center;
    }

    @Override
    public PreviewMode getPreviewMode() {
        return previewMode;
    }

    @Override
    public void setPreviewMode(PreviewMode result) {
        this.previewMode = result;
    }

    @Override
    public boolean isCancelledInternal() {
        return false;
    }

    @Override
    public void setCancelledInternal(boolean canceled) {}

    @Override
    public BlockLive getCenterBlockLive() {
        return center;
    }

    @Override
    public @NonNull Set<BlockLive> getBlocksForPreview() {
        return blockLiveSet;
    }

    public static class Pre extends OnBlockPreviewEvent{
        boolean isCanceled;

        public Pre(PreviewMode previewMode, boolean isCancelled, BlockLive center, Set<BlockLive> blockLiveSet) {
            super(previewMode, center, blockLiveSet);
            this.isCanceled = isCancelled;
        }

        @Override
        public boolean isCancelledInternal() {
            return isCanceled;
        }

        @Override
        public void setCancelledInternal(boolean canceled) {
            this.isCanceled = canceled;
        }

        public boolean isCanceled() {
            return isCanceled;
        }

        public void cancel() {
            this.isCanceled = true;
        }

    }

    public static class Post extends OnBlockPreviewEvent{
        PoseStack poseStack;
        float partialTicks;
        SubmitNodeStorage submitNodeStorage;

        public Post(PreviewMode previewMode, BlockLive center, Set<BlockLive> blockLiveSet, PoseStack poseStack, float partialTicks, SubmitNodeStorage submitNodeStorage) {
            super(previewMode, center, blockLiveSet);

            this.poseStack = poseStack;
            this.partialTicks = partialTicks;
            this.submitNodeStorage = submitNodeStorage;
        }

        @Override
        public @NonNull Set<BlockLive> getBlocksForPreview() {
            return new HashSet<>(blockLiveSet);
        }

        public float getPartialTick() {
            return partialTicks;
        }

        public PoseStack getPoseStack() {
            return poseStack;
        }

        public SubmitNodeStorage getSubmitNodeStorage() {
            return submitNodeStorage;
        }

    }
    @FunctionalInterface
    public interface PreEvent {

        Event<PreEvent> EVENT = EventFactory.createArrayBacked(PreEvent.class,
                (listeners) -> (event) -> {
                    for (var listener : listeners) {
                        event = listener.onBlockPreview(event);

                        if (event.isCancelledInternal()) {
                            return event;
                        }
                    }

                    return event;
                }
        );

        OnBlockPreviewEvent.Pre onBlockPreview(OnBlockPreviewEvent.Pre event);

    }

    @FunctionalInterface
    public interface PostEvent {

        Event<PostEvent> EVENT = EventFactory.createArrayBacked(PostEvent.class,
                (listeners) -> (event) -> {
                    for (var listener : listeners) {
                        OnBlockPreviewEvent.Post onBlockPreviewEvent = listener.postBlockPreview(event);

                        if (onBlockPreviewEvent.isCancelledInternal()) {

                            onBlockPreviewEvent.setCancelledInternal(false);
                            Constants.LOGGER.error("OnBlockPreviewEvent.Post event cannot be cancelled, you might want the Pre event");
                        }
                    }

                    return event;
                }
        );

        OnBlockPreviewEvent.Post postBlockPreview(OnBlockPreviewEvent.Post event);

    }
}
