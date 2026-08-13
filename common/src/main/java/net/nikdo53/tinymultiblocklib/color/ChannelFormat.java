package net.nikdo53.tinymultiblocklib.color;

import com.mojang.datafixers.util.Function5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record ChannelFormat<T extends NikdoColor<?>>(String name, ColorFormat colorFormat,
                                                     Function5<ChannelFormat<T>, Float, Float, Float, Float, T> factory,
                                                     List<String> channelNames) {

    public static final ChannelFormat<NikdoColor.RGB> ARGB = new ChannelFormat<>("ARGB", ColorFormat.RGB, NikdoColor.RGB::new, "Alpha", "Red", "Green", "Blue");
    public static final ChannelFormat<NikdoColor.RGB> RGBA = new ChannelFormat<>("RGBA", ColorFormat.RGB, NikdoColor.RGB::new, "Red", "Green", "Blue", "Alpha");

    public static final ChannelFormat<NikdoColor.HSV> HSVA = new ChannelFormat<>("HSVA", ColorFormat.HSV, NikdoColor.HSV::new, "Hue", "Saturation", "Value", "Alpha");

    public ChannelFormat(String name, ColorFormat format, Function5<ChannelFormat<T>, Float, Float, Float, Float, T> factory, String... channelNames) {
        this(name, format, factory, List.of(channelNames));
        verifyChannelsPresent(format, channelNames);
    }

    private static void verifyChannelsPresent(ColorFormat format, String[] channelNames) {
        ArrayList<String> strings = new ArrayList<>(format.channelNames());
        for (String channelName : channelNames) {
            strings.remove(channelName);
        }
        if (!strings.isEmpty()){
            throw new IllegalArgumentException("Channel colorFormat missing channels: " + strings);
        }
    }

    public T reformatColor(NikdoColor<?> color){
        ChannelFormat<?> currentFormat = color.currentFormat;
        List<String> currentChannelNames = currentFormat.colorFormat.channelNames();
        List<Float> converted = this.colorFormat.convertFrom(
                List.of(
                        color.getChannel(currentChannelNames.get(0)).get(),
                        color.getChannel(currentChannelNames.get(1)).get(),
                        color.getChannel(currentChannelNames.get(2)).get()
                ), this.colorFormat
        );

        Map<String, Float> channelValues = Map.of(
                this.colorFormat.channelNames().get(0), converted.get(0),
                this.colorFormat.channelNames().get(1), converted.get(1),
                this.colorFormat.channelNames().get(2), converted.get(2),
                "Alpha", color.getAlpha()
        );

        return factory.apply(this,
                channelValues.get(this.channelNames.get(0)),
                channelValues.get(this.channelNames.get(1)),
                channelValues.get(this.channelNames.get(2)),
                channelValues.get(this.channelNames.get(3))
        );
    }

    @Override
    public String toString() {
        return name;
    }
}

