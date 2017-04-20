/**
 * Cell object
 * Tiles have a value between -1 and 8:
 * -1 for bombs, 0 for empty, 1-8 for a numbered tile
 * @author huynhstin
 */

import java.awt.image.BufferedImage;

class Cell {
    enum State {
        HIDDEN, FLAGGED, REVEALED, MARKED
    }

    private final SpriteLoader loader = new SpriteLoader();
    private State state;
    private int value;
    private boolean lastClicked;

    Cell() {
        state = State.HIDDEN;
        this.value = 0;
        lastClicked = false;
    }

    State getState() {
        return state;
    }

    void setState(final State state) {
        this.state = state;
    }

    void makeMine() {
        value = -1;
    }

    boolean isMine() {
        return value == -1;
    }

    void reveal() {
        if (state != State.REVEALED && state != State.FLAGGED) {
            state = State.REVEALED;
        }
    }

    /**
     * Flag the cell
     * @return number of flags to increase by
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

    void increase() {
        if (value != -1) { // don't increment bombs
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
                if (isMine()) { // if last clicked, red bg; else normal bg
                    return lastClicked ? loader.getTileSprite(6) : loader.getTileSprite(5);
                } else if (value == 0) { // empty
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