/**
 * Logic for Minesweeper
 * @author huynhstin
 */

import java.util.ArrayList;
import java.util.Random;

class Minesweeper {
    enum Difficulty {
        BEGINNER, INTERMEDIATE, EXPERT
    }

    private int rows;
    private int cols;
    private int mines;
    private int flagsLeft;
    private int revealed = 0;
    private Cell[][] board;
    private int[][] mineLocations;
    private boolean dead = false;
    private boolean madeFirstMove = false;
    private boolean won = false;
    private Random randy = new Random();
    private Cell lastClickedCell;
    private boolean markOption = false;
    private Difficulty diff;

    /* This list holds the coordinates of all the Cells that have changed
     state, to avoid having to repaint all of the Cells on each click. */
    private ArrayList<int[]> changedList = new ArrayList<>();

    Minesweeper(Difficulty diff) {
        this.diff = diff;
        switch (diff) {
            case BEGINNER:
                rows = 9;
                cols = 9;
                mines = 10;
                break;
            case INTERMEDIATE:
                rows = 16;
                cols = 16;
                mines = 40;
                break;
            case EXPERT:
                rows = 16;
                cols = 30;
                mines = 99;
                break;
        }
        board = new Cell[rows][cols];
        mineLocations = new int[mines][2];
        fillBoard();
        flagsLeft = mines;
        lastClickedCell = board[0][0]; // doesn't matter; will update on first click
    }

    int getFlagsLeft() {
        return flagsLeft;
    }

    void flag(int r, int c) {
        /* Only flagCell if you have flags left, or if the cell you are trying to
           flagCell is already flagged, meaning that you are trying to toggle. */
        if (flagsLeft > 0 || board[r][c].getState() == Cell.State.FLAGGED) {
            flagsLeft += board[r][c].flagCell(markOption);
        }
    }

    Difficulty getDiff() {
        return this.diff;
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
        generate(this.mines);
    }

    /**
     * Generates random coordinates to place mines in, and saves the locations
     *  of those mines to the mineLocations array.
     * @param num how many mines to generate.
     */
    private void generate(int num) {
        int minesLeft = num;
        while (minesLeft > 0) {
            int r = randy.nextInt(rows);
            int c = randy.nextInt(cols);
            if (!board[r][c].isMine()) {
                board[r][c].makeMine();
                incrSurround(r, c);
                mineLocations[minesLeft - 1] = new int[]{r, c};
                minesLeft--;
            }
        }
    }

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

    int[] getDim() {
        return new int[] {this.rows, this.cols};
    }

    void move(int r, int c) {
        if (!dead && !won) {

            // only move if if it's within bound (for recursive calls), and if it's currently hidden or marked
            if (inBound(r, c) && (board[r][c].getState() == Cell.State.HIDDEN ||
                                       board[r][c].getState() == Cell.State.MARKED)) {
                if (!madeFirstMove) {
                    madeFirstMove = true;

                    /* If you hit a mine on your first move, generate a new board.
                       There is still a chance that the new spot will be a mine,
                       but if their luck is that bad, they probably deserve it. */
                    if (board[r][c].isMine()) {
                        Cell.State state = board[r][c].getState();
                        fillBoard();
                        move(r, c);
                        if (state == Cell.State.FLAGGED || state == Cell.State.MARKED) {
                            board[r][c].setState(state);
                        }
                        return;
                    }
                }
                board[r][c].reveal(); // reveal it, since it was hidden
                revealed++;
                changedList.add(new int[]{r, c});
                if (board[r][c].isMine()) { // if you hit a bomb, you dead
                    dead = true;
                } else if (board[r][c].getValue() > 0) { // once we hit a number, get out
                    return;
                } else { // reveal all the ones around it
                    move(r + 1, c);
                    move(r - 1, c);
                    move(r, c + 1);
                    move(r, c - 1);
                    move(r + 1, c + 1);
                    move(r - 1, c - 1);
                    move(r + 1, c - 1);
                    move(r - 1, c + 1);
                }
            }
        }
    }

    ArrayList<int[]> getChangedList() {
        return changedList;
    }

    /**
     * Clears the list of all the coordinates,
     * so we don't repaint the same cells over and over.
     */
    void clearChangedList() {
        changedList.clear();
    }

    /**
     * Check if won
     * @return true if the number of revealed tiles is equal to the total
     *         number of slots - the number of mines, implying that
     *         player has revealed everything but the mines.
     */
    boolean checkWin() {
        if (revealed == (rows * cols) - mines) {
            for (int[] mineLocation : mineLocations) {
                flag(mineLocation[0], mineLocation[1]);
                changedList.add(new int[]{mineLocation[0], mineLocation[1]});
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
    void revealOnDead() {
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

    int getRows() {
        return rows;
    }

    int getCols() {
        return cols;
    }
}
