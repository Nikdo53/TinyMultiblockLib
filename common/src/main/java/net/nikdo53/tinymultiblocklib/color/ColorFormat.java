package net.nikdo53.tinymultiblocklib.color;

public abstract class ColorFormat {
    public static final ColorFormat RGB = new ColorFormat(ColorChannel.RED, ColorChannel.GREEN, ColorChannel.BLUE) {
        @Override
        public float[] convertFrom(ColorFormat other, float... otherValues) {
            if (other == this){
                return otherValues;
            }

            if (other != HSV)
                throw new IllegalArgumentException("Not implemented yet: " + other + " -> RGB");

            return ColorUtils.hsbToRgb(otherValues[0], otherValues[1], otherValues[2]);
        }
    };
    public static final ColorFormat HSV = new ColorFormat(ColorChannel.HUE, ColorChannel.SATURATION, ColorChannel.VALUE) {
        @Override
        public float[] convertFrom(ColorFormat other, float... otherValues) {
            if (other == this){
                return otherValues;
            }
            if (other != RGB)
                throw new IllegalArgumentException("Not implemented yet: " + other + " -> HSV");

            return ColorUtils.rgbToHsv(otherValues[0], otherValues[1], otherValues[2]);
        }
    };

    private final ColorChannel[] channelNames;

    public ColorFormat(ColorChannel... channelNames) {
        this.channelNames = channelNames;
    }

    public ColorChannel[] requiredChannels() {
        return channelNames;
    }

    public abstract float[] convertFrom(ColorFormat other, float... values);;
}
