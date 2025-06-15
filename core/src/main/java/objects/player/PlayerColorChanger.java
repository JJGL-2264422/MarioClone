package objects.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class PlayerColorChanger {

    public static Texture cambiarColorRopaConContraste(Texture original, String colorHex) {
        Color baseColor = colorDesdeHex(colorHex);

        // Baja el brillo (Mas oscuro)
        Color tonoMedio = oscurecer(baseColor, 0.8f);
        Color tonoOscuro = oscurecer(baseColor, 0.6f);

        Color[] coloresObjetivo = new Color[] {
            tonoOscuro, tonoMedio, baseColor
        };

        original.getTextureData().prepare();
        Pixmap pixmap = original.getTextureData().consumePixmap();

        Color[] tonosOriginales = new Color[] {
            new Color(24 / 255f, 29 / 255f, 51 / 255f, 1),
            new Color(31 / 255f, 45 / 255f, 58 / 255f, 1),
            new Color(75 / 255f, 88 / 255f, 102 / 255f, 1)
        };

        float tolerancia = 0.15f;

        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                int pixel = pixmap.getPixel(x, y);
                Color actual = new Color();
                Color.rgba8888ToColor(actual, pixel);

                for (int i = 0; i < tonosOriginales.length; i++) {
                    if (esColorSimilar(actual, tonosOriginales[i], tolerancia)) {
                        pixmap.drawPixel(x, y, Color.rgba8888(coloresObjetivo[i]));
                        break;
                    }
                }
            }
        }

        Texture resultado = new Texture(pixmap);
        pixmap.dispose();
        return resultado;
    }

    private static Color colorDesdeHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return Color.valueOf(hex);
    }

    private static Color oscurecer(Color color, float factor) {
        return new Color(
            clamp(color.r * factor),
            clamp(color.g * factor),
            clamp(color.b * factor),
            1f
        );
    }

    private static boolean esColorSimilar(Color a, Color b, float tolerancia) {
        return Math.abs(a.r - b.r) < tolerancia &&
            Math.abs(a.g - b.g) < tolerancia &&
            Math.abs(a.b - b.b) < tolerancia;
    }

    private static float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
