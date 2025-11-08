package com.comp2042;

public class GameController implements InputEventListener {

    private final GuiController viewGuiController;
    private final Board board = new SimpleBoard(25, 10);
    private final GameLogic logic = new GameLogic(board);

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(logic.getScore());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return new DownData(null, board.getViewData());
        }
        DownData downData = logic.moveDown(event);
        syncBoardState();
        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.moveLeft();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.moveRight();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.rotate();
        syncBoardState();
        return data;
    }


    @Override
    public ViewData createNewGame() {
        ViewData viewData = logic.newGame();
        syncBoardState();
        return viewData;
    }

    private void syncBoardState() {
        if (logic.consumeBoardRefreshFlag()) {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }
        if (logic.consumeGameOverFlag()) {
            viewGuiController.gameOver();
        }
    }
}
