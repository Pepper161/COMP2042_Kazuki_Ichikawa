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

    /**
     * Creates a new handler with the specified action and timing configuration.
     *
     * @param action      the runnable to execute on repeat
     * @param dasDuration the initial delay before repeating starts
     * @param arrDuration the interval between repeats
     */
    public AutoRepeatHandler(Runnable action, Duration dasDuration, Duration arrDuration) {
        this.action = action;
        configure(dasDuration, arrDuration);
    }

    /**
     * Updates the timing configuration.
     *
     * @param dasDuration the new initial delay
     * @param arrDuration the new repeat interval
     */
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

    /**
     * Triggers the action immediately and starts the DAS/ARR cycle.
     */
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

    /**
     * Stops any active repeat cycles.
     */
    public void stop() {
        delayTransition.stop();
        if (repeatTimeline != null) {
            repeatTimeline.stop();
        }
    }
}
