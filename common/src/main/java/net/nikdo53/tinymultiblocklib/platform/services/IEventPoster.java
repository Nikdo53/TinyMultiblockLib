package net.nikdo53.tinymultiblocklib.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import java.util.Set;

public interface IEventPoster {

    IOnBlockPreviewEvent onBlockPreviewPre(PreviewMode previewMode, boolean isCancelled, BlockLive center, Set<BlockLive> blockLiveSet);

    void onBlockPreviewPost(PreviewMode previewMode, BlockLive center, Set<BlockLive> blockLiveSet, PoseStack poseStack, float partialTicks, MultiBufferSource.BufferSource bufferSource);

}
