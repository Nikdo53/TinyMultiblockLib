package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockLike {
    public BlockPos pos;
    public BlockState state;
    public @Nullable CompoundTag blockEntityTag;

    public BlockLike(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntityTag) {
        this.pos = pos;
        this.state = state;
        this.blockEntityTag = blockEntityTag == null ? null : blockEntityTag.saveWithoutMetadata(blockEntityTag.getLevel().registryAccess());
    }

    public BlockLike(BlockPos pos, BlockState state) {
        new BlockLike(pos, state, null);
    }

    public static BlockLike fromPos(BlockGetter level, BlockPos pos){
        return new BlockLike(pos, level.getBlockState(pos), level.getBlockEntity(pos));
    }

    public void move(Level level, BlockPos offset){
        BlockPos posNew = pos.offset(offset);

        level.setBlockAndUpdate(posNew, state);

        if (blockEntityTag != null) {
            BlockEntity blockEntity = BlockEntity.loadStatic(posNew, state, blockEntityTag, level.registryAccess());
            if (blockEntity != null) level.setBlockEntity(blockEntity);
        }
    }

    public static HolderGetter<Block> getBlockGetter(@Nullable LevelReader level) {
       return level != null ? level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
    }
}
