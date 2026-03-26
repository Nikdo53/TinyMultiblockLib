package net.nikdo53.tinymultiblocklib.components;


import net.minecraft.util.ARGB;

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

    public int applyColors(int originalColor){
        float r = ARGB.red(originalColor);
        float g = ARGB.green(originalColor);
        float b = ARGB.blue(originalColor);
        float a = ARGB.alpha(originalColor);

        r = r * red;
        g = g * green;
        b = b * blue;
        a = a * alpha;

        return ARGB.color((int) a, (int) r, (int) g, (int) b);
    }

    public float[] applyColorsFloat(float r, float g, float b, float a){
        float[] rgba = new float[4];
        rgba[0] = r * red;
        rgba[1] = g * green;
        rgba[2] = b * blue;
        rgba[3] = a * alpha;

        return  rgba;
    }

    public int packedARGB(){
       return ARGB.color((int) (alpha * 255), (int) (red * 255), (int) (green * 255), (int) (blue * 255));
    }
}

