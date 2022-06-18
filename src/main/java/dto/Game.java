package dto;

import reversi.board.ReversiBoard;

import javax.annotation.Nullable;

public class Game
{
    private final ReversiBoard board;
    private Player whitePlayer;
    private Player blackPlayer;

    public Game() {
        board = new ReversiBoard();
    }

    public ReversiBoard getBoard() {
        return board;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public Player getCurrentPlayer() {
        return board.isBlackMove() ? blackPlayer : whitePlayer;
    }

    public Player getOtherPlayer() {
        return board.isBlackMove() ? whitePlayer : blackPlayer;
    }

    public String getScore() {
        return (int) board.blackScore() + " - " + (int) board.whiteScore();
    }

    @Nullable
    public Player getWinner() {
        float diff = board.blackScore() - board.whiteScore();
        if (diff > 0) {
            return whitePlayer;
        } else if (diff < 0) {
            return blackPlayer;
        } else {
            return null;
        }
    }
}
