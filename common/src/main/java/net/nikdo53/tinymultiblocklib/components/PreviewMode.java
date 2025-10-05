package net.nikdo53.tinymultiblocklib.components;

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
}

