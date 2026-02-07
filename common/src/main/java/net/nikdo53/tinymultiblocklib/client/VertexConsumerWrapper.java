package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class VertexConsumerWrapper implements VertexConsumer{
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent) {
        this.parent = parent;
    }

    @Override
    public VertexConsumer setNormal(PoseStack.Pose pose, float normalX, float normalY, float normalZ) {
        parent.setNormal(pose, normalX, normalY, normalZ);
        return this;

    }

    @Override
    public VertexConsumer addVertex(Matrix4f pose, float x, float y, float z) {
        parent.addVertex(pose, x, y, z);
        return this;
    }

    @Override
    public VertexConsumer addVertex(PoseStack.Pose pose, float x, float y, float z) {
        parent.addVertex(pose, x, y, z);
        return this;
    }

    @Override
    public VertexConsumer addVertex(PoseStack.Pose pose, Vector3f pos) {
        parent.addVertex(pose, pos);
        return this;
    }

    @Override
    public VertexConsumer addVertex(Vector3f pos) {
        parent.addVertex(pos);
        return this;
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha) {
        parent.putBulkData(pose, quad, brightness, red, green, blue, alpha, lightmap, packedOverlay, readAlpha);
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
        parent.putBulkData(pose, quad, red, green, blue, alpha, packedLight, packedOverlay);
    }

    @Override
    public VertexConsumer setOverlay(int packedOverlay) {
        parent.setOverlay(packedOverlay);
        return this;

    }

    @Override
    public VertexConsumer setLight(int packedLight) {
        parent.setLight(packedLight);
        return this;
    }

    @Override
    public VertexConsumer setWhiteAlpha(int alpha) {
        parent.setWhiteAlpha(alpha);
        return this;

    }

    @Override
    public VertexConsumer setColor(int color) {
        parent.setColor(color);
        return this;

    }

    @Override
    public VertexConsumer setColor(float red, float green, float blue, float alpha) {
        parent.setColor(red, green, blue, alpha);
        return this;
    }

    @Override
    public void addVertex(float x, float y, float z, int color, float u, float v, int packedOverlay, int packedLight, float normalX, float normalY, float normalZ) {
        parent.addVertex(x, y, z, color,  u, v, packedOverlay, packedLight, normalX, normalY, normalZ);
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        this.parent.setColor(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        this.parent.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        this.parent.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        this.parent.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        this.parent.setNormal(x, y, z);
        return this;
    }


}
