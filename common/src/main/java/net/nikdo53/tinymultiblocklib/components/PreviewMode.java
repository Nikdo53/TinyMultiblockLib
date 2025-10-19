package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.util.FastColor;

public enum PreviewMode {
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
    ENTITY_BLOCKED(0.5f,1f,0.7f,0.4f),
    INVALID(0.5f,1f,0.4f,0.4f);

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

    public int applyColors(int originalColor){
        float r = FastColor.ARGB32.red(originalColor);
        float g = FastColor.ARGB32.green(originalColor);
        float b = FastColor.ARGB32.blue(originalColor);
        float a = FastColor.ARGB32.alpha(originalColor);

        r = r * red;
        g = g * green;
        b = b * blue;
        a = a * alpha;

        return FastColor.ARGB32.color((int) a, (int) r, (int) g, (int) b);
    }

    public float[] applyColorsFloat(float r, float g, float b, float a){
        float[] rgba = new float[4];
        rgba[0] = r * red;
        rgba[1] = g * green;
        rgba[2] = b * blue;
        rgba[3] = a * alpha;

        return  rgba;
    }
}

