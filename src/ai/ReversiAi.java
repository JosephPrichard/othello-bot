package ai;

import board.ReversiBoard;
import board.ReversiPiece;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class ReversiAi
{
    public static int INF = Integer.MAX_VALUE;

    private final int maxDepth;

    private int exploredNodesCount = 0;

    private final ReversiBoard rootBoard;
    private ReversiNode rootNode;
    private final boolean playingBlack;

    public ReversiAi(int boardSize, int maxDepth, boolean playingBlack) {
        this.rootBoard = new ReversiBoard(boardSize);
        this.playingBlack = playingBlack;
        this.maxDepth = maxDepth;
    }

    public ReversiNode getRootNode() {
        return rootNode;
    }

    public int getExploredNodesCount() {
        return exploredNodesCount;
    }

    /**
     * Creates reversi game tree with iterative deepening depth first search
     */
    public void createReversiTreeIDfs() {
        rootNode = new ReversiNode(rootBoard.copy());
        Stack<ReversiNode> nodeStack = new Stack<>();

        // perform DLS iteratively, each time letting DLS run a little further
        for (int depthLimit = 0; depthLimit < maxDepth; depthLimit++) {
            exploredNodesCount = 0;

            // restart the search from the root node
            nodeStack.clear();
            nodeStack.add(rootNode);

            // iterate until we run out of nodes at given depth
            while (!nodeStack.empty()) {
                ReversiNode currentNode = nodeStack.pop();
                int currentDepth = currentNode.getDepth();

                exploredNodesCount++;

                // stop when we reach depth limit or cannot expand node's children
                if (currentDepth >= depthLimit || currentNode.isTerminal()) {
                    continue;
                }

                // check to see if this is the first time expanding the node out of all iterations
                if (currentNode.getChildren() == null) {
                    ReversiBoard currentBoard = currentNode.getReversiBoard();
                    List<ReversiNode> children = new ArrayList<>();
                    List<ReversiPiece> moves = currentBoard.findPotentialMoves();

                    // create a new board with a corresponding node for each move
                    for (int i = 0; i < moves.size(); i++) {
                        ReversiPiece move = moves.get(i);
                        ReversiBoard copiedBoard = currentBoard.copy();
                        copiedBoard.makeMove(move);

                        ReversiNode newNode = new ReversiNode(copiedBoard, currentDepth + 1);
                        newNode.setMoveIndex(i);

                        children.add(newNode);
                        nodeStack.add(newNode);
                    }
                    currentNode.setChildren(children);

                    if (children.size() > 0) {
                        // empty the board to save memory, a node expanded once never needs to be expanded again
                        currentBoard.emptyBoard();
                    }
                } else {
                    // node has previously been expanded so we can just push children to the stack
                    nodeStack.addAll(currentNode.getChildren());
                }
            }
        }
    }

    /**
     * Performs minimax algorithm to search reversi game tree and find the most optimal move
     */
    public void minimax() {
        Stack<ReversiNode> nodeStack = new Stack<>();
        nodeStack.add(rootNode);

        while (!nodeStack.empty()) {
            ReversiNode current = nodeStack.pop();

            if (current.isMaximizingPlayer()) {
                for (ReversiNode child : current.getChildren()) {
                    nodeStack.add(child);
                }
            } else {
                for (ReversiNode child : current.getChildren()) {
                    nodeStack.add(child);
                }
            }
        }
    }
}
