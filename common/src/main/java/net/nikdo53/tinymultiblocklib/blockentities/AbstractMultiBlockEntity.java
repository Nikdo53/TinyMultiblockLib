package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.block.shape.MultiblockShape;

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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("offset", BlockPos.CODEC, this.offset);
        output.putBoolean("placed", this.isPlaced);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(new ProblemReporter.ScopedCollector(Constants.LOGGER), registries);
        saveAdditional(output);
        return output.buildResult();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.offset = input.read("offset", BlockPos.CODEC).orElseGet(() -> this.offset);
        this.isPlaced = input.getBooleanOr("placed", this.isPlaced());

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
