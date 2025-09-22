package net.nikdo53.tinymultiblocklib.components;

public enum PreviewMode {
    /**
     * PLACED - Regular multiblock placed in the world
     * <p>
     * PREVIEW - In preview and placeable
     * <p>
     * INVALID - In preview with an invalid place location
     * */
    PLACED(1f,1f,1f,1f),
    PREVIEW(0.5f,1f,1f,1f),
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
}

