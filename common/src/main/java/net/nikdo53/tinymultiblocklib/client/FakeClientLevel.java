package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.mixin.ClientLevelAccessorMixin;
import net.nikdo53.tinymultiblocklib.mixin.LevelAccessorMixin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BooleanSupplier;

public class FakeClientLevel extends ClientLevel {
    public static @Nullable FakeClientLevel INSTANCE = null;

    ClientLevel originalLevel;
    public Set<BlockLike> blockLikeSet = new HashSet<>();

    public FakeClientLevel(ClientLevel level) {
        super(((ClientLevelAccessorMixin) level).getConnection(),
                level.getLevelData(),
                level.dimension(),
                level.dimensionTypeRegistration(),
                1,
                1,
                level.getProfilerSupplier(),
                Minecraft.getInstance().levelRenderer,
                false,
                10
        );
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

    @Override
    public void sendPacketToServer(Packet<?> packet) {

    }

    @Override
    public void syncBlockState(BlockPos pos, BlockState state, Vec3 playerPos) {
    }

    @Override
    public void pollLightUpdates() {
    }

    @Override
    public void queueLightUpdate(Runnable task) {
    }

    @Override
    public void handleBlockChangedAck(int sequence) {
    }

    @Override
    public void setDayTime(long time) {
    }

    @Override
    public void setGameTime(long time) {
    }

    @Override
    public void tick(BooleanSupplier hasTimeLeft) {
    }

    @Override
    public void tickEntities() {
    }

    @Override
    public void tickNonPassenger(Entity p_entity) {
    }

    @Override
    public void unload(LevelChunk chunk) {
    }

    @Override
    public void onChunkLoaded(ChunkPos chunkPos) {
    }

    @Override
    public void clearTintCaches() {
    }

    @Override
    public void addPlayer(int playerId, AbstractClientPlayer playerEntity) {

    }

    @Override
    protected void addMapData(Map<String, MapItemSavedData> map) {
    }

    @Override
    public void addBlockEntityTicker(TickingBlockEntity ticker) {
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        return false;
    }

    @Override
    public void disconnect() {
    }

    @Override
    public void animateTick(int posX, int posY, int posZ) {
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
    }

    @Override
    public void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ) {
    }

    @Override
    public void setBlocksDirty(BlockPos blockPos, BlockState oldState, BlockState newState) {
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
    }

    @Override
    public void globalLevelEvent(int id, BlockPos pos, int data) {
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
    }

    @Override
    public void setSkyFlashTime(int timeFlash) {
    }

    @Override
    public void setDefaultSpawnPos(BlockPos spawnPos, float spawnAngle) {
    }

    @Override
    public void gameEvent(GameEvent event, Vec3 position, GameEvent.Context context) {

    }


    @Override
    public void setServerSimulationDistance(int serverSimulationDistance) {
    }

    @Override
    public void setServerVerifiedBlockState(BlockPos pos, BlockState state, int flags) {
    }

    @Override
    public List<AbstractClientPlayer> players() {
        return new ArrayList<>();
    }

    @Override
    public void setRainLevel(float strength) {
    }

    @Override
    public void setThunderLevel(float strength) {
    }

    @Override
    public void setSpawnSettings(boolean hostile, boolean peaceful) {
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
    }
}
