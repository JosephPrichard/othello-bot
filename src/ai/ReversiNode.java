package ai;

import board.ReversiBoard;

import java.util.List;

public class ReversiNode
{
    private final ReversiBoard reversiBoard;
    private List<ReversiNode> children = null;
    private int depth = 0;
    private int value = 0;
    private int moveIndex = 0;

    public ReversiNode(ReversiBoard reversiBoard) {
        this.reversiBoard = reversiBoard;
    }

    public ReversiNode(ReversiBoard reversiBoard, int depth) {
        this.reversiBoard = reversiBoard;
        this.depth = depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public void setMoveIndex(int moveIndex) {
        this.moveIndex = moveIndex;
    }

    public int getDepth() {
        return depth;
    }

    public List<ReversiNode> getChildren() {
        return children;
    }

    public void setChildren(List<ReversiNode> children) {
        this.children = children;
    }

    public ReversiBoard getReversiBoard() {
        return reversiBoard;
    }

    public boolean isMaximizingPlayer() {
        return reversiBoard.isBlackMove();
    }

    public boolean isTerminal() {
        return children != null && children.isEmpty();
    }

    public int heuristic() {
        return reversiBoard.blackScore() - reversiBoard.whiteScore();
    }
}
