package net.nikdo53.tinymultiblocklib.color;

import net.minecraft.util.StringRepresentable;

public enum ColorChannel implements StringRepresentable {
    RED("Red"),
    GREEN("Green"),
    BLUE("Blue"),

    HUE("Hue"),
    SATURATION("Saturation"),
    VALUE("Value"),
    LIGHTNESS("Lightness"), // not implemented yet

    ALPHA("Alpha")

    ;

    private final String name;
    private final char character;

    ColorChannel(String name) {
        this.name = name;
        this.character = Character.toLowerCase(name.charAt(0));
    }

    public WithValue withValue(float value){
        return new WithValue(this, value);
    }

    public static ColorChannel match(char character) {
        char lowerCase = Character.toLowerCase(character);

        for (ColorChannel channel : values()) {
            if (channel.character == lowerCase) {
                return channel;
            }
        }

        throw new IllegalArgumentException("No ColorChannel found for character: " + character);
    }

    public static ColorChannel match(String name) {
        if (name.length() == 1) {
            return match(name.charAt(0));
        }

        for (ColorChannel channel : values()) {
            if (channel.name.equalsIgnoreCase(name)) {
                return channel;
            }
        }

        throw new IllegalArgumentException("No ColorChannel found for name: " + name);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public record WithValue(ColorChannel channel, float value) {
        public WithValue {
            if (value < 0.0f || value > 1.0f) {
                throw new IllegalArgumentException("Value must be between 0.0 and 1.0");
            }
        }

        @Override
        public String toString() {
            return "ColorChannel.WithValue{" +
                    "channel=" + channel +
                    ", value=" + value +
                    '}';
        }
    }
}
