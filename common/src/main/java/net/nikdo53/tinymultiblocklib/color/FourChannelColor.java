package net.nikdo53.tinymultiblocklib.color;

public interface FourChannelColor {
    RGB asRGBA();
    HSV asHSVA();

    float getAlpha();
    void setAlpha(float alpha);

    interface RGB extends FourChannelColor, IColorSupplier {
        float getRed();
        float getGreen();
        float getBlue();

        void setRed(float red);
        void setGreen(float green);
        void setBlue(float blue);
    }

    interface HSV extends FourChannelColor {
        float getHue();
        float getSaturation();
        float getValue();

        void setHue(float hue);
        void setSaturation(float saturation);
        void setValue(float value);

    }
}
