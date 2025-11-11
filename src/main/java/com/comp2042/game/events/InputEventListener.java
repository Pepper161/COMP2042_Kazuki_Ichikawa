package com.comp2042.game.events;

import com.comp2042.board.ViewData;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateClockwise(MoveEvent event);

    ViewData onRotateCounterClockwise(MoveEvent event);

    DownData onHardDrop(MoveEvent event);

    ViewData createNewGame();
}
