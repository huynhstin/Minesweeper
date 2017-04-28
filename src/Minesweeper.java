import java.util.ArrayList;
import java.util.Random;

/**
 * Logic for Minesweeper
 * @author huynhstin
 */

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
    private final Random randy = new Random();
    private Cell lastClickedCell;
    private boolean markOption = false;
    private final Difficulty diff;

    /* This list holds the coordinates of all the Cells that have changed
     state, to avoid having to repaint all of the Cells on each click. */
    private ArrayList<int[]> toPaint = new ArrayList<>();

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
        printMines();
    }

    /**
     * TODO delete this
     */
    void printMines() {
        for (int[] mineLoc : mineLocations) {
            System.out.printf("%d, %d\n", mineLoc[0], mineLoc[1]);
        }
        System.out.println("******");
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
                incrSurround(r, c, 1);
                mineLocations[minesLeft - 1] = new int[]{r, c};
                minesLeft--;
            }
        }
    }

    private void incrSurround(int r, int c, int amt) {
        incrCell(r + 1, c, amt);
        incrCell(r - 1, c, amt);
        incrCell(r, c + 1, amt);
        incrCell(r, c - 1, amt);
        incrCell(r + 1, c + 1, amt);
        incrCell(r - 1, c - 1, amt);
        incrCell(r + 1, c - 1, amt);
        incrCell(r - 1, c + 1, amt);
    }

    private void incrCell(int r, int c, int amt) {
        if (inBound(r, c)) {
            board[r][c].increase(amt);
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
                    if (board[r][c].isMine()) {
                        firstMoveDeath(r, c);
                        move(r, c);
                        return;
                    }
                }
                board[r][c].reveal(); // reveal it, since it was hidden
                revealed++;
                toPaint.add(new int[]{r, c});
                if (board[r][c].isMine()) { // if you hit a bomb, you dead.
                    System.out.printf("board at %d %d is a bomb!\n", r, c);
                    dead = true;
                    revealOnDead();
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

    /**
     * Prevents first move deaths (hitting a mine on your first click).
     * <P> Process: <br> Finds the location of that mine in the mineLocations array,
     *  then generates a new mine in a random location. <br> Then, put the new mine's
     *  coordinates at the index that the old mine used to occupy in the mineLocations array. </P>
     * @param r row
     * @param c col
     */
    //TODO: clear around current mine
    private void firstMoveDeath(int r, int c) {
        // mineLocations is saved as [numberOfMines][coordinates]
        // need to search for the index of where the current mine is saved, so we can remove it
        int locInArr = 0;
        int count = 0;
        for (int[] mineCoords : mineLocations) {
            if (mineCoords[0] == r && mineCoords[1] == c) {
                locInArr = count;
                System.out.println(locInArr);
            }
            count++;
        }

        // Modified version of generate method
        // Don't make current one empty until we generate, so it knows not to generate it at the current index
        while (true) { // go until we break
            int i = randy.nextInt(this.rows);
            int j = randy.nextInt(this.cols);
            if (!board[i][j].isMine() && board[i][j].isEmpty()) {
                board[i][j].makeMine();
                incrSurround(i, j, 1);
                mineLocations[locInArr] = new int[]{i, j};
                System.out.println("new mine locations:");
                printMines();
                break;
            }
        }
        board[r][c].setEmpty();
        incrSurround(r, c, -1);
    }

    ArrayList<int[]> getToPaint() {
        return toPaint;
    }

    /**
     * Clears the list of all the coordinates,
     * so we don't repaint the same cells over and over.
     */
    void clearToPaint() {
        toPaint.clear();
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
            for (int[] mineLocation : mineLocations) {
                Cell mine = board[mineLocation[0]][mineLocation[1]];
                if (mine.getState() != Cell.State.FLAGGED) {
                    mine.setState(Cell.State.FLAGGED);
                    flagsLeft--;
                    toPaint.add(new int[]{mineLocation[0], mineLocation[1]});
                }
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
