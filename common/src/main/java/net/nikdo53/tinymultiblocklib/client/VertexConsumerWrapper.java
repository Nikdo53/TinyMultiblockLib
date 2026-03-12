package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class VertexConsumerWrapper implements VertexConsumer
{
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent)
    {
        this.parent = parent;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        parent.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a)
    {
        parent.color(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v)
    {
        parent.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v)
    {
        parent.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v)
    {
        parent.uv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        parent.normal(x, y, z);
        return this;
    }

    @Override
    public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
        parent.vertex(x, y, z, red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
    }

    @Override
    public VertexConsumer color(float red, float green, float blue, float alpha) {
        parent.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer color(int colorARGB) {
        parent.color(colorARGB);
        return this;

    }

    @Override
    public VertexConsumer uv2(int lightmapUV) {
        parent.uv2(lightmapUV);
        return this;

    }

    @Override
    public VertexConsumer overlayCoords(int overlayUV) {
        parent.overlayCoords(overlayUV);
        return this;
    }

    @Override
    public void putBulkData(PoseStack.Pose poseEntry, BakedQuad quad, float red, float green, float blue, int combinedLight, int combinedOverlay) {
        parent.putBulkData(poseEntry, quad, red, green, blue, combinedLight, combinedOverlay);
    }

    @Override
    public void putBulkData(PoseStack.Pose poseEntry, BakedQuad quad, float[] colorMuls, float red, float green, float blue, int[] combinedLights, int combinedOverlay, boolean mulColor) {
        parent.putBulkData(poseEntry, quad, colorMuls, red, green, blue, combinedLights, combinedOverlay, mulColor);
    }

    @Override
    public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        parent.vertex(matrix, x, y, z);
        return this;

    }

    @Override
    public VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
        parent.normal(matrix, x, y, z);
        return this;
    }

    @Override
    public void endVertex()
    {
        parent.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a)
    {
        parent.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor()
    {
        parent.unsetDefaultColor();
    }
}
