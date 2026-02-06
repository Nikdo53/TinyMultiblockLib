package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.nikdo53.tinymultiblocklib.components.BlockLike;
import net.nikdo53.tinymultiblocklib.mixin.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class FakeClientLevel extends Level {
    ClientLevel originalLevel;
    public Set<BlockLike> blockLikeSet = new HashSet<>();

    protected FakeClientLevel(ClientLevel originalLevel, WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
        this.originalLevel = originalLevel;
    }

    public FakeClientLevel(ClientLevel level) {
        super(level.getLevelData(), level.dimension(), level.registryAccess(), level.dimensionTypeRegistration(), level::getProfiler, true, false, 1, 10);
        this.originalLevel = level;
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        blockLikeSet.add(new BlockLike(pos, state));
        return false;
    }

    @Override
    public void sendBlockUpdated(BlockPos blockPos, BlockState blockState, BlockState blockState1, int i) {

    }

    @Override
    public void playSeededSound(@Nullable Player player, double v, double v1, double v2, Holder<SoundEvent> holder, SoundSource soundSource, float v3, float v4, long l) {

    }

    @Override
    public void playSeededSound(@Nullable Player player, Entity entity, Holder<SoundEvent> holder, SoundSource soundSource, float v, float v1, long l) {

    }

    @Override
    public String gatherChunkSourceStats() {
        return "";
    }

    @Override
    public @Nullable Entity getEntity(int i) {
        return null;
    }

    @Override
    public TickRateManager tickRateManager() {
        return originalLevel.tickRateManager();
    }

    @Override
    public @Nullable MapItemSavedData getMapData(MapId mapId) {
        return null;
    }

    @Override
    public void setMapData(MapId mapId, MapItemSavedData mapItemSavedData) {

    }

    @Override
    public MapId getFreeMapId() {
        return originalLevel.getFreeMapId();
    }

    @Override
    public void destroyBlockProgress(int i, BlockPos blockPos, int i1) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return originalLevel.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return originalLevel.getRecipeManager();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return ((LevelAccessor) originalLevel).getEntities();
    }

    @Override
    public PotionBrewing potionBrewing() {
        return originalLevel.potionBrewing();
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return originalLevel.getBlockTicks();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return originalLevel.getFluidTicks();
    }

    @Override
    public ChunkSource getChunkSource() {
        return originalLevel.getChunkSource();
    }

    @Override
    public void levelEvent(@Nullable Player player, int i, BlockPos blockPos, int i1) {

    }

    @Override
    public void gameEvent(Holder<GameEvent> holder, Vec3 vec3, GameEvent.Context context) {

    }

    public void setDayTimePerTick(float dayTimePerTick){

    };

    public float getDayTimePerTick(){
        return 1f;
    }


    @Override
    public float getShade(Direction direction, boolean b) {
        return originalLevel.getShade(direction, b);
    }

    @Override
    public List<? extends Player> players() {
        return originalLevel.players();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int i, int i1, int i2) {
        return originalLevel.getUncachedNoiseBiome(i, i1, i2);
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return originalLevel.enabledFeatures();
    }
}
