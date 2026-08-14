package net.nikdo53.tinymultiblocklib.color;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public abstract class NikdoColor<T extends NikdoColor<T>>{
    protected float[] channels;
    protected ChannelFormat<T> currentFormat;

    protected NikdoColor(ChannelFormat<T> format, float... values) {
        currentFormat = format;
        if (values.length != format.channelLookup().length)
            throw new IllegalArgumentException("Number of values does not match the number of channels in the format");

        channels = values;
    }

    public static NikdoColor.RGB fromHex(int hex){
        return new NikdoColor.RGB(ChannelFormat.ARGB,
                clamp((hex >> 24 & 0xFF) / 255f, 0, 1),
                clamp((hex >> 16 & 0xFF) / 255f, 0, 1),
                clamp((hex >> 8 & 0xFF) / 255f, 0, 1),
                clamp((hex & 0xFF) / 255f, 0, 1)
        );
    }

    public static NikdoColor.RGB fromHexNoAlpha(int hex){
        return new NikdoColor.RGB(ChannelFormat.RGB,
                clamp((hex >> 16 & 0xFF) / 255f, 0, 1),
                clamp((hex >> 8 & 0xFF) / 255f, 0, 1),
                clamp((hex & 0xFF) / 255f, 0, 1)
        );
    }

    // stops being dependent on mojang math class
    private static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

    public static RGB createRGB(ChannelFormat<NikdoColor.RGB> format, float... values) {
        return new NikdoColor.RGB(format, values);
    }

    public static HSV createHSV(ChannelFormat<NikdoColor.HSV> format, float... values) {
        return new NikdoColor.HSV(format, values);
    }


    public int[] getIntArray(){
        int[] result = new int[channels.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) (channels[i] * 255);
        }
        return result;
    }

    public int packed(){
        int[] intArray = getIntArray();
        int result = 0;

        for (int j : intArray) {
            result = (result << 8) | j & 0xFF;
        }

        return result;
    }

    /**
     * Creates a new color in the specified format, converting the values from the current format to the new format.
     * !!Does not change the current color's format, it creates a new color in the new format!!
     * @param format format of the new color
     * @return a new color in the specified format
     * @param <F> NikdoColor subclass type of the new color
     */
    public <F extends NikdoColor<?>> F reformatColor(ChannelFormat<F> format){
        return format.reformatColor(this);
    }

    /**
     * Reorders the channels of the current color to match the specified format. If a channel is not present in the current color, it has to be provided as an additional channel with a value.
     * @param formatNew format of the new color
     * @param additionalChannels channels with values to be used if the channel is not present in the current color (like alpha channel for RGB)
     */
    public T reorder(ChannelFormat<T> formatNew, ColorChannel.WithValue... additionalChannels) {
        if (currentFormat == formatNew)
            return cast();

        float[] replace = new float[formatNew.channelLookup().length];
        for (int i = 0; i < replace.length; i++) {
            ColorChannel channel = formatNew.channelLookup()[i];
            if (this.hasChannel(channel)) {
                replace[i] = getChannel(channel);
            } else {
                boolean success = false;
                for (ColorChannel.WithValue additionalChannel : additionalChannels) {
                    if (additionalChannel.channel() == channel) {
                        replace[i] = additionalChannel.value();
                        success = true;
                        break;
                    }
                }
                if (!success) {
                    throw new IllegalArgumentException("Channel " + channel + " is not present in the current color and no additional value was provided");
                }
            }
        }

        channels = replace;
        currentFormat = formatNew;
        return cast();
    }

    /**
     * Reorders the channels of the current color to match the specified format. If a channel is not present in the current color, the default value is used.
     * @param formatNew format of the new color
     * @param defaultValue default value to be used if the channel is not present in the current color (like alpha channel for RGB)
     */
    public T reorder(ChannelFormat<T> formatNew, float defaultValue) {
        if (currentFormat == formatNew)
            return cast();

        float[] replace = new float[formatNew.channelLookup().length];
        for (int i = 0; i < replace.length; i++) {
            ColorChannel channel = formatNew.channelLookup()[i];
            if (this.hasChannel(channel)) {
                replace[i] = getChannel(channel);
            } else {
                replace[i] = defaultValue;
            }
        }

        channels = replace;
        currentFormat = formatNew;
        return cast();
    }


    public boolean hasChannel(ColorChannel channel) {
        for (ColorChannel c : currentFormat.channelLookup()) {
            if (c == channel) {
                return true;
            }
        }
        return false;
    }

    public float getChannel(int index) {
        return channels[index];
    }

    public T setChannel(int index, float value) {
        channels[index] = value;
        return cast();
    }

    public float getChannel(ColorChannel channel) {
        return channels[currentFormat.getChannelIndex(channel)];
    }

    public T setChannel(ColorChannel channel, float value) {
        channels[currentFormat.getChannelIndex(channel)] = Math.clamp(value, 0.0f, 1.0f);
        return cast();
    }

    public float getChannel(String channelName) {
        return getChannel(ColorChannel.match(channelName));
    }

    public T setChannel(String channelName, float value) {
        return setChannel(ColorChannel.match(channelName), value);
    }

    public T operation(UnaryOperator<Float> operation, ColorChannel... channelsForOperation) {
        if (channelsForOperation.length == 0) {
            for (int i = 0; i < channels.length; i++) {
                channels[i] = operation.apply(channels[i]);
            }
        } else {
            for (ColorChannel channel : channelsForOperation) {
                setChannel(channel, operation.apply(getChannel(channel)));
            }
        }
        return cast();
    }

    public T operation(UnaryOperator<Float> operation, String channelsForOperation1, String... channelsForOperationRest) {
        ColorChannel[] channelsForOperation = new ColorChannel[channelsForOperationRest.length + 1];
        channelsForOperation[0] = ColorChannel.match(channelsForOperation1);
        for (int i = 0; i < channelsForOperationRest.length; i++) {
            channelsForOperation[i + 1] = ColorChannel.match(channelsForOperationRest[i]);
        }
        return operation(operation, channelsForOperation);
    }

    public T operation(NikdoColor<?> otherColor,  BinaryOperator<Float> operation, ColorChannel... channelPairs) {
        if (channelPairs.length == 0) {
            for (int i = 0; i < channels.length; i++) {
                channels[i] = operation.apply(channels[i], otherColor.getChannel(i));
            }
        } else {
            if (channelPairs.length % 2 != 0) {
                throw new IllegalArgumentException("channelPairs must be in pairs, 1st of the 1st color, 2nd of the 2nd color");
            }
            for (int i = 0; i < channelPairs.length; i += 2) {
                ColorChannel first =  channelPairs[i];
                ColorChannel second = channelPairs[i + 1];
                setChannel(first, operation.apply(getChannel(first), otherColor.getChannel(second)));
            }
        }
        return cast();
    }

    public T operation(NikdoColor<?> otherColor,  BinaryOperator<Float> operation, String channelPairs1, String... channelPairsRest) {
        ColorChannel[] channelsForOperation = new ColorChannel[channelPairsRest.length + 1];
        channelsForOperation[0] = ColorChannel.match(channelPairs1);
        for (int i = 0; i < channelPairsRest.length; i++) {
            channelsForOperation[i + 1] = ColorChannel.match(channelPairsRest[i]);
        }
        return operation(otherColor, operation, channelsForOperation);
    }

    public T swirl(ColorChannel channel1, ColorChannel channel2) {
        float temp = getChannel(channel1);
        setChannel(channel1, getChannel(channel2));
        setChannel(channel2, temp);
        return cast();
    }

    public T swirl(String channel1, String channel2) {
        float temp = getChannel(channel1);
        setChannel(channel1, getChannel(channel2));
        setChannel(channel2, temp);
        return cast();
    }


    @SuppressWarnings("unchecked")
    private T cast(){
        return (T) this;
    }

    public abstract T copy();

    public abstract NikdoColor.RGB asRGBA();

    public abstract NikdoColor.HSV asHSVA();

    public float getAlpha() {
        return getChannel(ColorChannel.ALPHA);
    }

    public T setAlpha(float alpha) {
        return setChannel(ColorChannel.ALPHA, alpha);
    }

    @Override
    public String toString() { // Color:[Red:0.5, Green:0.5, Blue:0.5, Alpha:1.0]
        StringBuilder builder = new StringBuilder();
        builder.append("Color:[");
        int i = 0;
        for (ColorChannel channel : currentFormat.channelLookup()) {
            if (i != 0) {
                builder.append(", ");
            }

            builder.append(channel.getSerializedName()).append(":").append(channels[i]);
            i++;
        }
        builder.append("]");
        return builder.toString();
    }

    public static class RGB extends NikdoColor<RGB> implements IColorSupplier {
        public RGB(ChannelFormat<NikdoColor.RGB> format, float... values) {
            super(format, values);
        }

        @Override
        public RGB copy() {
            return new RGB(currentFormat, channels.clone());
        }

        public float getRed() {
            return getChannel(ColorChannel.RED);
        }

        public float getGreen() {
            return getChannel(ColorChannel.GREEN);
        }

        public float getBlue() {
            return getChannel(ColorChannel.BLUE);
        }

        public RGB setRed(float red) {
            return setChannel(ColorChannel.RED, red);
        }

        public RGB setGreen(float green) {
            return setChannel(ColorChannel.GREEN, green);
        }

        public RGB setBlue(float blue) {
            return setChannel(ColorChannel.BLUE, blue);
        }

        public NikdoColor.RGB asRGBA() {
            return new NikdoColor.RGB(ChannelFormat.RGBA, getRed(), getGreen(), getBlue(), getAlpha());
        }

        public NikdoColor.HSV asHSVA() {
            float[] hsv = ColorUtils.rgbToHsv(getRed(), getGreen(), getBlue());
            return new NikdoColor.HSV(ChannelFormat.HSVA, hsv[0], hsv[1], hsv[2], getAlpha());
        }
    }

    public static class HSV extends NikdoColor<HSV> {
        public HSV(ChannelFormat<NikdoColor.HSV> format, float... values) {
            super(format, values);
        }

        @Override
        public HSV copy() {
            return new HSV(currentFormat, channels.clone());
        }

        public float getHue() {
            return getChannel(ColorChannel.HUE);
        }

        public float getSaturation() {
            return getChannel(ColorChannel.SATURATION);
        }

        public float getValue() {
            return getChannel(ColorChannel.VALUE);
        }

        public HSV setHue(float hue) {
            return setChannel(ColorChannel.HUE, hue);
        }

        public HSV setSaturation(float saturation) {
            return setChannel(ColorChannel.SATURATION, saturation);
        }

        public HSV setValue(float value) {
            return setChannel(ColorChannel.VALUE, value);
        }

        @Override
        public NikdoColor.RGB asRGBA() {
            float[] rgb = ColorUtils.hsbToRgb(getHue(), getSaturation(), getValue());
            return new NikdoColor.RGB(ChannelFormat.RGBA, rgb[0], rgb[1], rgb[2], getAlpha());
        }

        @Override
        public NikdoColor.HSV asHSVA() {
            return new NikdoColor.HSV(ChannelFormat.HSVA, getHue(), getSaturation(), getValue(), getAlpha());
        }
    }
}
