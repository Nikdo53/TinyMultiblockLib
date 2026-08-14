package net.nikdo53.tinymultiblocklib.color;

public interface IColorSupplier {
    float getRed();
    float getGreen();
    float getBlue();
    float getAlpha();

    default int applyColors(int originalColor){
        float r = originalColor >> 16 & 0xFF;
        float g = originalColor >> 8 & 0xFF;
        float b = originalColor & 0xFF;
        float a = originalColor >> 24 & 0xFF;

        r = r * getRed();
        g = g * getGreen();
        b = b * getBlue();
        a = a * getAlpha();

        return ((int) a & 0xFF) << 24 | ((int)r & 0xFF) << 16 | ((int)g & 0xFF) << 8 | (int)b & 0xFF;
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
        return (getAlphaInt() & 0xFF) << 24 | (getRedInt() & 0xFF) << 16 | (getGreenInt() & 0xFF) << 8 | getBlueInt() & 0xFF;
    }

    default int packedRGB(){
        return (getRedInt() & 0xFF) << 16 | (getGreenInt() & 0xFF) << 8 | getBlueInt() & 0xFF;
    }

    default Simple immutable(){
        return new Simple(getRed(), getGreen(), getBlue(), getAlpha());
    }

    default Mutable mutable(){
        return new Mutable(getRed(), getGreen(), getBlue(), getAlpha());
    }

    default int getRedInt(){
        return (int) (getRed() * 255);
    }

    default int getGreenInt(){
        return (int) (getGreen() * 255);
    }

    default int getBlueInt(){
        return (int) (getBlue() * 255);
    }

    default int getAlphaInt(){
        return (int) (getAlpha() * 255);
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

        public Simple copy(){
            return new Simple(red, green, blue, alpha);
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
