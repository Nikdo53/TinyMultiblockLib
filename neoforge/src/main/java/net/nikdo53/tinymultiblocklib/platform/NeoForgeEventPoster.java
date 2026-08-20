package net.nikdo53.tinymultiblocklib.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.common.MinecraftForge;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.event.OnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.platform.services.IEventPoster;

import java.nio.Buffer;
import java.util.Set;

public class NeoForgeEventPoster implements IEventPoster {
    public static final NeoForgeEventPoster INSTANCE = new NeoForgeEventPoster();

    @Override
    public IOnBlockPreviewEvent onBlockPreviewPre(PreviewMode previewMode, boolean isCancelled, BlockLive center, Set<BlockLive> blockLiveSet) {
        OnBlockPreviewEvent.Pre event = new OnBlockPreviewEvent.Pre(previewMode, isCancelled, center, blockLiveSet);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    @Override
    public void onBlockPreviewPost(PreviewMode previewMode, BlockLive center, Set<BlockLive> blockLiveSet, PoseStack poseStack, float partialTicks, MultiBufferSource.BufferSource bufferSource) {
        MinecraftForge.EVENT_BUS.post(new OnBlockPreviewEvent.Post(previewMode, center, blockLiveSet, poseStack, partialTicks, bufferSource));
    }

}
