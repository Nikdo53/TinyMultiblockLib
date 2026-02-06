package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.mixin.ClientLevelAccessor;

import java.util.HashSet;
import java.util.Set;

public class FakeClientLevel extends ClientLevel {
    ClientLevel originalLevel;
    public Set<BlockLike> blockLikeSet = new HashSet<>();

    public FakeClientLevel(ClientLevel level) {
        super(((ClientLevelAccessor) level).getConnection(), level.getLevelData(), level.dimension(), level.dimensionTypeRegistration(), 1, 1, level.getProfilerSupplier(), Minecraft.getInstance().levelRenderer, false, 10);
        this.originalLevel = level;
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        blockLikeSet.add(new BlockLike(pos, state));
        return false;
    }

}
