package net.nikdo53.tinymultiblocklib.color;

import com.mojang.datafixers.util.Function5;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

@NullMarked
public abstract class NikdoColor implements FourChannelColor{
    protected List<Channel> channels = new ArrayList<>(4);
    protected NikdoColor.ChannelFormat<? extends NikdoColor> currentFormat;

    protected NikdoColor(ChannelFormat<?> format) {
        currentFormat = format;
    }

    public static NikdoColor.RGB fromHex(int hex){
        return new NikdoColor.RGB(ChannelFormat.ARGB, Mth.clamp((hex >> 24 & 0xFF) / 255f, 0, 1), Mth.clamp((hex >> 16 & 0xFF) / 255f, 0, 1), Mth.clamp((hex >> 8 & 0xFF) / 255f, 0, 1), Mth.clamp((hex & 0xFF) / 255f, 0, 1));
    }

    public <T extends NikdoColor> T reformatColor(ChannelFormat<T> format){
        return format.reformatColor(this);
    }

    public void reorder(ChannelFormat<? extends NikdoColor> format){
        if (currentFormat == format)
            return;

        if (currentFormat.colorFormat != format.colorFormat){
            throw new IllegalArgumentException("Cannot reorder colors as they are not of the same colorFormat: " + currentFormat + " and " + format);
        }

        List<Channel> replace = new ArrayList<>(4);

        replace.add(getChannel(format.channelNames().get(0)));
        replace.add(getChannel(format.channelNames().get(1)));
        replace.add(getChannel(format.channelNames().get(2)));
        replace.add(getChannel(format.channelNames().get(3)));

        channels = replace;
        currentFormat = format;
    }

    public Channel getChannel(int channel) {
        return channels.get(channel);
    }

    public Channel getChannel(String channel) {
        try {
            int i = Integer.parseInt(channel);
            return getChannel(i);
        } catch (NumberFormatException _) {
           //ignore
        }
        return channels.stream().filter(c -> c.matchesName(channel)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tried to get channel:[" + channel + "] which does not exist in: " + this));
    }

    public void operation(UnaryOperator<Float> operation) {
        for (Channel channel : channels) {
            channel.set(operation.apply(channel.get()));
        }
    }

    public void operation(NikdoColor color,  BinaryOperator<Float> operation) {
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = getChannel(i);
            channel.set(operation.apply(channel.get(), color.getChannel(i).get()));
        }
    }


    @Override
    public abstract NikdoColor.RGB asRGBA();

    @Override
    public abstract NikdoColor.HSV asHSVA();

    @Override
    public float getAlpha() {
        return getChannel("alpha").get();
    }

    @Override
    public void setAlpha(float alpha) {
        getChannel("alpha").set(alpha);
    }

    @Override
    public String toString() {
        return "Color:" + channels;
    }

    public static class RGB extends NikdoColor implements FourChannelColor.RGB {
        public RGB(ChannelFormat<NikdoColor.RGB> format, float one, float two, float three, float four) {
            super(format);

            if (format.colorFormat != ColorFormat.RGB)
                throw new IllegalArgumentException("RGB color must be created with RGB colorFormat, not: " + format);

            float[] values = {one, two, three, four};
            for (int i = 0; i < format.channelNames().size(); i++) {
                channels.add(new Channel(values[i], format.channelNames().get(i)));
            }

        }

        @Override
        public float getRed() {
            return getChannel("red").get();
        }

        @Override
        public float getGreen() {
            return getChannel("green").get();
        }

        @Override
        public float getBlue() {
            return getChannel("blue").get();
        }

        @Override
        public void setRed(float red) {
            getChannel("red").set(red);
        }

        @Override
        public void setGreen(float green) {
            getChannel("green").set(green);
        }

        @Override
        public void setBlue(float blue) {
            getChannel("blue").set(blue);
        }

        @Override
        public NikdoColor.RGB asRGBA() {
            return new NikdoColor.RGB(ChannelFormat.RGBA, getRed(), getGreen(), getBlue(), getAlpha());
        }

        @Override
        public NikdoColor.HSV asHSVA() {
            float[] hsv = ColorUtils.rgbToHsb(getRed(), getGreen(), getBlue());
            return new NikdoColor.HSV(ChannelFormat.HSVA, hsv[0], hsv[1], hsv[2], getAlpha());
        }
    }

    public static class HSV extends NikdoColor implements FourChannelColor.HSV {
        public HSV(ChannelFormat<NikdoColor.HSV> format, float one, float two, float three, float four) {
            super(format);

            if (format.colorFormat != ColorFormat.HSV)
                throw new IllegalArgumentException("HSV color must be created with HSV colorFormat, not: " + format);

            float[] values = {one, two, three, four};
            for (int i = 0; i < format.channelNames().size(); i++) {
                channels.add(new Channel(values[i], format.channelNames().get(i)));
            }

        }

        @Override
        public float getHue() {
            return getChannel("hue").get();
        }

        @Override
        public float getSaturation() {
            return getChannel("saturation").get();
        }

        @Override
        public float getValue() {
            return getChannel("value").get();
        }

        @Override
        public void setHue(float hue) {
            getChannel("hue").set(hue);
        }

        @Override
        public void setSaturation(float saturation) {
            getChannel("saturation").set(saturation);
        }

        @Override
        public void setValue(float value) {
            getChannel("value").set(value);
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

    public static class Channel{
        protected float value;
        protected final String name;

        public Channel(float value, String name) {
            this.value = value;
            this.name = name;
            if (name.length() < 2)
                throw new IllegalArgumentException("Channel name should be at least 2 characters long: " + name);
        }

        public float get() {
            return value;
        }

        public void set(float value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public char getChar(){
            return name.toLowerCase().charAt(0);
        }

        public boolean matchesName(String nameOrChar){
            nameOrChar = nameOrChar.toLowerCase();
            String lowerCaseName = name.toLowerCase();
            return nameOrChar.equals(lowerCaseName) || getChar() == nameOrChar.charAt(0);
        }

        @Override
        public String toString() {
            return name + ": " + value; //eg. red: 1.0
        }
    }

    public record ChannelFormat<T extends NikdoColor>(String name, ColorFormat colorFormat,
                                                      Function5<ChannelFormat<T>, Float, Float, Float, Float, T> factory,
                                                      List<String> channelNames) {

        public static final ChannelFormat<RGB> ARGB = new ChannelFormat<>("ARGB", ColorFormat.RGB, NikdoColor.RGB::new, "Alpha", "Red", "Green", "Blue");
        public static final ChannelFormat<RGB> RGBA = new ChannelFormat<>("RGBA", ColorFormat.RGB, NikdoColor.RGB::new, "Red", "Green", "Blue", "Alpha");

        public static final ChannelFormat<HSV> HSVA = new ChannelFormat<>("HSVA", ColorFormat.HSV, NikdoColor.HSV::new, "Hue", "Saturation", "Value", "Alpha");

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

        public T reformatColor(NikdoColor color){
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
}
