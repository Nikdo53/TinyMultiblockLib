package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IOnBlockPreviewEvent {
    PreviewMode getPreviewMode();
    void setPreviewMode(PreviewMode result);

    boolean isCancelledInternal();
    void setCancelledInternal(boolean canceled);

    BlockState getBlockState();
    BlockPos getCenter();

    LocalPlayer getPlayer();

    @Nullable
    BlockEntity getBlockEntity();

    float getPartialTick();
    PoseStack getPoseStack();

    Set<BlockLike> getBlocksForPreview();

    static IOnBlockPreviewEvent firePreEvent(PreviewMode previewMode, boolean isCancelled, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack, Set<BlockLike> blockLikeSet) {
       return Services.PLATFORM.getEventPoster().onBlockPreviewPre(previewMode, isCancelled, state, pos, player, blockEntity, partialTicks, poseStack, blockLikeSet);
    }

    static void firePostEvent(PreviewMode previewMode, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack, Set<BlockLike> blockLikeSet){
        Services.PLATFORM.getEventPoster().onBlockPreviewPost(previewMode, state, pos, player, blockEntity, partialTicks, poseStack, blockLikeSet);
    }

}
