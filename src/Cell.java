import java.awt.image.BufferedImage;

/**
 * Cell object
 * Tiles have a value between -1 and 8:
 * -1 for bombs, 0 for empty, 1-8 for a numbered tile
 * @author huynhstin
 */

class Cell {
    enum State {
        HIDDEN, FLAGGED, REVEALED, MARKED
    }

    private final SpriteLoader loader = new SpriteLoader();
    private State state = State.HIDDEN;
    private int value = 0;
    private boolean lastClicked = false;
    private final int MINE_VAL = -1;
    private final int EMPTY_VAL = 0;

    State getState() {
        return state;
    }

    void setState(final State state) {
        this.state = state;
    }

    void makeMine() {
        value = MINE_VAL;
    }

    boolean isMine() {
        return value == MINE_VAL;
    }

    boolean isEmpty() {
        return value == EMPTY_VAL;
    }

    /**
     * Change the state of the current cell to REVEALED.
     * Don't do this if the state is FLAGGED, since FLAGGED cells should
     *  stay flagged until un-flagged or until the game is won/lost.
     */
    void reveal() {
        if (state != State.FLAGGED) {
            state = State.REVEALED;
        }
    }

    /**
     * Flag the cell
     * @return number of flags to increase flag count by
     * @param markOption whether or not to consider marks when changing the state of the cell.
     */
    int flagCell(boolean markOption) {
        switch (state) {
            case HIDDEN:
                state = State.FLAGGED;
                return -1;
            case FLAGGED:
                state = markOption ? State.MARKED : State.HIDDEN;
                return 1;
            case MARKED:
                state = State.HIDDEN;
                return 0;
        }
        return 0;
    }

    int getValue() {
        return value;
    }

    /**
     * Increment the value of the cell, to be used when generating the board.
     * Do not increment the cell if it is a mine.
     */
    void increase() {
        if (value != MINE_VAL) {
            this.value++;
        }
    }

    void setLastClicked(boolean lastClicked) {
        this.lastClicked = lastClicked;
    }

    /**
     * Returns basic images associated with each cell.
     * This does not return images that rely on game state
     *  (e.g. dead/alive, click state): this is delegated to MinesUI.
     * @return image of cell
     */
    BufferedImage getImg() {
        switch (state) {
            case REVEALED:
                if (isMine()) {
                    // if last clicked, red bg; else normal bg
                    return lastClicked ? loader.getTileSprite(6) : loader.getTileSprite(5);
                } else if (isEmpty()) {
                    return loader.getTileSprite(1);
                } else if (value >= 1 && value <= 8) {
                    return loader.getTileSprite(7 + value);
                }
                break;
            case FLAGGED:
                return loader.getTileSprite(2);
            case HIDDEN:
                return loader.getTileSprite(0);
            case MARKED:
                return loader.getTileSprite(3);
        }
        return null;
    }
}