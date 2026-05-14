package net.nikdo53.tinymultiblocklib.components;


import net.minecraft.util.ARGB;
import net.nikdo53.tinymultiblocklib.client.IColorSupplier;

public enum PreviewMode implements IColorSupplier {
    /**
     * PLACED - Regular multiblock placed in the world
     * <p>
     * PREVIEW - In preview and placeable
     * <p>
     * ENTITY_BLOCKED - In preview with a valid location, but an entity in the way
     * <p>
     * INVALID - In preview with an invalid place location
     * */
    PLACED(1f,1f,1f,1f),
    PREVIEW(0.5f,1f,1f,1f),
    ENTITY_BLOCKED(0.5f,1f,0.8f,0.4f),
    INVALID(0.5f,0.8f,0.3f,0.3f);

    public final float alpha;
    public final float red;
    public final float green;
    public final float blue;

    PreviewMode(float alpha, float red, float green, float blue) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public float getRed() {
        return red;
    }

    @Override
    public float getGreen() {
        return green;
    }

    @Override
    public float getBlue() {
        return blue;
    }

    @Override
    public float getAlpha() {
        return alpha;
    }
}

