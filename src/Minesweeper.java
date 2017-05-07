import java.util.ArrayList;
import java.util.Random;

/**
 * Logic for Minesweeper
 * @author huynhstin
 */

class Minesweeper {
    private int rows;
    private int cols;
    private int mines;
    private int flagsLeft;
    private int revealed = 0;
    private final Cell[][] board;
    private int[][] mineLocations;
    private boolean dead = false;
    private boolean madeFirstMove = false;
    private boolean won = false;
    private final Random randy = new Random();
    private Cell lastClickedCell;
    private boolean markOption = false;

    /* This list holds the coordinates of all the Cells that have changed
     state, to avoid having to repaint all of the Cells on each click. */
    private final ArrayList<int[]> toPaint = new ArrayList<>();

    Minesweeper(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        board = new Cell[rows][cols];
        mineLocations = new int[mines][2];
        fillBoard();
        flagsLeft = mines;
        lastClickedCell = board[0][0]; // doesn't matter; will update on first click
    }

    int getFlagsLeft() {
        return flagsLeft;
    }

    /**
     * Flag the cell located at the given coordinates. <br>
     * Only flagCell if you have flags left, or if the cell you are trying to
     *  flagCell is already flagged, meaning that you are toggling.
     * @param r row
     * @param c column
     */
    void flag(int r, int c) {
        if (flagsLeft > 0 || board[r][c].getState() == Cell.State.FLAGGED) {
            flagsLeft += board[r][c].flagCell(markOption);
        }
    }

    Cell getPrevLastClicked() {
        return lastClickedCell;
    }

    void setLastClickedCell(Cell cell) {
        lastClickedCell = cell;
    }

    /**
     * Fills the board with new cells, and then generates all the mines.
     */
    private void fillBoard() {
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                board[r][c] = new Cell();
            }
        }
    }

    /**
     * Generates random coordinates to place mines in, and saves the locations
     *  of those mines to the mineLocations array.
     * @param num how many mines to generate.
     * @param ignore the coordinates of the cell to not generate a mine at
     */
    private void generate(int num, int[] ignore) {
        int minesLeft = num;
        while (minesLeft > 0) {
            int r = randy.nextInt(rows);
            int c = randy.nextInt(cols);
            if (!board[r][c].isMine() && r != ignore[0] && c != ignore[1]) {
                board[r][c].makeMine();
                incrSurround(r, c);
                mineLocations[minesLeft - 1] = new int[]{r, c};
                minesLeft--;
            }
        }
    }

    /**
     * Increment all the cells around a given coordinate
     * @param r row
     * @param c col
     */
    private void incrSurround(int r, int c) {
        incrCell(r + 1, c);
        incrCell(r - 1, c);
        incrCell(r, c + 1);
        incrCell(r, c - 1);
        incrCell(r + 1, c + 1);
        incrCell(r - 1, c - 1);
        incrCell(r + 1, c - 1);
        incrCell(r - 1, c + 1);
    }

    /**
     * Increment the cell at the given coordinate
     * @param r row
     * @param c col
     */
    private void incrCell(int r, int c) {
        if (inBound(r, c)) {
            board[r][c].increase();
        }
    }

    private boolean inBound(int r, int c) {
        return r >= 0 && r < this.rows && c >= 0 && c < this.cols;
    }

    Cell[][] getBoard() {
        return board;
    }

    boolean isDead() {
        return this.dead;
    }

    int[] getDimAndMines() {
        return new int[] {this.rows, this.cols, this.mines};
    }

    /**
     * Make a move
     * @param r row
     * @param c col
     * @param checkMine whether or not to check for mines (for recursive calls)
     */
    void move(int r, int c, boolean checkMine) {
        if (!dead && !won) {
            // only move if if it's within bound (for recursive calls), and if it's currently hidden or marked
            if (inBound(r, c) && (board[r][c].getState() == Cell.State.HIDDEN ||
                                       board[r][c].getState() == Cell.State.MARKED)) {

                // Generate the board on the first move, avoiding the first cell clicked
                if (!madeFirstMove) {
                    madeFirstMove = true;
                    generate(this.mines, new int[] {r, c});
                }

                if (!board[r][c].isMine()) { // reveal it, since it was hidden. do not reveal mines
                    board[r][c].reveal();
                    revealed++;
                    toPaint.add(new int[]{r, c});
                }

                if (board[r][c].isMine() && checkMine) { // if you hit a bomb, and we're looking for bombs, you dead.
                    dead = true;
                    revealOnDead();
                } else if (board[r][c].getValue() > 0 || board[r][c].isMine()) { // once we hit a number, get out
                    return;
                } else { // reveal all the ones around it
                    move(r + 1, c, false);
                    move(r - 1, c, false);
                    move(r, c + 1, false);
                    move(r, c - 1, false);
                    move(r + 1, c + 1, false);
                    move(r - 1, c - 1, false);
                    move(r + 1, c - 1, false);
                    move(r - 1, c + 1, false);
                }
            }
        }
    }

    ArrayList<int[]> getToPaint() {
        return toPaint;
    }

    /**
     * Checks to see if player won:
     * <P> Checks to see if the number of revealed tiles is
     *    equal to the number of slots minus the number of mines,
     *     implying that the player has revealed everything but the mines
     *     and has therefore won. </P>
     * <P> If this is true, then the method returns true and
     *     auto-flags all the mines, and adds those mines to the toPaint list. </P>
     * @return won or not
     */
    boolean checkWin() {
        if (revealed == (rows * cols) - mines) {
            // Auto-flag all the mines
            for (int[] mineLocation : mineLocations) {
                board[mineLocation[0]][mineLocation[1]].setState(Cell.State.FLAGGED);
                flagsLeft--;
                toPaint.add(new int[]{mineLocation[0], mineLocation[1]});
            }
            won = true;
        }
        return won;
    }

    boolean getWon() {
        return won;
    }

    /**
     * When game is over (dead is true), reveal all the mines
     */
    private void revealOnDead() {
        if (dead) {
            for (int[] mineLocation : mineLocations) {
                Cell cell = board[mineLocation[0]][mineLocation[1]];
                cell.reveal();
            }
        }
    }

    void toggleMarkOption() {
        this.markOption = !this.markOption;
    }

    void setMarkOption(boolean markOption) {
        this.markOption = markOption;
    }

    boolean isMarkOption() {
        return markOption;
    }
}