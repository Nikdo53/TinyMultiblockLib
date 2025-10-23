package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AbstractMultiBlockEntity extends BlockEntity implements IMultiBlockEntity{
    private BlockPos offset;
    private boolean isPlaced;
    private PreviewMode previewMode = PreviewMode.PLACED;
    private List<BlockPos> BLOCK_SHAPE_CACHE = new ArrayList<>();

    public AbstractMultiBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.offset = new BlockPos(0,0,0);
        this.isPlaced = false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("offset", NbtUtils.writeBlockPos(this.offset));
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
        this.offset = NbtUtils.readBlockPos(tag.getCompound("offset"));
        this.isPlaced = tag.getBoolean("placed");

        if (tag.contains("center")) // For maintaining compatibility with TMBL < 2.1
            setCenter(NbtUtils.readBlockPos(tag.getCompound("center")));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public BlockPos getOffset() {
        return offset;
    }

    @Override
    public void setOffset(BlockPos offset) {
       this.offset = offset;
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
    public List<BlockPos> getFullBlockShapeCache() {
        return BLOCK_SHAPE_CACHE;
    }

    @Override
    public void setFullBlockShapeCache(List<BlockPos> blockPosList) {
        BLOCK_SHAPE_CACHE = blockPosList;
    }

    @Override
    public void invalidateCaches() {
        BLOCK_SHAPE_CACHE = new ArrayList<>();
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
