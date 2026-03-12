package net.nikdo53.tinymultiblocklib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jetbrains.annotations.Nullable;

public class OnBlockPreviewEvent extends Event implements IOnBlockPreviewEvent {
    private PreviewMode previewMode;
    BlockState state;
    BlockPos pos;
    Block block;
    LocalPlayer player;
    @Nullable
    BlockEntity blockEntity;
    float partialTicks;
    PoseStack poseStack;

    public OnBlockPreviewEvent(PreviewMode previewMode, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack) {
        this.previewMode = previewMode;
        this.state = state;
        this.pos = pos;
        this.block = state.getBlock();
        this.player = player;
        this.blockEntity = blockEntity;
        this.partialTicks = partialTicks;
        this.poseStack = poseStack;
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
    public void setCancelledInternal(boolean canceled) {

    }

    @Override
    public BlockState getBlockState() {
        return state;
    }

    @Override
    public void setBlockState(BlockState state) {
        this.state = state;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public LocalPlayer getPlayer() {
        return player;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public float getPartialTick() {
        return partialTicks;
    }

    @Override
    public PoseStack getPoseStack() {
        return poseStack;
    }

    @Cancelable
    public static class Pre extends OnBlockPreviewEvent {
        public Pre(PreviewMode previewMode, boolean isCancelled, BlockState state, BlockPos pos, LocalPlayer player, BlockEntity blockEntity, float partialTicks, PoseStack poseStack) {
            super(previewMode, state, pos, player, blockEntity, partialTicks, poseStack);

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
        public Post(PreviewMode previewMode, BlockState state, BlockPos pos, LocalPlayer player, BlockEntity blockEntity, float partialTicks, PoseStack poseStack) {
            super(previewMode, state, pos, player, blockEntity, partialTicks, poseStack);
        }
    }
}
