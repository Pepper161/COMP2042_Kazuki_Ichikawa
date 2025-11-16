package com.comp2042.ui.input;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Handles DAS/ARR style repeats for movement keys.
 */
public class AutoRepeatHandler {

    private final Runnable action;
    private final PauseTransition delayTransition = new PauseTransition();
    private Timeline repeatTimeline;
    private Duration arrDuration = Duration.millis(30);
    private Duration dasDuration = Duration.millis(150);

    public AutoRepeatHandler(Runnable action, Duration dasDuration, Duration arrDuration) {
        this.action = action;
        configure(dasDuration, arrDuration);
    }

    public void configure(Duration dasDuration, Duration arrDuration) {
        stop();
        this.dasDuration = dasDuration != null ? dasDuration : Duration.ZERO;
        Duration interval = (arrDuration != null && !arrDuration.isIndefinite())
                ? arrDuration
                : Duration.millis(1);
        if (interval.lessThanOrEqualTo(Duration.ZERO)) {
            interval = Duration.millis(1);
        }
        this.arrDuration = interval;
        repeatTimeline = new Timeline(new KeyFrame(this.arrDuration, e -> action.run()));
        repeatTimeline.setCycleCount(Animation.INDEFINITE);
        delayTransition.setDuration(this.dasDuration);
        delayTransition.setOnFinished(e -> {
            action.run();
            repeatTimeline.playFromStart();
        });
    }

    public void start() {
        stop();
        action.run();
        if (repeatTimeline == null) {
            return;
        }
        if (dasDuration.lessThanOrEqualTo(Duration.ZERO)) {
            repeatTimeline.playFromStart();
        } else {
            delayTransition.playFromStart();
        }
    }

    public void stop() {
        delayTransition.stop();
        if (repeatTimeline != null) {
            repeatTimeline.stop();
        }
    }
}
