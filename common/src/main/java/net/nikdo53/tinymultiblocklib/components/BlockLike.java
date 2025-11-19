package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.SerializationUtils;

import javax.annotation.Nullable;

public class BlockLike {
    public BlockPos pos;
    public BlockState state;
    public @Nullable BlockEntity blockEntity;

    public BlockLike(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        this.pos = pos;
        this.state = state;
        this.blockEntity = blockEntity;
    }

    public BlockLike(BlockPos pos, BlockState state) {
        new BlockLike(pos, state, null);
    }

    public static BlockLike fromPos(BlockGetter level, BlockPos pos){
        return new BlockLike(pos, level.getBlockState(pos), level.getBlockEntity(pos));
    }

    public void place(Level level){
        level.setBlockAndUpdate(pos, state);
        if (blockEntity != null) level.setBlockEntity(blockEntity);
    }

    public CompoundTag saveToTag(HolderLookup.Provider registries, @Nullable BlockEntity thisBlockEntity, boolean writePos) {
        CompoundTag tag = new CompoundTag();

        tag.put("state", NbtUtils.writeBlockState(state));
        if (writePos)
            tag.put("pos", NbtUtils.writeBlockPos(pos));
        if (canSaveBE(thisBlockEntity))
            tag.put("be", blockEntity.saveWithFullMetadata(registries));

        return tag;
    }

    public static BlockLike fromTag(CompoundTag tag, HolderLookup.Provider registries, @Nullable LevelReader level, @Nullable BlockPos fallbackPos) {
        BlockState state1 = NbtUtils.readBlockState(getBlockGetter(level), tag);
        BlockPos pos1;
        BlockEntity blockEntity1 = null;

        if (NbtUtils.readBlockPos(tag, "pos").isPresent()) {
            pos1 = NbtUtils.readBlockPos(tag, "pos").get();
        } else {
            pos1 = fallbackPos;
        }

        if (tag.contains("be")) {
            if (pos1 == null) {
                throw new IllegalArgumentException("BlockEntity can't be created without a BlockPos");
            }

           blockEntity1 = BlockEntity.loadStatic(pos1, state1, tag, registries);
        }

        return new BlockLike(pos1, state1, blockEntity1);
    }

    public static HolderGetter<Block> getBlockGetter(@Nullable LevelReader level) {
       return level != null ? level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
    }

    //Prevents StackOverflows
    public boolean canSaveBE(BlockEntity blockEntity) {
        return this.blockEntity != null && blockEntity != this.blockEntity;
    }

}
