import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Loads sprites for Minesweeper UI
 * @author huynhstin
 */

class SpriteLoader {
    private BufferedImage tileSheet;
    private BufferedImage numSheet;
    private BufferedImage faceSheet;
    private Image icon;

    private BufferedImage[] tileSprites;
    private ImageIcon[] numSprites;
    private ImageIcon[] faceSprites;

    // Row x Col
    private static final int[] tileGrouping = {2, 8};
    private static final int[] numGrouping = {1, 10};
    private static final int[] faceGrouping = {1, 5};

    // Height x Width
    private int[] tileDimensions;
    private int[] numDimensions;
    private int[] faceDimensions;

    SpriteLoader() {
        try {
            URL tileURL = getClass().getClassLoader().getResource("sprites/tiles.png");
            URL numURL = getClass().getClassLoader().getResource("sprites/clock.png");
            URL faceURL = getClass().getClassLoader().getResource("sprites/faces.png");
            URL iconURL = getClass().getClassLoader().getResource("sprites/icon.png");

            if (tileURL != null) {
                tileSheet = ImageIO.read(tileURL);
            } else {
                showError();
            }

            if (numURL != null) {
                numSheet = ImageIO.read(numURL);
            } else {
                showError();
            }

            if (faceURL != null) {
                faceSheet = ImageIO.read(faceURL);
            } else {
                showError();
            }

            if (iconURL != null) {
                icon = new ImageIcon(iconURL).getImage();
            } // don't need to show error, it will just use default java icon
        } catch (IOException ex) {
            showError();
            System.exit(1);
        }

        tileDimensions = new int[] {tileSheet.getHeight() / tileGrouping[0],
                                    tileSheet.getWidth() / tileGrouping[1]};
        numDimensions = new int[] {numSheet.getHeight() / numGrouping[0],
                                   numSheet.getWidth() / numGrouping[1]};
        faceDimensions = new int[] {faceSheet.getHeight() / faceGrouping[0],
                                    faceSheet.getWidth() / faceGrouping[1]};

        loadTileSprites();
        loadNumSprites();
        loadFaceSprites();
    }

    /**
     * Show an error message when a sprite file is missing.
     */
    private void showError() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        JOptionPane.showMessageDialog(null, "Missing Images!\nPlease make sure that " +
                "all of the sprite files are downloaded.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Chop up the tile sprite sheet into individual images,
     * and then store them in an array.
     */
    private void loadTileSprites() {
        int height = tileDimensions[0];
        int width = tileDimensions[1];

        int rows = tileGrouping[0];
        int cols = tileGrouping[1];
        tileSprites = new BufferedImage[rows * cols];

        int counter = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tileSprites[counter] = tileSheet.getSubimage(c * width, r * height, width, height);
                counter++;
            }
        }
    }

    private void loadNumSprites() {
        int height = numDimensions[0];
        int width = numDimensions[1];

        // Only one row, so ignore rows
        int cols = numGrouping[1];

        numSprites = new ImageIcon[cols];

        for (int c = 0; c < cols; c++) {
            numSprites[c] = new ImageIcon(numSheet.getSubimage(c * width, 0, width, height));
        }
    }

    private void loadFaceSprites() {
        int height = faceDimensions[0];
        int width = faceDimensions[1];

        // Only one row, so ignore rows
        int cols = faceGrouping[1];

        faceSprites = new ImageIcon[cols];

        for (int c = 0; c < cols; c++) {
            faceSprites[c] = new ImageIcon(faceSheet.getSubimage(
                    c * width, 0, width, height));
        }
    }

    /**
     * Return tile sprite
     * @param num index to get from:
     *        <P> 0-1: normal, revealed (pushed down) <br>
     *            2: flagged <br>
     *            3-4: normal, revealed (pushed down) ?s <br>
     *            5-7: bombs: normal, red bg, red x <br>
     *            8-15: numbers (subtract 7): e.g. 8 gets 1, 9 gets 2, etc </P>
     * @return image of tile at that index
     */
    BufferedImage getTileSprite(int num) {
        return tileSprites[num];
    }

    /**
     * Return number sprite
     * @param num number sprite to get: 0 gets 0, 1 gets 1, etc
     * @return image of that number
     */
    ImageIcon getNumberSprite(int num) {
        return numSprites[num];
    }

    /**
     * Return face sprite
     * @param num index to get it from:
     *        <P> 0 = smiley <br>
     *            1 = smiley (pressed down) <br>
     *            2 = :o <br>
     *            3 = sunglasses <br>
     *            4 = dead </P>
     * @return button image
     */
    ImageIcon getFaceSprite(int num) {
        return faceSprites[num];
    }

    Image getIcon() {
        return icon;
    }

    int[] getTileDimensions() {
        return tileDimensions;
    }

    int[] getNumDimensions() {
        return numDimensions;
    }

    int[] getFaceDimensions() {
        return faceDimensions;
    }
}