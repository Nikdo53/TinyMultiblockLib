package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.nikdo53.tinymultiblocklib.color.IColorSupplier;

public class TintedVertexConsumer extends VertexConsumerWrapper {
    public IColorSupplier colorSupplier;

    public TintedVertexConsumer(VertexConsumer parent, IColorSupplier colorSupplier) {
        super(parent);
        this.colorSupplier = colorSupplier;
    }


    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha) {
        parent.putBulkData(pose, quad, brightness, red * colorSupplier.getRed(), green * colorSupplier.getGreen(), blue * colorSupplier.getBlue(), alpha * colorSupplier.getAlpha(), lightmap, packedOverlay, readAlpha);
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
        parent.putBulkData(pose, quad, red * colorSupplier.getRed(), green * colorSupplier.getGreen(), blue * colorSupplier.getBlue(), alpha * colorSupplier.getAlpha(), packedLight, packedOverlay);
    }

    @Override
    public void addVertex(float x, float y, float z, int color, float u, float v, int packedOverlay, int packedLight, float normalX, float normalY, float normalZ) {
        parent.addVertex(x, y, z, colorSupplier.applyColors(color), u, v, packedOverlay, packedLight, normalX, normalY, normalZ);
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        return parent.setColor(r * colorSupplier.getRed(), g * colorSupplier.getGreen(), b * colorSupplier.getBlue(), a * colorSupplier.getAlpha());
    }

    @Override
    public VertexConsumer setColor(float red, float green, float blue, float alpha) {
        return parent.setColor(red * colorSupplier.getRed(), green * colorSupplier.getGreen(), blue * colorSupplier.getBlue(), alpha * colorSupplier.getAlpha());
    }

    @Override
    public VertexConsumer setColor(int color) {
        return parent.setColor(colorSupplier.applyColors(color));
    }
}
