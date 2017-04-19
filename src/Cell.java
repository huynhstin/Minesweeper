/**
 * Cell object
 * Tiles have a value between -1 and 8:
 * -1 for bombs, 0 for empty, 1-8 for a numbered tile
 * @author justin
 */

import java.awt.image.BufferedImage;

class Cell {
    enum State {
        HIDDEN, FLAGGED, REVEALED, MARKED
    }
    private State state;
    private int value;
    private boolean lastClicked;
    private SpriteLoader s = new SpriteLoader();
    Cell() {
        this.value = 0;
        state = State.HIDDEN;
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
        if (state != State.REVEALED) {
            state = State.REVEALED;
        }
    }

    /**
     * Set flagCell
     * @return number of flags to increase by
     */
    int flagCell(boolean markOption) {
        if (state == State.HIDDEN) {
            state = State.FLAGGED;
            return -1; // remove one flagCell
        } else if (state == State.FLAGGED) {
            state = markOption ? State.MARKED : State.HIDDEN;
            return 1; // add one flagCell back
        } else if (state == State.MARKED) { // if markOption is false, it'll never get here
            state = State.HIDDEN;
        }
        return 0; // if state is revealed, add nothing
    }

    int getValue() {
        return value;
    }

    void increase() {
        if (value >= 0) { // don't increment bombs
            this.value++;
        }
    }

    void setLastClicked(boolean lastClicked) {
        this.lastClicked = lastClicked;
    }

    /**
     * Returns basic images associated with each cell.
     * This oes not return images that rely on game state
     *  (e.g. dead/alive, click state): this is delegated to MinesUI.
     * @return image of cell
     */
    BufferedImage getImg() {
        switch (state) {
            case REVEALED:
                if (isMine()) { // if last clicked, red bg; else normal bg
                    return lastClicked ? s.getTileSprite(6) : s.getTileSprite(5);
                } else if (value == 0) { // empty
                    return s.getTileSprite(1);
                } else if (value >= 1 && value <= 8) {
                    return s.getTileSprite(7 + value);
                }
                break;
            case FLAGGED:
                return s.getTileSprite(2);
            case HIDDEN:
                return s.getTileSprite(0);
            case MARKED:
                return s.getTileSprite(3);
        }
        return null;
    }
}