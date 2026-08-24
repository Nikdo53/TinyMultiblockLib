package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Set;

public interface IOnBlockPreviewEvent {
    PreviewMode getPreviewMode();
    void setPreviewMode(PreviewMode result);

    boolean isCancelledInternal();
    void setCancelledInternal(boolean canceled);

    BlockLive getCenterBlockLive();
    @Nonnull
    Set<BlockLive> getBlocksForPreview();

    default BlockState getCenterBlockState() {
        return getCenterBlockLive().state;
    }

    default BlockPos getCenter() {
        return getCenterBlockLive().pos;
    }

    @Nullable
    default BlockEntity getCenterBlockEntity() {
        return getCenterBlockLive() instanceof BlockLive.Live live ? live.blockEntity : null;
    }


    static IOnBlockPreviewEvent firePreEvent(PreviewMode previewMode, boolean isCancelled, BlockLive center, Set<BlockLive> blockLiveSet) {
       return Services.PLATFORM.getEventPoster().onBlockPreviewPre(previewMode, isCancelled, center, blockLiveSet);
    }

    static void firePostEvent(PreviewMode previewMode, BlockLive center, Set<BlockLive> blockLiveSet, PoseStack poseStack, float partialTicks, MultiBufferSource.BufferSource bufferSource){
        Services.PLATFORM.getEventPoster().onBlockPreviewPost(previewMode, center, blockLiveSet, poseStack, partialTicks, bufferSource);
    }

}
