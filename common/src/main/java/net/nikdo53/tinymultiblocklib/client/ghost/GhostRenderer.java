package net.nikdo53.tinymultiblocklib.client.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.Constants;
import net.nikdo53.tinymultiblocklib.client.*;
import net.nikdo53.tinymultiblocklib.color.IColorSupplier;
import net.nikdo53.tinymultiblocklib.components.RenderOffsetType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GhostRenderer<T extends GhostRenderer<T>> {
    public static List<GhostRenderer<?>> RENDERERS = new ArrayList<>();

    protected Either<Vec3, BlockPos> posEither;
    protected int ticksRemaining;
    protected final int maxTicksRemaining;
    protected RenderOffsetType renderOffsetType = RenderOffsetType.SCALED;
    protected IColorSupplier colorStatic = new IColorSupplier.Simple(1, 1, 1, 1);
    protected @Nullable Integer packedLight = null;
    protected boolean shouldRender = true;

    protected float maxAlpha = 1;
    protected Integer fadeOutTicks = null;
    protected Pair<Double, Double> fadeDistanceAndStart = null;
    protected Consumer<PoseStack> poseStackConsumer = p -> {};

    public GhostRenderer(Vec3 position, int ticksRemaining) {
        this.posEither = Either.left(position);
        this.ticksRemaining = ticksRemaining;
        this.maxTicksRemaining = ticksRemaining;
    }

    public GhostRenderer(BlockPos blockPos, int ticksRemaining) {
        this.posEither = Either.right(blockPos);
        this.ticksRemaining = ticksRemaining;
        this.maxTicksRemaining = ticksRemaining;
    }

    protected Vec3 getPosition() {
        if (posEither.left().isPresent())
            return posEither.left().get();
        return Vec3.atLowerCornerOf(posEither.right().orElseThrow());
    }

    protected BlockPos getBlockPos() {
        if (posEither.right().isPresent())
            return posEither.right().get();

        Vec3 pos = posEither.left().orElseThrow();
        return BlockPos.containing(pos.x(), pos.y(), pos.z());
    }

    public void addToRenderList() {
        RENDERERS.add(this);
    }

    public static void renderAll(float partialTick, Camera camera, PoseStack poseStack){
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        ClientLevel level = minecraft.level;
        if (level == null) return;

        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);

        IColorSupplier.Mutable color = new IColorSupplier.Mutable(1, 1, 1, 1);
        MultiBufferSource.BufferSource tintedBuffer = new TintedBufferSource(buffer, color);

        List<GhostRenderer<?>> renderers = new ArrayList<>(RENDERERS);
        renderers.forEach(renderer -> renderer.prepareAndRender(partialTick, camera, level, poseStack, tintedBuffer, color));

        poseStack.popPose();
    }

    public static void tickAll(){
        RENDERERS.removeIf(renderer -> {
            renderer.ticksRemaining--;
            return renderer.ticksRemaining < 0;
        });
    }

    protected void prepareAndRender(float partialTick, Camera camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer, IColorSupplier.Mutable currentColor){
        shouldRender = true;
        currentColor.copy(this.colorStatic);

        if (fadeOutTicks != null) doTimeFade(partialTick, currentColor);
        if (fadeDistanceAndStart != null) doDistanceFade(fadeDistanceAndStart.getFirst(), fadeDistanceAndStart.getSecond(), camera.getPosition(), currentColor);

        if (!shouldRender)
            return;

        poseStack.pushPose();
        Vec3 position = getPosition();
        poseStack.translate(position.x(), position.y(), position.z());
        renderOffsetType.applyTransforms(poseStack);
        poseStackConsumer.accept(poseStack);

        render(partialTick, camera, level, poseStack, buffer);

        buffer.endLastBatch();
        poseStack.popPose();
    }

    private void doTimeFade(float partialTick, IColorSupplier.Mutable currentColor) {
        if (ticksRemaining <= fadeOutTicks){
            float smoothTicks = (ticksRemaining - partialTick) /  fadeOutTicks;
            if (smoothTicks < 0) smoothTicks = 0;

            currentColor.setAlpha(smoothTicks * currentColor.getAlpha());
        }
    }

    private void doDistanceFade(double maxDistance, double fadeStart, Vec3 cameraPos, IColorSupplier.Mutable currentColor) {
        double camDistance = cameraPos.distanceTo(getPosition());
        if (camDistance >= maxDistance) {
             shouldRender = false;
            return;
        }
        if (camDistance > fadeStart){
            double fullRange = maxDistance - fadeStart; // 3
            double positionInRange = maxDistance - camDistance; // 2
            double alphaFactor =  positionInRange / fullRange; // 0.66

            currentColor.setAlpha((float) alphaFactor * currentColor.getAlpha());
        }
    }

    protected abstract void render(float partialTick, Camera camera, ClientLevel level, PoseStack poseStack, MultiBufferSource.BufferSource buffer);

    protected int getLight(){
        Minecraft minecraft = Minecraft.getInstance();
        return packedLight != null ? packedLight : minecraft.levelRenderer.getLightColor(minecraft.level, getBlockPos());
    }
    public T setRenderOffsetType(RenderOffsetType renderOffsetType) {
        this.renderOffsetType = renderOffsetType;
        return cast();
    }

    public T setARGB(float red, float green, float blue, float alpha) {
        colorStatic = new IColorSupplier.Simple(red, green, blue, alpha);
        return cast();
    }

    public T setLight(int packedLight){
        this.packedLight = packedLight;
        return cast();
    }


    public T transform(Consumer<PoseStack> poseStackConsumer){
        this.poseStackConsumer = poseStackConsumer;
        return cast();
    }

    public T enableTimeFade(int fadeOutTicks) {
        if (fadeOutTicks <= ticksRemaining){
            this.fadeOutTicks = fadeOutTicks;
        } else {
            Constants.LOGGER.error("{} fadeOutTicks can't be larger than remaining ticks", this);
        }
        return cast();
    }

    public T enableDistanceFade(double maxDistance, double fadeStart) {
        if (maxDistance >= fadeStart){
            this.fadeDistanceAndStart = new Pair<>(maxDistance, fadeStart);
        } else {
            Constants.LOGGER.error("{} Fade cant start further from max maxDistance", this);
        }
        return cast();
    }

    @SuppressWarnings("unchecked")
    public T cast(){
        return (T) this;
    }
}
