package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RenderUtils {
    /**
     * Used to pass side checking (like in BlockRenderDispatcher#renderBatched) into renderSingleBlock,
     * as the former doesn't work with iris for some reason
     */
    public static @Nullable CheckSidesContext CHECK_SIDES_CONTEXT = null;

    public static int getPackedLight(Level level, BlockPos blockPos) {
        return LightTexture.pack(level.getBrightness(LightLayer.BLOCK, blockPos), level.getBrightness(LightLayer.SKY, blockPos));
    }


    public record CheckSidesContext(Level level, BlockState state, BlockPos pos) {
    }
}
