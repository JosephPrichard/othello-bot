package board;

import utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public final class ReversiBoard
{
    public static final byte EMPTY = 0;
    public static final byte WHITE = 1;
    public static final byte BLACK = 2;

    private static final int[][] BOARD_DIRECTIONS = {
        {0, 1}, {0, -1}, {1, 0}, {-1, 0},
        {-1, -1}, {-1, 1}, {-1, 1}, {1, 1}
    };

    private byte[][] board;
    private int numWhitePieces;
    private int numBlackPieces;
    private boolean blackMove;

    public ReversiBoard() {
        this(8);
    }

    public ReversiBoard(int boardSize) {
        board = new byte[boardSize][boardSize];

        board[getBoardSize() / 2 - 1][getBoardSize() / 2 - 1] = WHITE;
        board[getBoardSize() / 2][getBoardSize() / 2] = WHITE;
        board[getBoardSize() / 2 - 1][getBoardSize() / 2] = BLACK;
        board[getBoardSize() / 2][getBoardSize() / 2 - 1] = BLACK;

        blackMove = true;
        numWhitePieces = numBlackPieces = 2;
    }

    public ReversiBoard(ReversiBoard reversiBoard) {
        this.board = ArrayUtils.deepCopyOf2DArray(reversiBoard.board);
        this.blackMove = reversiBoard.blackMove;
        this.numWhitePieces = reversiBoard.numWhitePieces;
        this.numBlackPieces = reversiBoard.numBlackPieces;
    }

    public int getBoardSize() {
        return board.length;
    }

    public byte[][] getBoard() {
        return board;
    }

    public boolean isBlackMove() {
        return blackMove;
    }

    public int whiteScore() {
        return numWhitePieces;
    }

    public int blackScore() {
        return numBlackPieces;
    }

    public ReversiBoard copy() {
        return new ReversiBoard(this);
    }

    /**
     * Deallocates the board array while retaining other information about the state
     */
    public void emptyBoard() {
        board = null;
    }

    /**
     * Check if a row column pair is in value
     * @param row of position
     * @param col of position
     * @return whether the pair is potentially a valid board position
     */
    public boolean inBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < getBoardSize() && col < getBoardSize();
    }

    /**
     * Searches the board for all reversi pieces of matching color
     * @param color to find pieces for
     * @return list containing positions of the pieces
     */
    public List<ReversiPiece> findPieces(int color) {
        List<ReversiPiece> pieces = new ArrayList<>();
        int maxPieces = color == WHITE ? numWhitePieces : numBlackPieces;

        // iterate through each square and find the pieces
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                if (pieces.size() >= maxPieces)
                    break;
                if (board[i][j] == color)
                    pieces.add(new ReversiPiece(i, j));
            }
        }
        return pieces;
    }

    /**
     * Find the available moves the reversi board depending on the board state
     * @return a list containing the moves
     */
    public List<ReversiPiece> findPotentialMoves() {
        List<ReversiPiece> moves = new ArrayList<>();

        List<ReversiPiece> pieces = blackMove ? findPieces(BLACK) : findPieces(WHITE);
        int oppositeColor = blackMove ? WHITE : BLACK;

        // check each placed piece for potential flanks
        for (ReversiPiece piece : pieces) {
            // check each direction from placed piece for potential flank
            for (int[] direction : BOARD_DIRECTIONS) {
                int row = piece.getRow() + direction[0];
                int col = piece.getCol() + direction[1];

                // iterate from placed piece to next opposite color
                int count = 0;
                while (inBounds(row, col)) {
                    if (board[row][col] != oppositeColor)
                        break;
                    row += direction[0];
                    col += direction[1];
                    count++;
                }
                // add move to potential moves list
                if (count > 0 && inBounds(row, col)) {
                    moves.add(new ReversiPiece(row, col));
                }
            }
        }
        return moves;
    }

    /**
     * Makes a move on a position on the reversi board
     * @param move to make move
     */
    public void makeMove(ReversiPiece move) {
        byte oppositeColor;
        byte currentColor;
        int incBlack;
        if (blackMove) {
            oppositeColor = WHITE;
            currentColor = BLACK;
            incBlack = 1;
        } else {
            oppositeColor = BLACK;
            currentColor = WHITE;
            incBlack = -1;
        }

        blackMove = !blackMove;
        board[move.getRow()][move.getCol()] = currentColor;

        // check each direction of new piece position
        for (int[] direction : BOARD_DIRECTIONS) {
            int initialRow = move.getRow() + direction[0];
            int initialCol = move.getCol() + direction[1];

            int row = initialRow;
            int col = initialCol;

            boolean flank = false;

            // iterate from placed piece until first potential flank
            while (inBounds(row, col)) {
                if (board[row][col] == currentColor) {
                    flank = true;
                    break;
                } else if (board[row][col] == EMPTY) {
                    break;
                }
                row += direction[0];
                col += direction[1];
            }

            if (!flank) {
                continue;
            }

            row = initialRow;
            col = initialCol;

            // flip each piece to opposite color to flank, update piece counts
            while (inBounds(row, col)) {
                if (board[row][col] != oppositeColor)
                    break;

                board[row][col] = currentColor;
                numBlackPieces += incBlack;
                numWhitePieces += -incBlack;

                row += direction[0];
                col += direction[1];
            }
        }
    }

    /**
     * Sets a square to a value on the board by reversi board notation
     * @param square in reversi board notation
     * @param color to set, must be one of the constants
     */
    public void setSquare(String square, byte color) {
        int col = square.charAt(0) - 'a';
        int row = Character.getNumericValue(square.charAt(1)) - 1;
        board[row][col] = color;
    }

    /**
     * Converts the internal board to a parsable, readable string
     * @return internal board as a string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // add space for better board indentation
        builder.append("  ");
        // add each column header as letter
        for (int i = 0; i < getBoardSize(); i++) {
            builder.append((char)('a' + (char)i));
            builder.append(" ");
        }
        builder.append("\n");
        // add each matrix element in board with row header
        for (int i = 0; i < getBoardSize(); i++) {
            builder.append(i + 1);
            builder.append(" ");
            for (int j = 0; j < getBoardSize(); j++) {
                builder.append(board[i][j]);
                builder.append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
