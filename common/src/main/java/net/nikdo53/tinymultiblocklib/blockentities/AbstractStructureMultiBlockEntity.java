package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.BlockLike;

public class AbstractStructureMultiBlockEntity extends AbstractMultiBlockEntity implements IStructureMultiBlockEntity{
    private BlockState oldBlockState = Blocks.AIR.defaultBlockState();

    public AbstractStructureMultiBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BlockState getOldBlockState() {
        return oldBlockState;
    }

    @Override
    public void setOldBlockState(BlockState blockState) {
        oldBlockState = blockState;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (oldBlockState != null) tag.put("blockState", NbtUtils.writeBlockState(oldBlockState));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        oldBlockState = NbtUtils.readBlockState(BlockLike.getBlockGetter(getLevel()), tag.getCompound("blockState"));
    }
}
