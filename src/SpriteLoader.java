
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Loads sprites for Minesweeper UI
 * TODO: make methods static
 * @author justin
 */
class SpriteLoader {
    private BufferedImage tileSheet;
    private BufferedImage numSheet;
    private BufferedImage faceSheet;
    private Image icon;

    private BufferedImage[] tileSprites;
    private BufferedImage[] numSprites;
    private BufferedImage[] faceSprites;

    // Row x Col
    private final int[] tileGrouping = {2, 8};
    private final int[] numGrouping = {1, 10};
    private final int[] faceGrouping = {1, 5};

    // Height x Width
    private int[] tileDimensions;
    private int[] numDimensions;
    private int[] faceDimensions;

    SpriteLoader() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

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

        chopTileSprites();
        chopNumSprites();
        chopFaceSprites();
    }

    private void showError() {
        JOptionPane.showMessageDialog(null, "Missing Images!\nPlease make sure that " +
                "all of the sprite files are downloaded.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void chopTileSprites() {
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

    private void chopNumSprites() {
        int height = numDimensions[0];
        int width = numDimensions[1];

        // Only one row, so ignore that
        int cols = numGrouping[1];

        numSprites = new BufferedImage[cols];

        for (int c = 0; c < cols; c++) {
            numSprites[c] = numSheet.getSubimage(c * width, 0, width, height);
        }
    }

    private void chopFaceSprites() {
        int height = faceDimensions[0];
        int width = faceDimensions[1];

        // Only one row, so ignore that
        int cols = faceGrouping[1];

        faceSprites = new BufferedImage[cols];

        for (int c = 0; c < cols; c++) {
            faceSprites[c] = faceSheet.getSubimage(
                    c * width, 0, width, height);
        }
    }

    /**
     * Return tile sprite
     * @param num index to get from:
     *            0-1: normal, revealed (pushed down) /
     *            2: flagged /
     *            3-4: normal, revealed (pushed down) ?s /
     *            5-7: bombs: normal, red bg, red x /
     *            8-15: numbers (subtract 7):
     *                  e.g. 8 gets 1, 9 gets 2, etc
     * @return image of tile at that index
     */
    public  BufferedImage getTileSprite(int num) {
        return tileSprites[num];
    }

    /**
     * Return number sprite
     * @param num number sprite to get
     *            0 gets 0, 1 gets 1, etc
     * @return image of that number
     */
    public BufferedImage getNumberSprite(int num) {
        return numSprites[num];
    }

    /**
     * Return face sprite
     * @param num index to get it from:
     *            0 = smiley /
     *            1 = smiley (pressed down) /
     *            2 = :o /

     *            3 = sunglasses /
     *            4 = dead
     * @return button image
     */
    public BufferedImage getFaceSprite(int num) {
        return faceSprites[num];
    }

    public Image getIcon() {
        return icon;
    }

    public int[] getTileDimensions() {
        return tileDimensions;
    }

    public int[] getNumDimensions() {
        return numDimensions;
    }

    public  int[] getFaceDimensions() {
        return faceDimensions;
    }
}
