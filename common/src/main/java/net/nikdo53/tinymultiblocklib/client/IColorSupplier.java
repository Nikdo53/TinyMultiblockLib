package net.nikdo53.tinymultiblocklib.client;

import net.minecraft.util.ARGB;

public interface IColorSupplier {
    float getRed();
    float getGreen();
    float getBlue();
    float getAlpha();

    default int applyColors(int originalColor){
        float r = ARGB.red(originalColor);
        float g = ARGB.green(originalColor);
        float b = ARGB.blue(originalColor);
        float a = ARGB.alpha(originalColor);

        r = r * getRed();
        g = g * getGreen();
        b = b * getBlue();
        a = a * getAlpha();

        return ARGB.color((int) a, (int) r, (int) g, (int) b);
    }

    default float[] applyColorsFloat(float r, float g, float b, float a){
        float[] rgba = new float[4];
        rgba[0] = r * getRed();
        rgba[1] = g * getGreen();
        rgba[2] = b * getBlue();
        rgba[3] = a * getAlpha();

        return  rgba;
    }

    default int packedARGB(){
        return ARGB.color((int) (getAlpha() * 255), (int) (getRed() * 255), (int) (getGreen() * 255), (int) (getBlue() * 255));
    }

    record Simple(float red, float green, float blue, float alpha) implements IColorSupplier{

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

    class Mutable implements IColorSupplier{
        protected float red;
        protected float green;
        protected float blue;
        protected float alpha;

        public Mutable(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
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

        public void setRed(float red) {
            this.red = red;
        }

        public void setGreen(float green) {
            this.green = green;
        }

        public void setBlue(float blue) {
            this.blue = blue;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        public void copy(IColorSupplier color){
            this.red = color.getRed();
            this.green = color.getGreen();
            this.blue = color.getBlue();
            this.alpha = color.getAlpha();
        }
    }
}
