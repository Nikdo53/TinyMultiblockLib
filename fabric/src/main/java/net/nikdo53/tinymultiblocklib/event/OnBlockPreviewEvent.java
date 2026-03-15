package net.nikdo53.tinymultiblocklib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.client.IOnBlockPreviewEvent;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class OnBlockPreviewEvent implements IOnBlockPreviewEvent {
    private PreviewMode previewMode;
    BlockState state;
    BlockPos pos;
    Block block;
    LocalPlayer player;
    @Nullable
    BlockEntity blockEntity;
    float partialTicks;
    PoseStack poseStack;
    Set<BlockLike> blockLikeSet;
    boolean cancelled;

    public OnBlockPreviewEvent(PreviewMode previewMode, boolean isCancelled, BlockState state, BlockPos pos, LocalPlayer player, @Nullable BlockEntity blockEntity, float partialTicks, PoseStack poseStack, Set<BlockLike> blockLikeSet) {
        this.previewMode = previewMode;
        this.state = state;
        this.pos = pos;
        this.block = state.getBlock();
        this.player = player;
        this.blockEntity = blockEntity;
        this.partialTicks = partialTicks;
        this.poseStack = poseStack;
        this.blockLikeSet = blockLikeSet;
        this.cancelled = isCancelled;
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
        return cancelled;
    }

    @Override
    public void setCancelledInternal(boolean canceled) {
        this.cancelled = canceled;
    }

    public void cancel(){
        this.cancelled = true;
    }

    @Override
    public BlockState getBlockState() {
        return state;
    }

    @Override
    public BlockPos getCenter() {
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

    @Override
    public Set<BlockLike> getBlocksForPreview() {
        return blockLikeSet;
    }

    @FunctionalInterface
    public interface Pre {

        Event<OnBlockPreviewEvent.Pre> EVENT = EventFactory.createArrayBacked(OnBlockPreviewEvent.Pre.class,
                (listeners) -> (event) -> {
                    for (var listener : listeners) {
                        event = listener.onBlockPreview(event);

                        if (event.isCancelledInternal()) {
                            return event;
                        }
                    }

                    return event;
                }
        );

        OnBlockPreviewEvent onBlockPreview(OnBlockPreviewEvent event);

    }

    @FunctionalInterface
    public interface Post {

        Event<OnBlockPreviewEvent.Post> EVENT = EventFactory.createArrayBacked(OnBlockPreviewEvent.Post.class,
                (listeners) -> (event) -> {
                    for (var listener : listeners) {
                        OnBlockPreviewEvent onBlockPreviewEvent = listener.postBlockPreview(event);

                        if (onBlockPreviewEvent.isCancelledInternal()) {

                            onBlockPreviewEvent.setCancelledInternal(false);
                            Constants.LOGGER.error("OnBlockPreviewEvent.Post event cannot be cancelled, you might want the Pre event");
                        }
                    }

                    return event;
                }
        );

        OnBlockPreviewEvent postBlockPreview(OnBlockPreviewEvent event);

    }
}
