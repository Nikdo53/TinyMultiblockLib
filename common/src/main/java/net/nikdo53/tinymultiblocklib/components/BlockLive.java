package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.nikdo53.tinymultiblocklib.Constants;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class BlockLive {
    public BlockPos pos;
    public BlockState state;

    /**
     * A Blocklike that saves a block entity tag. Can be saved and moved around. Entity cannot be rendered in a preview
     */
    public BlockLive(BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
    }

    public BlockLive(BlockGetter level, BlockPos pos) {
        this(pos, level.getBlockState(pos));
    }

    public void move(Level level, BlockPos offset){
        BlockPos posNew = pos.offset(offset);
        level.setBlockAndUpdate(posNew, state);
    }

    public static HolderGetter<Block> getBlockGetter(@Nullable LevelReader level) {
       return level != null ? level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK;
    }

    public BlockLive.Live addBlockEntity(BlockEntity entity){
        return new BlockLive.Live(pos, state, entity);
    }

    public BlockLive.Live replaceBlockEntity(BlockEntity entity){
        return new BlockLive.Live(pos, state, entity);
    }

    public static class Tag extends BlockLive{
        public @NonNull CompoundTag blockEntityTag;

        /**
         * A Blocklike that saves a block entity tag. Can be saved and moved around. Entity cannot be rendered in a preview
         */
        public Tag(BlockPos pos, BlockState state, @NonNull BlockEntity blockEntity) {
            super(pos, state);

            TagValueOutput output = TagValueOutput.createWithContext(new ProblemReporter.ScopedCollector(Constants.LOGGER), blockEntity.getLevel().registryAccess());
            blockEntity.saveWithId(output);
            this.blockEntityTag = output.buildResult();
        }

        public Tag(BlockPos pos, BlockState state, @NonNull CompoundTag tag) {
            super(pos, state);
            this.blockEntityTag = tag;
        }

        public Tag(BlockGetter level, BlockPos pos) {
            this(pos, level.getBlockState(pos), Objects.requireNonNull(level.getBlockEntity(pos)));
        }

        @Override
        public void move(Level level, BlockPos offset) {
            super.move(level, offset);

            BlockPos posNew = pos.offset(offset);

            BlockEntity blockEntity = BlockEntity.loadStatic(posNew, state, blockEntityTag, level.registryAccess());
            if (blockEntity != null)
                level.setBlockEntity(blockEntity);
        }

        @Override
        public Live addBlockEntity(BlockEntity entity) {
            throw new RuntimeException("Adding BlockEntity to Tag is not supported! Use replaceBlockEntity instead.");
        }

    }

    /**
     * A Blocklike with a live block entity which can be rendered
     */
    public static class Live extends BlockLive{
        public final BlockEntity blockEntity;

        public Live(BlockPos pos, BlockState state, @NonNull BlockEntity blockEntity) {
            super(pos, state);
            this.blockEntity = blockEntity;
        }

        public Live(BlockGetter level, BlockPos pos) {
            this(pos, level.getBlockState(pos), Objects.requireNonNull(level.getBlockEntity(pos)));
        }

        @Override
        public void move(Level level, BlockPos offset) {
            throw new RuntimeException("Moving Live BlockEntity is not supported!");
        }

        @Override
        public Live addBlockEntity(BlockEntity entity) {
            return this;
        }
    }

}
