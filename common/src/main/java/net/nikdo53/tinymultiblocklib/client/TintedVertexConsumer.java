package net.nikdo53.tinymultiblocklib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.nikdo53.nikdocolor.IColorSupplier;

public class TintedVertexConsumer extends VertexConsumerWrapper {
    public IColorSupplier colorSupplier;

    public TintedVertexConsumer(VertexConsumer parent, IColorSupplier colorSupplier) {
        super(parent);
        this.colorSupplier = colorSupplier;
    }


    @Override
    public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
        float[] colors = colorSupplier.applyColorsFloat(red, green, blue, alpha);
        super.vertex(x, y, z, colors[0], colors[1], colors[2], colors[3], texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        float[] colors = colorSupplier.applyColorsFloat(r, g, b, a);
        return super.color(((int) colors[0]), (int) colors[1], (int) colors[2], (int) colors[3]);
    }

    @Override
    public VertexConsumer color(float red, float green, float blue, float alpha) {
        float[] colors = colorSupplier.applyColorsFloat(red, green, blue, alpha);
        return super.color(colors[0], colors[1], colors[2], colors[3]);
    }

    @Override
    public VertexConsumer color(int colorARGB) {
        return super.color(colorSupplier.applyColors(colorARGB));
    }
}
