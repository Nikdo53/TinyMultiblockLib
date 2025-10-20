package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;

import java.util.ArrayList;
import java.util.List;

public class AbstractMultiBlockEntity extends BlockEntity implements IMultiBlockEntity{
    public BlockPos offset;
    public boolean isPlaced;
    public PreviewMode previewMode = PreviewMode.PLACED;
    public List<BlockPos> BLOCK_SHAPE_CACHE = new ArrayList<>();

    public AbstractMultiBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.offset = new BlockPos(0,0,0);
        this.isPlaced = false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("offset", NbtUtils.writeBlockPos(this.offset));
        tag.putBoolean("placed", this.isPlaced);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.offset = NbtUtils.readBlockPos(tag, "offset").orElseGet(this::getBlockPos);
        this.isPlaced = tag.getBoolean("placed");

        if (tag.contains("center")) // For maintaining compatibility with TMBL < 2.1
            setCenter(NbtUtils.readBlockPos(tag, "center").orElseGet(this::getBlockPos));
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

    /**
     * Certain mods let you change the location of the multiblock (like Carry on)
     * <p>
     * That's a problem because the {@link #center} won't update. This should trick it into updating
     */
/*    @Override
    public void setBlockState(BlockState blockState) {
        if (IMultiBlock.isCenter(blockState)){
            setCenter(this.getBlockPos());
        }
        super.setBlockState(blockState);
    }*/
}
