package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import javax.annotation.Nullable;

public class AbstractMultiBlockEntity extends BlockEntity implements IMultiBlockEntity{
    public BlockPos center;
    public boolean isPlaced;
    public PreviewMode previewMode = PreviewMode.PLACED;

    public AbstractMultiBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.center = this.getBlockPos();
        this.isPlaced = false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("center", NbtUtils.writeBlockPos(this.center));
        tag.putBoolean("placed", this.isPlaced);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.center = NbtUtils.readBlockPos(tag.getCompound("center"));
        this.isPlaced = tag.getBoolean("placed");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public BlockPos getCenter() {
        return this.center;
    }

    @Override
    public void setCenter(BlockPos pos) {
        this.center = pos;
    }

    @Override
    public boolean isPlaced() {
        return isPlaced;
    }

    @Override
    public void setPlaced(boolean placed) {
        this.isPlaced = placed;
    }

    @Override
    public PreviewMode getPreviewMode() {
        return previewMode;
    }

    @Override
    public void setPreviewMode(PreviewMode mode) {
        this.previewMode = mode;
    }
}
