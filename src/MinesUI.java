/**
 * GUI for Minesweeper game
 * @author huynhstin
 */

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

import java.net.URL;

import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.ImageIcon;
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

public class MinesUI {
    private SpriteLoader s = new SpriteLoader();
    private Minesweeper.Difficulty gameDiff;
    private Minesweeper game;
    private JFrame frame = new JFrame();
    private JButton faceButton = new JButton();
    private JPanel topPanel;
    private Dimension windowSize; // gets updated in constructor
    private DigClock clock = new DigClock();
    private FlagTicker flagger;
    private Grid grid;
    private final Color background = new Color(192, 192, 192);

    private MinesUI(Minesweeper.Difficulty gameDiff) {
        this.gameDiff = gameDiff;
        game = new Minesweeper(gameDiff);
        flagger = new FlagTicker();
        switch (gameDiff) {
            case EASY:
                windowSize = new Dimension(135, 180);
                break;
            case MEDIUM:
                windowSize = new Dimension(300, 350);
                break;
            case HARD:
                windowSize = new Dimension(450, 350);
                break;
        }
        init();
        frame.setVisible(true);
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        frame.setMinimumSize(new Dimension(this.windowSize));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Minesweeper");
        frame.setBackground(background);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(s.getIcon());

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
        easyOption.addActionListener(e -> resetNewDiff(Minesweeper.Difficulty.EASY));
        JMenuItem medOption = new JMenuItem("Intermediate");
        medOption.addActionListener(e -> resetNewDiff(Minesweeper.Difficulty.MEDIUM));
        JMenuItem hardOption = new JMenuItem("Expert");
        hardOption.addActionListener(e -> resetNewDiff(Minesweeper.Difficulty.HARD));

        JSeparator separatorTwo = new JSeparator();
        separatorTwo.setPreferredSize(new Dimension(0, 1));

        JCheckBoxMenuItem marksOn = new JCheckBoxMenuItem("Marks (?)");
        marksOn.addActionListener(e -> game.toggleMarkOption());
        marksOn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));

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
            } catch (Exception e) {
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
        faceButton.setPreferredSize(new Dimension(s.getFaceDimensions()[0],
                                    s.getFaceDimensions()[1]));
        faceButton.setFocusable(false);
        faceButton.setBorder(null);
        faceButton.setIcon(new ImageIcon(s.getFaceSprite(0)));
        faceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                faceButton.setIcon(new ImageIcon(s.getFaceSprite(1)));
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                faceButton.setIcon(new ImageIcon(s.getFaceSprite(0)));
            }
        });

        /* Panels */
        flagger.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        flagger.setBorder(new EmptyBorder(8, 18, 0, 0));
        flagger.setBackground(background);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.PAGE_START;
        buttonPanel.setBackground(background);
        buttonPanel.add(faceButton, gbc);
        buttonPanel.setBorder(null);

        topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        topPanel.setLayout(new GridLayout(1, 3));
        topPanel.add(flagger);
        topPanel.add(buttonPanel);
        topPanel.add(clock);
        topPanel.setBackground(background);

        grid = new Grid();
        grid.setBorder(new EmptyBorder(10, 15, 15, 15));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(grid);
        frame.pack();
    }

    /**
     * Note that this method throws away user's mark option;
     *  it will have to be turned back on manually.
     * @param gameDiff new difficulty of game
     */
    private void resetNewDiff(Minesweeper.Difficulty gameDiff) {
        if (gameDiff == this.gameDiff) {
            return;
        }
        frame.dispose();
        new MinesUI(gameDiff);
    }

    private void reset() {
        boolean marks = game.isMarkOption();
        game = new Minesweeper(gameDiff);

        game.setMarkOption(marks);
        flagger.updateFlags();

        topPanel.remove(clock);
        clock = new DigClock();
        topPanel.add(clock);
        topPanel.revalidate();
        topPanel.repaint();

        faceButton.setIcon(new ImageIcon(s.getFaceSprite(0)));

        frame.revalidate();
        frame.repaint();
        grid.repaint();
        grid.updateImgs();
    }

    private void checkDead() {
        if (game.isDead()) {
            game.revealOnDead();
            clock.endTimer();
            faceButton.setIcon(new ImageIcon(s.getFaceSprite(4)));
            grid.repaint();
        } else if (game.checkWin()) {
            faceButton.setIcon(new ImageIcon(s.getFaceSprite(3)));
            clock.endTimer();
            grid.updateImgs();
        }
    }

    class DigClock extends JComponent {
        private final int digits = 3;
        private int secs = 0;
        private boolean started = false;
        private JLabel[] timeLabels = new JLabel[digits];
        private Timer t;

        DigClock() {
            super();
            this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            this.setBorder(new EmptyBorder(8, 0, 0, 18));
            this.setBackground(background);
            // Initialize to all 0 sprites
            for (int i = 0; i < digits; i++) {
                timeLabels[i] = new JLabel(new ImageIcon(s.getNumberSprite(0)));
                this.add(timeLabels[i]);
            }
            t = new Timer(1000, e -> {
                secs++;
                if (secs >= 999) {
                    secs = 0;
                }
                String model = String.format("%03d", secs);
                for (int i = 0; i < digits; i++) {
                    this.remove(timeLabels[i]);
                    int digit = Character.getNumericValue(model.charAt(i));
                    timeLabels[i].setIcon(new ImageIcon(s.getNumberSprite(digit)));
                    this.add(timeLabels[i]);
                }
                this.repaint();
            });
        }

        void startTimer() {
            started = true;
            t.start();
        }

        void endTimer() {
            started = false;
            t.stop();
        }

        boolean isStarted() {
            return started;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(s.getNumDimensions()[0],
                    s.getNumDimensions()[1] * digits);
        }
    }

    class FlagTicker extends JComponent {
        private final int digits = 3;
        private JLabel[] digitLabels = new JLabel[digits];

        FlagTicker() {
            super();
            // Initialize all of the labels to 0
            for (int i = 0; i < digits; i++) {
                digitLabels[i] = new JLabel(new ImageIcon(s.getNumberSprite(0)));
                this.add(digitLabels[i]);
                this.revalidate();
            }
            updateFlags();
        }

        void updateFlags() {
            String model = String.format("%03d", game.getFlagsLeft());
            for (int i = 0; i < digits; i++) {
                this.remove(digitLabels[i]);
                this.revalidate();
                digitLabels[i].setIcon(new ImageIcon(s.getNumberSprite(
                        Character.getNumericValue(model.charAt(i)))));
                this.add(digitLabels[i]);
                this.revalidate();
            }
            this.repaint();
        }
    }

    /**
     * The grid JComponent
     */
    public class Grid extends JPanel {
        private Square[][] cells; // the array of squares
        private int rows;
        private int cols;

        Grid() {
            rows = game.getDim()[0];
            cols = game.getDim()[1];
            this.setBackground(background);
            createBoard();
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

        void updateImgs() {
            for (int[] coords : game.getChangedList()) {
                cells[coords[0]][coords[1]].repaint();
            }
            game.clearChangedList();
        }
    }

    public class Square extends JComponent {
        private final int row;
        private final int col;
        private boolean selected = false;

        /* This boolean is used to make sure mouse is still down,
        so it knows to release the selected sprite. */
        private boolean mouseDown = false;

        private Square(int row, int col) {
            this.row = row;
            this.col = col;
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!game.isDead() && !game.getWon()) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            game.flag(row, col);
                            flagger.updateFlags();
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            mouseDown = true;
                            if (game.getBoard()[row][col].getState() != Cell.State.REVEALED) {
                                selected = true;
                            }
                            // last click bool for red bg bombs
                            game.getPrevLastClicked().setLastClicked(false);
                            game.getBoard()[row][col].setLastClicked(true);
                            game.setLastClickedCell(game.getBoard()[row][col]);

                            faceButton.setIcon(new ImageIcon(s.getFaceSprite(2)));
                        } else if (SwingUtilities.isMiddleMouseButton(e)) {
                            faceButton.setIcon(new ImageIcon(s.getFaceSprite(2)));
                        }
                    }
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mouseDown = false;
                    if (!game.isDead() && !game.getWon()) {
                        faceButton.setIcon(new ImageIcon(s.getFaceSprite(0)));
                        if (selected) {
                            if (!clock.isStarted()) {
                                clock.startTimer();
                            }
                            game.move(row, col);
                            grid.updateImgs();
                            repaint();
                            selected = false;
                        }
                        checkDead();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (mouseDown) {
                        selected = false;
                        repaint();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (mouseDown) {
                        selected = true;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            Cell cell = game.getBoard()[row][col];
            BufferedImage tileImg = cell.getImg();
            if (game.isDead()) {
                if (cell.getState() == Cell.State.FLAGGED) {
                    if (cell.isMine()) {
                        tileImg = s.getTileSprite(2);
                    } else {
                        tileImg = s.getTileSprite(7);
                    }
                }
            }
            // "Pushed in" sprites
            if (selected) {
                if (cell.getState() == Cell.State.MARKED) {
                    tileImg = s.getTileSprite(4);
                } else if (cell.getState() != Cell.State.FLAGGED) {
                    tileImg = s.getTileSprite(1);
                }
            }
            g2.drawImage(tileImg, 0, 0, s.getTileDimensions()[0],
                    s.getTileDimensions()[1], this);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(s.getTileDimensions()[0],
                    s.getTileDimensions()[1]);
        }
    }

    public static void main(String[] args) {
        // Default difficulty is easy
        SwingUtilities.invokeLater(() -> new MinesUI(Minesweeper.Difficulty.EASY));
    }
}