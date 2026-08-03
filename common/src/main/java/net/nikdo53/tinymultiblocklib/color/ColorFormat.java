package net.nikdo53.tinymultiblocklib.color;

import com.google.common.collect.ImmutableList;

import java.util.List;

public abstract class ColorFormat {
    public static final ColorFormat RGB = new ColorFormat(List.of("Red", "Green", "Blue")) {
        @Override
        public List<Float> convertFrom(List<Float> otherValues, ColorFormat other) {
            if (other == this){
                return otherValues;
            }

            if (other != HSV)
                throw new IllegalArgumentException("Not implemented yet: " + other + " -> RGB");

            float[] floats = ColorUtils.hsbToRgb(otherValues.get(0), otherValues.get(1), otherValues.get(2));
            return List.of(floats[0], floats[1], floats[2]);
        }
    };
    public static final ColorFormat HSV = new ColorFormat(List.of("Hue", "Saturation", "Value")) {
        @Override
        public List<Float> convertFrom(List<Float> otherValues, ColorFormat other) {
            if (other == this){
                return otherValues;
            }
            if (other != RGB)
                throw new IllegalArgumentException("Not implemented yet: " + other + " -> HSV");

            float[] floats = ColorUtils.rgbToHsb(otherValues.get(0), otherValues.get(1), otherValues.get(2));
            return List.of(floats[0], floats[1], floats[2]);
        }
    };

    private final ImmutableList<String> channelNames;

    public ColorFormat(List<String> channelNames) {
        this.channelNames = ImmutableList.copyOf(channelNames);
    }

    public List<String> channelNames() {
        return channelNames;
    }

    public abstract List<Float> convertFrom(List<Float> values, ColorFormat other);
}
