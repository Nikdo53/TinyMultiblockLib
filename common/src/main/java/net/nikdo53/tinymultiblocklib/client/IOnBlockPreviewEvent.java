package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.platform.Services;
import org.jetbrains.annotations.Nullable;

public interface IOnBlockPreviewEvent {
    PreviewMode getPreviewMode();
    void setPreviewMode(PreviewMode result);

    boolean isCancelledInternal();
    void setCancelledInternal(boolean canceled);

    BlockState getBlockState();
    void setBlockState(BlockState state);

    Block getBlock();
    BlockPos getPos();
    LocalPlayer getPlayer();

    @Nullable
    BlockEntity getBlockEntity();

    float getPartialTick();
    PoseStack getPoseStack();

    static IOnBlockPreviewEvent firePreEvent(PreviewMode previewMode, boolean isCancelled, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack) {
       return Services.PLATFORM.getEventPoster().onBlockPreviewPre(previewMode, isCancelled, state, pos, player, blockEntity, partialTicks, poseStack);
    }

    static void firePostEvent(PreviewMode previewMode, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack){
        Services.PLATFORM.getEventPoster().onBlockPreviewPost(previewMode, state, pos, player, blockEntity, partialTicks, poseStack);
    }

}
