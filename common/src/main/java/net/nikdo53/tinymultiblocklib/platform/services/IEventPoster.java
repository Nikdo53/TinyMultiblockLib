package net.nikdo53.tinymultiblocklib.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jetbrains.annotations.Nullable;

public interface IEventPoster {

    IOnBlockPreviewEvent onBlockPreviewPre(PreviewMode previewMode, boolean isCancelled, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack);

    void onBlockPreviewPost(PreviewMode previewMode, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack);

}
