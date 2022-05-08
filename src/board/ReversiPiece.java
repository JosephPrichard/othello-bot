package board;

public class ReversiPiece
{
    private final int row;
    private final int col;

    public ReversiPiece(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ReversiPiece(String square) {
        this.col = square.charAt(0) - 'a';
        this.row = Character.getNumericValue(square.charAt(1)) - 1;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String toString() {
        char c = (char) ((char) col + 'a');
        String r = Integer.toString(row + 1);
        return r + c;
    }
}
