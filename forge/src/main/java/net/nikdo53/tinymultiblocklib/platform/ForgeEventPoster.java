package net.nikdo53.tinymultiblocklib.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.event.OnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import net.nikdo53.tinymultiblocklib.platform.services.IEventPoster;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ForgeEventPoster implements IEventPoster {
    public static final ForgeEventPoster INSTANCE = new ForgeEventPoster();

    @Override
    public IOnBlockPreviewEvent onBlockPreviewPre(PreviewMode previewMode, boolean isCancelled, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack, Set<BlockLike> blockLikeSet) {
        OnBlockPreviewEvent.Pre event = new OnBlockPreviewEvent.Pre(previewMode, isCancelled, state, pos, player, blockEntity, partialTicks, poseStack, blockLikeSet);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    @Override
    public void onBlockPreviewPost(PreviewMode previewMode, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack, Set<BlockLike> blockLikeSet) {
        MinecraftForge.EVENT_BUS.post(new OnBlockPreviewEvent.Post(previewMode, state, pos, player, blockEntity, partialTicks, poseStack, blockLikeSet));
    }
}
