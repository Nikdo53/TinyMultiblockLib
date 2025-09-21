package net.nikdo53.tinymultiblocklib.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class TMBLClientEvents {
    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(TMBLClientEvents::renderLevelStageEvent);
    }

    private static void renderLevelStageEvent(WorldRenderContext context) {
        MultiblockPreviewRenderer.renderMultiblockPreviews(context.tickDelta(), Minecraft.getInstance(), context.world(), context.camera(), context.matrixStack());
    }
}
