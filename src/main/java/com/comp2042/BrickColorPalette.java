package com.comp2042;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Centralises the mapping between tile IDs and their display colours so multiple UI
 * components can stay in sync.
 */
public final class BrickColorPalette {

    private BrickColorPalette() {
    }

    public static Paint resolve(int tileId) {
        Paint returnPaint;
        switch (tileId) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }
}
