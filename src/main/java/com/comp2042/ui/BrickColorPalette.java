package com.comp2042.ui;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 * Centralises the mapping between tile IDs and their display colours so multiple UI
 * components can stay in sync. Supports multiple palettes to aid accessibility.
 */
public final class BrickColorPalette {

    public enum PaletteProfile {
        CLASSIC(createClassicPalette(), createClassicGhostPalette()),
        HIGH_CONTRAST(createHighContrastPalette(), createHighContrastGhostPalette());

        private final Paint[] brickColors;
        private final Paint[] ghostColors;

        PaletteProfile(Paint[] brickColors, Paint[] ghostColors) {
            this.brickColors = brickColors;
            this.ghostColors = ghostColors;
        }

        Paint brickColor(int tileId) {
            return colorFor(tileId, brickColors);
        }

        Paint ghostColor(int tileId) {
            return colorFor(tileId, ghostColors);
        }
    }

    private static PaletteProfile currentProfile = PaletteProfile.CLASSIC;

    private BrickColorPalette() {
    }

    public static void setProfile(PaletteProfile profile) {
        currentProfile = profile != null ? profile : PaletteProfile.CLASSIC;
    }

    public static Paint resolve(int tileId) {
        return currentProfile.brickColor(tileId);
    }

    public static Paint resolveGhost(int tileId) {
        return currentProfile.ghostColor(tileId);
    }

    private static Paint colorFor(int tileId, Paint[] palette) {
        if (palette == null || palette.length == 0) {
            return Color.TRANSPARENT;
        }
        if (tileId < 0 || tileId >= palette.length) {
            return palette[0];
        }
        Paint paint = palette[tileId];
        return paint != null ? paint : palette[0];
    }

    private static Paint[] createClassicPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                Color.AQUA,
                Color.BLUEVIOLET,
                Color.DARKGREEN,
                Color.YELLOW,
                Color.RED,
                Color.BEIGE,
                Color.BURLYWOOD
        };
    }

    private static Paint[] createClassicGhostPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                withOpacity(Color.AQUA, 0.35),
                withOpacity(Color.BLUEVIOLET, 0.35),
                withOpacity(Color.DARKGREEN, 0.35),
                withOpacity(Color.YELLOW, 0.35),
                withOpacity(Color.RED, 0.35),
                withOpacity(Color.BEIGE, 0.35),
                withOpacity(Color.BURLYWOOD, 0.35)
        };
    }

    private static Paint[] createHighContrastPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                stripedPaint(Color.rgb(0, 45, 95), Color.rgb(0, 168, 255)),
                stripedPaint(Color.rgb(55, 0, 90), Color.rgb(220, 230, 80)),
                stripedPaint(Color.rgb(5, 75, 20), Color.rgb(255, 255, 255)),
                stripedPaint(Color.rgb(75, 50, 0), Color.rgb(255, 190, 0)),
                stripedPaint(Color.rgb(70, 0, 0), Color.rgb(255, 255, 255)),
                stripedPaint(Color.rgb(35, 35, 35), Color.rgb(255, 0, 150)),
                stripedPaint(Color.rgb(0, 0, 0), Color.rgb(255, 255, 255))
        };
    }

    private static Paint[] createHighContrastGhostPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                withOpacity(Color.rgb(0, 168, 255), 0.5),
                withOpacity(Color.rgb(220, 230, 80), 0.5),
                withOpacity(Color.rgb(255, 255, 255), 0.5),
                withOpacity(Color.rgb(255, 190, 0), 0.5),
                withOpacity(Color.rgb(255, 255, 255), 0.5),
                withOpacity(Color.rgb(255, 0, 150), 0.5),
                withOpacity(Color.rgb(220, 220, 220), 0.5)
        };
    }

    private static Color withOpacity(Color base, double opacity) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), opacity);
    }

    private static LinearGradient stripedPaint(Color background, Color accent) {
        return new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.REPEAT,
                new Stop(0.0, accent),
                new Stop(0.35, accent),
                new Stop(0.36, background),
                new Stop(0.65, background),
                new Stop(0.66, accent),
                new Stop(1.0, accent)
        );
    }
}
