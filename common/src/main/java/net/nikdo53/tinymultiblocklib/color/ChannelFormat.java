package net.nikdo53.tinymultiblocklib.color;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;

import java.util.ArrayList;
import java.util.List;

public record ChannelFormat<T extends NikdoColor<?>>(ColorFormat colorFormat, Factory<T> factory, ColorChannel... channelLookup) {

    public static final ChannelFormat<NikdoColor.RGB> RGB = new ChannelFormat<>(ColorFormat.RGB, NikdoColor.RGB::new, ColorChannel.RED, ColorChannel.GREEN, ColorChannel.BLUE);

    public static final ChannelFormat<NikdoColor.RGB> ARGB = new ChannelFormat<>(ColorFormat.RGB, NikdoColor.RGB::new, ColorChannel.ALPHA, ColorChannel.RED, ColorChannel.GREEN, ColorChannel.BLUE);
    public static final ChannelFormat<NikdoColor.RGB> RGBA = new ChannelFormat<>(ColorFormat.RGB, NikdoColor.RGB::new, ColorChannel.RED, ColorChannel.GREEN, ColorChannel.BLUE, ColorChannel.ALPHA);

    public static final ChannelFormat<NikdoColor.HSV> HSV = new ChannelFormat<>(ColorFormat.HSV, NikdoColor.HSV::new, ColorChannel.HUE, ColorChannel.SATURATION, ColorChannel.VALUE, ColorChannel.ALPHA);

    public static final ChannelFormat<NikdoColor.HSV> HSVA = new ChannelFormat<>(ColorFormat.HSV, NikdoColor.HSV::new, ColorChannel.HUE, ColorChannel.SATURATION, ColorChannel.VALUE, ColorChannel.ALPHA);

    public ChannelFormat(ColorFormat colorFormat, Factory<T> factory, ColorChannel... channelLookup) {
        this.colorFormat = colorFormat;
        this.factory = factory;
        this.channelLookup = channelLookup;

        verifyChannelsPresent(colorFormat, channelLookup);
    }

    public int getChannelIndex(ColorChannel channel){
        for (int i = 0; i < channelLookup.length; i++) {
            if (channelLookup[i] == channel) {
                return i;
            }
        }
        throw new IllegalArgumentException("Channel " + channel + " not found in format " + this);
    }

    private static void verifyChannelsPresent(ColorFormat format, ColorChannel... channels) {
        ArrayList<ColorChannel> channelsMissing = new ArrayList<>(List.of(format.requiredChannels()));
        for (ColorChannel channel : channels) {
            channelsMissing.remove(channel);
        }

        if (!channelsMissing.isEmpty()){
            throw new IllegalArgumentException("Channel colorFormat missing channels: " + channelsMissing);
        }
    }

    public T reformatColor(NikdoColor<?> color){
        ChannelFormat<?> oldFormat = color.currentFormat;
        ColorChannel[] oldFormatNames = oldFormat.colorFormat.requiredChannels();

        float[] oldFormattedValues = new float[oldFormatNames.length];
        for(int i = 0; i < oldFormatNames.length; i++){
            oldFormattedValues[i] = color.getChannel(oldFormatNames[i]);
        }

        float[] converted = this.colorFormat.convertFrom(
                this.colorFormat, oldFormattedValues
        );

        Object2FloatArrayMap<ColorChannel> channelValues = new Object2FloatArrayMap<>();
        for(int i = 0; i < this.colorFormat.requiredChannels().length; i++){
            channelValues.put(this.colorFormat.requiredChannels()[i], converted[i]);
        }

        float[] newValues = new float[this.channelLookup.length];
        int i = 0;
        for (ColorChannel colorChannel : channelLookup) {
            if (channelValues.containsKey(colorChannel)) {
                newValues[i] = channelValues.getFloat(colorChannel);
            } else {
                newValues[i] = color.getChannel(colorChannel); // for cases like alpha where the channel is not present in the ColorFormat, just use the old value
            }
            i++;
        }

        return factory.apply(this, newValues);
    }

    @Override
    public String toString() {
        return "ChannelFormat{" +
                "colorFormat=" + colorFormat +
                ",channelNames=" + List.of(channelLookup) +
                '}';
    }

    @FunctionalInterface
    public interface Factory<C extends NikdoColor<?>> {
        C apply(ChannelFormat<C> format, float... values);
    }

}

