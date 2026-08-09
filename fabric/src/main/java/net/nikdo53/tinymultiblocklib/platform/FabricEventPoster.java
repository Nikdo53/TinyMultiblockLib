package net.nikdo53.tinymultiblocklib.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.BlockLive;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.event.OnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.platform.services.IEventPoster;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class FabricEventPoster implements IEventPoster {
    public static final FabricEventPoster INSTANCE = new FabricEventPoster();

    @Override
    public IOnBlockPreviewEvent onBlockPreviewPre(PreviewMode previewMode, boolean isCancelled, BlockLive center, Set<BlockLive> blockLiveSet, MultiBufferSource.BufferSource bufferSource) {
        return OnBlockPreviewEvent.PreEvent.EVENT.invoker().onBlockPreview(new OnBlockPreviewEvent.Pre(previewMode, isCancelled, center, blockLiveSet, bufferSource));
    }

    @Override
    public void onBlockPreviewPost(PreviewMode previewMode, BlockLive center, Set<BlockLive> blockLiveSet, PoseStack poseStack, float partialTicks, SubmitNodeStorage submitNodeStorage) {
        OnBlockPreviewEvent.PostEvent.EVENT.invoker().postBlockPreview(new OnBlockPreviewEvent.Post(previewMode, center, blockLiveSet, poseStack, partialTicks, submitNodeStorage));
    }

}
