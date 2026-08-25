package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AbstractMultiBlockEntity extends BlockEntity implements IMultiBlockEntity{
    private BlockPos offset;
    private boolean isPlaced;
    private MultiblockShape blockShapeCache = MultiblockShape.empty();
    private PreviewMode previewMode = PreviewMode.PLACED;

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
    public @NotNull CompoundTag getUpdateTag() {
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
    public Packet<ClientGamePacketListener> getUpdatePacket() {
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
    public MultiblockShape getFullBlockShapeCache() {
        return blockShapeCache;
    }

    @Override
    public void setFullBlockShapeCache(MultiblockShape blockPosList) {
        blockShapeCache = blockPosList;
    }

    @Override
    public void invalidateCaches() {
        blockShapeCache.getGlobalPositions().clear();
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
