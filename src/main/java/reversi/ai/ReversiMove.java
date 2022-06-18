package reversi.ai;

import reversi.board.Tile;

public class ReversiMove
{
    private Tile piece;
    private float heuristic;

    public ReversiMove(Tile piece, float heuristic) {
        this.piece = piece;
        this.heuristic = heuristic;
    }

    public Tile getPiece() {
        return piece;
    }

    public void setPiece(Tile piece) {
        this.piece = piece;
    }

    public float getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(float heuristic) {
        this.heuristic = heuristic;
    }

    public String toString() {
        return "Piece: " + piece + ", Heuristic: " + heuristic;
    }
}
