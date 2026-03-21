package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (oldBlockState != null)
            output.store("blockState", BlockState.CODEC, oldBlockState);

    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        oldBlockState = input.read("blockState", BlockState.CODEC).orElseGet(() -> this.oldBlockState);

    }

}
