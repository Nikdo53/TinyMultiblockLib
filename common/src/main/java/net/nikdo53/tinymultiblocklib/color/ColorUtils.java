package net.nikdo53.tinymultiblocklib.color;

public class ColorUtils {

    public static float[] rgbToHsv(float r, float g, float b) {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0.0f;
        float s = max == 0.0f ? 0.0f : delta / max;
        float v = max;

        if (delta != 0.0f) {
            if (max == r) {
                h = ((g - b) / delta) % 6.0f;
            } else if (max == g) {
                h = (b - r) / delta + 2.0f;
            } else {
                h = (r - g) / delta + 4.0f;
            }

            h /= 6.0f;

            if (h < 0.0f) {
                h += 1.0f;
            }
        }

        return new float[] {h, s, v};
    }

    public static float[] hsbToRgb(float h, float s, float v) {
        float r, g, b;

        float c = v * s;
        float x = c * (1.0f - Math.abs((h * 6.0f) % 2.0f - 1.0f));
        float m = v - c;

        if (h < 1.0f / 6.0f) {
            r = c;
            g = x;
            b = 0;
        } else if (h < 2.0f / 6.0f) {
            r = x;
            g = c;
            b = 0;
        } else if (h < 3.0f / 6.0f) {
            r = 0;
            g = c;
            b = x;
        } else if (h < 4.0f / 6.0f) {
            r = 0;
            g = x;
            b = c;
        } else if (h < 5.0f / 6.0f) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        return new float[] {r + m, g + m, b + m};
    }
}
