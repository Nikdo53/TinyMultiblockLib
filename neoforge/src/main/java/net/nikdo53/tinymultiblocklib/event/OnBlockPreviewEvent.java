package net.nikdo53.tinymultiblocklib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class OnBlockPreviewEvent extends Event implements IOnBlockPreviewEvent {
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
    public Set<BlockLive> getBlocksForPreview() {
        return blockLiveSet;
    }

    public static class Pre extends OnBlockPreviewEvent implements ICancellableEvent{
        public Pre(PreviewMode previewMode, boolean isCancelled, BlockLive center, Set<BlockLive> blockLiveSet) {
            super(previewMode, center, blockLiveSet);

            if (isCancelled){
                setCanceled(true);
            }
        }

        @Override
        public boolean isCancelledInternal() {
            return isCanceled();
        }

        @Override
        public void setCancelledInternal(boolean canceled) {
            setCanceled(canceled);
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
        public Set<BlockLive> getBlocksForPreview() {
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
}
