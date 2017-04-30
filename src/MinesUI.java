import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * GUI for Minesweeper game
 * @author huynhstin
 */

public class MinesUI {
    private Minesweeper game = new Minesweeper(Minesweeper.Difficulty.BEGINNER);
    private final SpriteLoader loader = new SpriteLoader();
    private final JFrame frame = new JFrame();
    private final JButton faceButton = new JButton();
    private final Color background = new Color(192, 192, 192);
    private final FlagDisplay flagger = new FlagDisplay();
    private final DigClock clock = new DigClock();
    private Grid grid = new Grid();

    private MinesUI() {
        init();
        frame.setVisible(true);
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Minesweeper");
        frame.setBackground(background);
        frame.setIconImage(loader.getIcon());

        /* Menu */
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(236, 233, 216));
        JMenu menu = new JMenu("Game");

        JMenuItem newGameOption = new JMenuItem("New");
        newGameOption.addActionListener(e -> reset());
        newGameOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

        JSeparator separatorOne = new JSeparator();
        separatorOne.setPreferredSize(new Dimension(0, 1));

        JMenuItem easyOption = new JMenuItem("Beginner");
        easyOption.addActionListener(e -> resetNewDiff(Minesweeper.Difficulty.BEGINNER));
        JMenuItem medOption = new JMenuItem("Intermediate");
        medOption.addActionListener(e -> resetNewDiff(Minesweeper.Difficulty.INTERMEDIATE));
        JMenuItem hardOption = new JMenuItem("Expert");
        hardOption.addActionListener(e -> resetNewDiff(Minesweeper.Difficulty.EXPERT));

        JSeparator separatorTwo = new JSeparator();
        separatorTwo.setPreferredSize(new Dimension(0, 1));

        JCheckBoxMenuItem marksOn = new JCheckBoxMenuItem("Marks (?)");
        marksOn.addActionListener(e -> game.toggleMarkOption());
        marksOn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0));

        JSeparator separatorThree = new JSeparator();
        separatorThree.setPreferredSize(new Dimension(0, 1));

        JMenuItem exitOption = new JMenuItem("Exit");
        exitOption.addActionListener(e -> System.exit(0));

        menu.add(newGameOption);
        menu.add(separatorOne);
        menu.add(easyOption);
        menu.add(medOption);
        menu.add(hardOption);
        menu.add(separatorTwo);
        menu.add(marksOn);
        menu.add(separatorThree);
        menu.add(exitOption);
        menuBar.add(menu);

        JMenu help = new JMenu("Help");
        JMenuItem link = new JMenuItem("How to Play");
        link.addActionListener(event -> {
            try {
                Desktop.getDesktop().browse(new URL("http://www.minesweeper.info/wiki/Strategy").toURI());
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        });
        link.setToolTipText("Will open browser.");

        help.add(link);
        menuBar.add(help);
        frame.setJMenuBar(menuBar);

        /* Button */
        faceButton.addActionListener(actionEvent -> reset());
        faceButton.setContentAreaFilled(false);
        faceButton.setPreferredSize(new Dimension(loader.getFaceDimensions()[0],
                                    loader.getFaceDimensions()[1]));
        faceButton.setFocusable(false);
        faceButton.setBorder(null);
        faceButton.setIcon(loader.getFaceSprite(0));
        faceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                faceButton.setIcon(loader.getFaceSprite(1));
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                faceButton.setIcon(loader.getFaceSprite(0));
            }
        });

        /* Panels */
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(background);
        buttonPanel.add(faceButton);
        buttonPanel.setBorder(null);

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        topPanel.setLayout(new GridLayout(1, 3));
        topPanel.add(flagger);
        topPanel.add(buttonPanel);
        topPanel.add(clock);
        topPanel.setBackground(background);
        frame.add(topPanel, BorderLayout.NORTH);

        frame.add(grid);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    /**
     * Reset game with the same difficulty as before.
     * Do this by creating a new Minesweeper object, and resetting timer and flags.
     * Note that we do not need to create a new Grid object:
     *      we only have to update its sprites since the dimensions will remain the same.
     */
    private void reset() {
        boolean marks = game.isMarkOption();
        game = new Minesweeper(game.getDiff());
        game.setMarkOption(marks);

        flagger.updateFlags();

        clock.resetTimer();

        faceButton.setIcon(loader.getFaceSprite(0));

        frame.revalidate();
        grid.repaint();
        grid.updateImgs();
        System.gc();
    }

    /**
     * Reset game with a new difficulty setting
     * @param gameDiff new difficulty of game
     */
    private void resetNewDiff(Minesweeper.Difficulty gameDiff) {
        // Don't do anything if we're already at that difficulty.
        if (gameDiff == game.getDiff()) {
            return;
        }

        boolean marks = game.isMarkOption();
        game = new Minesweeper(gameDiff);
        game.setMarkOption(marks);

        frame.remove(grid);

        grid.killAll();
        grid.removeAll();
        grid = new Grid();
        frame.add(grid);

        flagger.updateFlags();

        clock.resetTimer();

        faceButton.setIcon(loader.getFaceSprite(0));

        frame.revalidate();
        frame.pack();
        System.gc();
    }

    private void endGameCheck() {
        if (game.isDead()) {
            clock.endTimer();
            faceButton.setIcon(loader.getFaceSprite(4));
            grid.repaint();
        } else if (game.checkWin()) {
            clock.endTimer();
            faceButton.setIcon(loader.getFaceSprite(3));
            grid.updateImgs();
        }
    }

    class DigClock extends JComponent {
        private final int DIGITS = 3;
        private int secs = 1;
        private boolean started = false;
        private JLabel[] timeLabels = new JLabel[DIGITS];
        private Timer t;

        DigClock() {
            super();
            this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            this.setBorder(new EmptyBorder(7, 0, 0, 15));
            this.setBackground(background);

            // Initialize to all 0 sprites
            for (int i = 0; i < DIGITS; i++) {
                timeLabels[i] = new JLabel(loader.getNumberSprite(0));
                this.add(timeLabels[i]);
            }

            t = new Timer(1000, e -> {
                secs++;
                if (secs > 999) {
                    secs = 0;
                }

                String model = String.format("%03d", secs);
                for (int i = 0; i < DIGITS; i++) {
                    timeLabels[i].setIcon(loader.getNumberSprite(Character.getNumericValue(model.charAt(i))));
                }
                this.repaint();
            });
        }

        void startTimer() {
            started = true;

            // Make timer display "001" on first click.
            timeLabels[2].setIcon(loader.getNumberSprite(1));
            this.repaint();
            t.start();
        }

        void endTimer() {
            started = false;
            t.stop();
        }

        void resetTimer() {
            started = false;
            t.stop();
            secs = 1;
            // Reset timer to "000"
            for (int i = 0; i < DIGITS; i++) {
                timeLabels[i].setIcon(loader.getNumberSprite(0));
            }
        }

        boolean isStarted() {
            return started;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(loader.getNumDimensions()[0],
                    loader.getNumDimensions()[1] * DIGITS);
        }
    }

    /**
     * The JComponent that displays the remaining flags.
     */
    class FlagDisplay extends JComponent {
        private final int DIGITS = 3;
        private JLabel[] digitLabels = new JLabel[DIGITS];

        FlagDisplay() {
            super();
            for (int i = 0; i < DIGITS; i++) {
                digitLabels[i] = new JLabel();
                this.add(digitLabels[i]);
            }
            updateFlags();
            this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            this.setBorder(new EmptyBorder(7, 15, 0, 0));
            this.setBackground(background);
        }

        void updateFlags() {
            String model = String.format("%03d", game.getFlagsLeft());
            for (int i = 0; i < DIGITS; i++) {
                digitLabels[i].setIcon(loader.getNumberSprite(Character.getNumericValue(model.charAt(i))));
            }
            this.repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(loader.getNumDimensions()[0],
                    loader.getNumDimensions()[1] * DIGITS);
        }
    }

     class Grid extends JPanel {
        private Square[][] cells;
        private int rows;
        private int cols;
        Grid() {
            rows = game.getDim()[0];
            cols = game.getDim()[1];
            createBoard();
            this.setBackground(background);
            this.setBorder(new EmptyBorder(5, 10, 10, 10));
        }

        void createBoard() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            cells = new Square[rows][cols];

            // Create grid and borders, add squares to cells array
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    gbc.gridx = c;
                    gbc.gridy = r;
                    Square square = new Square(r, c);

                    /* Since the sprites only have the top and left edge painted,
                       we need to draw in the bottom and right edges manually
                       if the tile is at the very bottom and/or right edges. */
                    final Color borderColor = new Color(128, 128, 128);
                    MatteBorder border = null;
                    if (r == game.getRows() - 1 && c == game.getCols() - 1) {
                        border = new MatteBorder(0, 0, 1, 1, borderColor); // bottom right
                    } else if (r == game.getRows() - 1) {
                        border = new MatteBorder(0, 0, 1, 0, borderColor); // bottom
                    } else if (c == game.getCols() - 1) {
                        border = new MatteBorder(0, 0, 0, 1, borderColor); // right edge
                    }

                    cells[r][c] = square;
                    square.setBorder(border);
                    this.add(square, gbc);
                }
            }
        }

        /**
         * Repaint only the Squares that appear in changedList, then clear that list.
         */
        void updateImgs() {
            for (int[] coords : game.getToPaint()) {
                cells[coords[0]][coords[1]].repaint();
            }
            game.getToPaint().clear();
        }

        /**
         * Remove all the squares from the array, to kill the listeners and free up memory.
         */
        void killAll() {
            for (int r = 0; r < this.rows; r++) {
                for (int c = 0; c < this.cols; c++) {
                    cells[r][c].removeAll();
                }
            }
        }
    }

    /**
     * The graphic wrapper for the Cell class
     */
    class Square extends JComponent {
        private final int row;
        private final int col;
        private boolean selected = false;

        private Square(int row, int col) {
            this.row = row;
            this.col = col;
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (!game.isDead() && !game.getWon()) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            game.flag(row, col);
                            flagger.updateFlags();
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            if (game.getBoard()[row][col].getState() != Cell.State.REVEALED) {
                                // Don't move until you release the mouse button
                                selected = true;
                            }
                            faceButton.setIcon(loader.getFaceSprite(2));
                        } else if (SwingUtilities.isMiddleMouseButton(e)) {
                            faceButton.setIcon(loader.getFaceSprite(2));
                        }
                    }
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    if (!game.isDead() && !game.getWon()) {
                        faceButton.setIcon(loader.getFaceSprite(0));
                        if (selected) {
                            if (!clock.isStarted()) {
                                clock.startTimer();
                            }
                            game.move(row, col, true);

                            // last click bool for red bg bombs
                            if (game.getBoard()[row][col].isMine()) {
                                game.getPrevLastClicked().setLastClicked(false);
                                game.getBoard()[row][col].setLastClicked(true);
                                game.setLastClickedCell(game.getBoard()[row][col]);
                            }

                            grid.updateImgs();
                            repaint();
                            selected = false;
                        }
                        endGameCheck();
                        repaint();
                    }
                }

                /**
                 * When the mouse exits the Square, the Square is no longer
                 *  considered 'selected', so make that boolean false and repaint.
                 */
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    selected = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            Cell cell = game.getBoard()[row][col];
            BufferedImage tileImg = cell.getImg();

            // On death, if the cell wasn't actually a mine but it was flagged, draw a red X mine
            if (game.isDead() && cell.getState() == Cell.State.FLAGGED && !cell.isMine()) {
                tileImg = loader.getTileSprite(7);
            }

            // "Pushed in" sprites
            if (selected) {
                if (cell.getState() == Cell.State.MARKED) {
                    tileImg = loader.getTileSprite(4);
                } else if (cell.getState() != Cell.State.FLAGGED) {
                    tileImg = loader.getTileSprite(1);
                }
            }
            g2.drawImage(tileImg, 0, 0, loader.getTileDimensions()[0],
                    loader.getTileDimensions()[1], this);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(loader.getTileDimensions()[0],
                    loader.getTileDimensions()[1]);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MinesUI::new);
    }
}