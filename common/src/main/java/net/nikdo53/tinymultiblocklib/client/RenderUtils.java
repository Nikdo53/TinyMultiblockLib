package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class RenderUtils {


    public static int getPackedLight(Level level, BlockPos blockPos) {
        return LightTexture.pack(level.getBrightness(LightLayer.BLOCK, blockPos), level.getBrightness(LightLayer.SKY, blockPos));
    }


}
