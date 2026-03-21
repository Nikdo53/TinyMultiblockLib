package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.*;

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

    @Override
    public void putBulkData(PoseStack.Pose p_85988_, BakedQuad p_85989_, float[] p_331397_, float p_85990_, float p_85991_, float p_85992_, float p_331416_, int[] p_331378_, int p_85993_) {
        this.parent.putBulkData(p_85988_, p_85989_, p_331397_, p_85990_, p_85991_, p_85992_, p_331416_, p_331378_, p_85993_);
    }

    @Override
    public VertexConsumer addVertex(Vector3fc p_458106_) {
         this.parent.addVertex(p_458106_);
        return this;
    }

    @Override
    public VertexConsumer addVertex(Matrix4fc p_458205_, float p_457830_, float p_457564_, float p_457823_) {
         this.parent.addVertex(p_458205_, p_457830_, p_457564_, p_457823_);
        return this;

    }

    @Override
    public VertexConsumer addVertexWith2DPose(Matrix3x2fc p_457647_, float p_415815_, float p_416074_) {
         this.parent.addVertexWith2DPose(p_457647_, p_415815_, p_416074_);
        return this;

    }

    @Override
    public VertexConsumer setNormal(PoseStack.Pose pose, Vector3f normalVector) {
         this.parent.setNormal(pose, normalVector);
        return this;

    }

    @Override
    public VertexConsumer setLineWidth(float p_456188_) {
        parent.setLineWidth(p_456188_);
        return this;
    }
}
