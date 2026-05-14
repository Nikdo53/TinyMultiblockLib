package net.nikdo53.tinymultiblocklib.components;

import com.mojang.blaze3d.vertex.PoseStack;

public enum RenderOffsetType {
    NONE(0f, 1f, 0f),
    OFFSET(0.001f, 1f, 0f),
    SCALED(0.001f, 1.002f, 0f),
    CROSS(0.001f, 1f, 0.001f);

    public final float offset;
    public final float scale;
    public final float xOffset;

    RenderOffsetType(float offset, float scale, float xOffset){
        this.offset = offset;
        this.scale = scale;
        this.xOffset = xOffset;
    }

    public void applyTransforms(PoseStack poseStack){
        if (this.equals(NONE)) return;

        poseStack.translate(-offset, -offset, -offset);
        poseStack.translate(-xOffset, 0f, 0f);
        poseStack.scale(scale, scale, scale);
    }
}
