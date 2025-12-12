import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;

/**
 * GameFrame - Main game window with Paper & Pencil theme
 * Tema minimalis seperti coret-coretan di buku tulis
 */
public class GameFrame extends JFrame {

    // Paper & Pencil Colors
    public static final Color PAPER_BG = new Color(250, 247, 235); // Cream paper
    public static final Color PAPER_LINES = new Color(200, 195, 180); // Light pencil lines
    public static final Color PENCIL_DARK = new Color(60, 60, 60); // Dark pencil
    public static final Color PENCIL_LIGHT = new Color(150, 145, 135); // Light pencil
    public static final Color PENCIL_BLUE = new Color(70, 130, 180); // Blue pencil (X)
    public static final Color PENCIL_RED = new Color(205, 92, 92); // Red pencil (O)
    public static final Color PAPER_SHADOW = new Color(230, 225, 210); // Paper shadow
    public static final Color HIGHLIGHT = new Color(255, 235, 150); // Yellow highlighter
    public static final Color ERASER_PINK = new Color(255, 182, 193); // Eraser pink

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;

    // Game settings
    private int gridSize = 3;
    private boolean vsAI = true;
    private boolean classicMode = true; // Classic = no power-ups, 3x3 only
    private AIPlayer.Difficulty aiDifficulty = AIPlayer.Difficulty.MEDIUM;

    // Handwritten style font
    public static Font getSketchFont(int style, int size) {
        String[] handwrittenFonts = { "Segoe Print", "Comic Sans MS", "Bradley Hand ITC", "Ink Free" };
        for (String fontName : handwrittenFonts) {
            Font font = new Font(fontName, style, size);
            if (!font.getFamily().equals(Font.DIALOG)) {
                return font;
            }
        }
        return new Font("Comic Sans MS", style, size);
    }

    public GameFrame() {
        setTitle("Tic Tac Toe - Paper & Pencil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(PAPER_BG);

        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(menuPanel, "menu");
        mainPanel.add(gamePanel, "game");

        add(mainPanel);

        setSize(700, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        showMenu();
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "menu");
    }

    public void startGame() {
        // Classic mode forces 3x3
        if (classicMode) {
            gridSize = 3;
        }
        gamePanel.initGame(gridSize, vsAI, aiDifficulty, classicMode);
        cardLayout.show(mainPanel, "game");
    }

    public void setGridSize(int size) {
        this.gridSize = size;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setVsAI(boolean vsAI) {
        this.vsAI = vsAI;
    }

    public boolean isVsAI() {
        return vsAI;
    }

    public void setClassicMode(boolean classic) {
        this.classicMode = classic;
    }

    public boolean isClassicMode() {
        return classicMode;
    }

    public void setAIDifficulty(AIPlayer.Difficulty difficulty) {
        this.aiDifficulty = difficulty;
    }

    public AIPlayer.Difficulty getAIDifficulty() {
        return aiDifficulty;
    }

    // Custom button with sketchy paper style
    public static class NeonButton extends JButton {
        private Color accentColor;
        private boolean hovering = false;

        public NeonButton(String text, Color accentColor) {
            super(text);
            this.accentColor = accentColor;

            setFont(getSketchFont(Font.BOLD, 16));
            setForeground(PENCIL_DARK);
            setBackground(PAPER_BG);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovering = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hovering) {
                g2d.setColor(HIGHLIGHT);
            } else {
                g2d.setColor(PAPER_BG);
            }
            g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);

            g2d.setColor(hovering ? accentColor : PENCIL_LIGHT);
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 8, 8);

            g2d.setFont(getFont());
            g2d.setColor(hovering ? accentColor : PENCIL_DARK);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2d.drawString(getText(), textX, textY);

            g2d.dispose();
        }
    }

    // Custom panel with paper background
    public static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Paper background
            g2d.setColor(PAPER_BG);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Notebook lines
            g2d.setColor(new Color(200, 220, 240, 60));
            g2d.setStroke(new BasicStroke(1));
            for (int y = 30; y < getHeight(); y += 28) {
                g2d.drawLine(0, y, getWidth(), y);
            }

            // Left margin
            g2d.setColor(new Color(255, 150, 150, 80));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(45, 0, 45, getHeight());
        }
    }
}
