package reversi.ai;

import reversi.board.ReversiBoard;
import reversi.board.Tile;

import java.util.ArrayList;
import java.util.List;

public final class ReversiAi
{
    private static final float INF = Float.MAX_VALUE;
    private static final int TT_SIZE = (int) Math.pow(2, 16) + 1;
    private static final int CL_SIZE = 10;

    private final int maxDepth;
    private final ReversiBoard rootBoard;

    private final ZHasher hasher;
    private final TTable tTable;

    public ReversiAi(int boardSize, int maxDepth) {
        this.rootBoard = new ReversiBoard(boardSize);
        this.maxDepth = maxDepth;
        this.hasher = new ZHasher(boardSize);
        this.tTable = new TTable(TT_SIZE, CL_SIZE);
    }

    public ReversiAi(ReversiBoard board, int maxDepth) {
        this.rootBoard = board.copy();
        this.maxDepth = maxDepth;
        this.hasher = new ZHasher(board.getBoardSize());
        this.tTable = new TTable(TT_SIZE, CL_SIZE);
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public ReversiBoard getRootBoard() {
        return rootBoard;
    }

    public ReversiMove findBestMove() {
        List<Tile> moves = rootBoard.findPotentialMoves();
        Tile bestMove = null;
        float bestHeuristic = -INF;

        // call the iterative deepening negamax to calculate the heuristic for each potential move and determine the best one
        for (Tile move : moves) {
            ReversiBoard copiedBoard = rootBoard.copy();
            copiedBoard.makeMove(move);

            float heuristic = negamaxIDDFS(copiedBoard, maxDepth - 1);
            if (heuristic > bestHeuristic) {
                bestMove = move;
                bestHeuristic = heuristic;
            }
        }

        return new ReversiMove(bestMove, bestHeuristic);
    }

    /**
     * Searches reversi game tree with iterative deepening depth first search
     * Starts from a relative depth of 1 until a specified relative max depth
     */
    public float negamaxIDDFS(ReversiBoard board, int maxDepth) {
        float heuristic = 0;
        for (int depthLimit = 1; depthLimit < maxDepth; depthLimit++) {
            heuristic = negamax(board.copy(), depthLimit, board.isBlackMove(), -INF, INF);
        }
        return heuristic;
    }

    /**
     * Searches reversi game tree DLS with alpha beta pruning to evaluate how good a board is
     */
    public float negamax(
        ReversiBoard board,
        int depth,
        boolean maximizer,
        float alpha,
        float beta
    ) {
        List<Tile> moves = board.findPotentialMoves();

        // stop when we reach depth floor or cannot expand node's children
        if (depth == 0 || moves.isEmpty()) {
            return board.heuristic();
        }

        // find the children for the board state
        List<ReversiBoard> children = new ArrayList<>();
        // create a new child board with a corresponding node for each move
        for (Tile move : moves) {
            ReversiBoard copiedBoard = board.copy();
            copiedBoard.makeMove(move);
            children.add(copiedBoard);
        }

        if (maximizer) {
            // explore best children first for move ordering, find the best moves and return them
            for (ReversiBoard child : children) {
                alpha = Math.max(alpha, negamax(child, depth - 1, false, alpha, beta));
                // prune this branch, it cannot possibly be better than any child found so far
                if (alpha >= beta) {
                    break;
                }
            }
            // empty the board to save memory, a node expanded once never needs to be expanded again
            board.setBoardEmpty();
            return alpha;
        } else {
            // explore best children first for move ordering, find the best moves and return them
            for (ReversiBoard child : children) {
                beta = Math.min(beta, negamax(child, depth - 1, true, alpha, beta));
                // prune this branch, it cannot possibly be better than any child found so far
                if (beta <= alpha) {
                    break;
                }
            }
            // empty the board to save memory, a node expanded once never needs to be expanded again
            board.setBoardEmpty();
            return beta;
        }
    }
}
