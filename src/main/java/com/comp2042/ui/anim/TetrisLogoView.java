package com.comp2042.ui.anim;

import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Animated "Tetris FX" style logo with glitch overlays.
 */
public class TetrisLogoView extends StackPane {

    private static final Color COLOR_CYAN = Color.web("#00f3ff");
    private static final Color COLOR_MAGENTA = Color.web("#ff0055");
    private static final double GLITCH_INTERVAL_MS = 70;

    private final Random random = new Random();
    private final Text glitchMagenta;
    private final Text glitchCyan;
    private final Timeline glitchTimeline;

    private final Text mainText;
    private final Timeline colorTimeline;
    private double hueOffset = 0;

    public TetrisLogoView(String text) {
        setPickOnBounds(false);
        setMouseTransparent(true);

        Font font = loadLogoFont();
        mainText = buildMainText(text, font);
        glitchMagenta = buildGlitchLayer(text, font, COLOR_MAGENTA);
        glitchCyan = buildGlitchLayer(text, font, COLOR_CYAN);

        getChildren().addAll(glitchMagenta, glitchCyan, mainText);

        glitchTimeline = new Timeline(new KeyFrame(Duration.millis(GLITCH_INTERVAL_MS), e -> runGlitchCycle()));
        glitchTimeline.setCycleCount(Animation.INDEFINITE);

        colorTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> runColorCycle()));
        colorTimeline.setCycleCount(Animation.INDEFINITE);

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                glitchTimeline.stop();
                colorTimeline.stop();
            } else if (glitchTimeline.getStatus() != Animation.Status.RUNNING) {
                glitchTimeline.play();
                colorTimeline.play();
            }
        });
    }

    public void play() {
        if (glitchTimeline.getStatus() != Animation.Status.RUNNING) {
            glitchTimeline.play();
            colorTimeline.play();
        }
    }

    public void stop() {
        glitchTimeline.stop();
        colorTimeline.stop();
    }

    private Text buildMainText(String text, Font font) {
        Text mainText = new Text(text);
        mainText.setFont(font);
        updateMainTextColor(mainText, COLOR_CYAN);
        return mainText;
    }

    private void updateMainTextColor(Text text, Color baseColor) {
        text.setFill(new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.WHITE),
                new Stop(0.65, Color.WHITE),
                new Stop(1.0, baseColor)));

        DropShadow glow = new DropShadow();
        glow.setColor(baseColor.deriveColor(0, 1, 1, 0.65));
        glow.setRadius(30);
        glow.setSpread(0.25);
        text.setEffect(glow);
    }

    private Text buildGlitchLayer(String text, Font font, Color color) {
        Text glitchLayer = new Text(text);
        glitchLayer.setFont(font);
        glitchLayer.setFill(color);
        glitchLayer.setOpacity(0.85);
        glitchLayer.setBlendMode(BlendMode.ADD);
        return glitchLayer;
    }

    private void runGlitchCycle() {
        applyGlitch(glitchMagenta, -3.0, -0.5);
        applyGlitch(glitchCyan, 0.5, 3.0);
    }

    private void runColorCycle() {
        hueOffset += 2; // Shift hue by 2 degrees each frame
        if (hueOffset > 360)
            hueOffset -= 360;

        Color dynamicColor = COLOR_CYAN.deriveColor(hueOffset, 1.0, 1.0, 1.0);
        updateMainTextColor(mainText, dynamicColor);
    }

    private void applyGlitch(Text textNode, double minOffset, double maxOffset) {
        if (random.nextInt(100) > 15) {
            textNode.setVisible(false);
            textNode.setTranslateX(0);
            textNode.setClip(null);
            return;
        }

        textNode.setVisible(true);
        double offsetX = minOffset + (maxOffset - minOffset) * random.nextDouble();
        textNode.setTranslateX(offsetX);

        double textHeight = textNode.getLayoutBounds().getHeight();
        double textWidth = textNode.getLayoutBounds().getWidth();
        double sliceY = random.nextDouble() * textHeight;
        double sliceHeight = Math.max(4, random.nextDouble() * textHeight * 0.25);
        Rectangle clip = new Rectangle(0, sliceY, textWidth, sliceHeight);
        textNode.setClip(clip);
    }

    private Font loadLogoFont() {
        // Try loading Orbitron or Impact, fall back to Arial Black
        Font preferred = Font.font("Orbitron", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 56);
        if (preferred.getFamily().toLowerCase().contains("orbitron")) {
            return preferred;
        }

        // Try Impact (matches CSS design)
        Font impact = Font.font("Impact", FontWeight.BOLD, 56);
        if (impact.getFamily().toLowerCase().contains("impact")) {
            return impact;
        }

        // Final fallback
        return Font.font("Arial Black", FontWeight.EXTRA_BOLD, 56);
    }
}
