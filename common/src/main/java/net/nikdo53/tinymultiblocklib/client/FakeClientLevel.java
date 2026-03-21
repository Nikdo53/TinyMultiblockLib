package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.mixin.ClientLevelAccessorMixin;
import net.nikdo53.tinymultiblocklib.mixin.LevelAccessorMixin;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FakeClientLevel extends ClientLevel {
    public static @Nullable FakeClientLevel INSTANCE = null;

    ClientLevel originalLevel;
    public Set<BlockLike> blockLikeSet = new HashSet<>();

    public FakeClientLevel(ClientLevel level) {
        super(((ClientLevelAccessorMixin) level).getConnection(), level.getLevelData(), level.dimension(), level.dimensionTypeRegistration(), 1, 1, Minecraft.getInstance().levelRenderer, false, 67, 67);
        this.originalLevel = level;

        setClientSide(false);

    }

    public void setClientSide(boolean isClientSide) {
        ((LevelAccessorMixin) this).tmbl$setClientSide(isClientSide);
    }

    public static FakeClientLevel getOrThrow() {
        FakeClientLevel instance = INSTANCE;
        if (instance == null) {
            throw new IllegalStateException("Tried accessing FakeClientLevel before any ClientLevel was initialized.");
        }
        instance.originalLevel = Minecraft.getInstance().level;
        instance.blockLikeSet.clear();

        return instance;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        BlockState blockState = super.getBlockState(pos);
        if(!blockState.canOcclude()) {
            Optional<BlockLike> previewed = blockLikeSet.stream().filter(like -> like.pos.equals(pos)).findAny();

            if (previewed.isPresent()){
                return previewed.get().state;
            }
        }
        return blockState;
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        blockLikeSet.add(new BlockLike(pos, state));
        return false;
    }

}
