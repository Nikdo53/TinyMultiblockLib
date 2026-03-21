package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.components.PreviewMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AbstractMultiBlockEntity extends BlockEntity implements IMultiBlockEntity{
    private BlockPos offset;
    private boolean isPlaced;
    private List<BlockPos> BLOCK_SHAPE_CACHE = new ArrayList<>();

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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
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
    public List<BlockPos> getFullBlockShapeCache() {
        return BLOCK_SHAPE_CACHE;
    }

    @Override
    public void setFullBlockShapeCache(List<BlockPos> blockPosList) {
        BLOCK_SHAPE_CACHE = blockPosList;
    }

    @Override
    public void invalidateCaches() {
        BLOCK_SHAPE_CACHE.clear();
    }
}
