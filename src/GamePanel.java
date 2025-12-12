import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;

/**
 * GamePanel - Main game board with Paper & Pencil theme
 * Papan permainan seperti grid di buku tulis
 */
public class GamePanel extends GameFrame.GradientPanel {

    private GameFrame frame;
    private GameBoard board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private boolean gameOver;
    private boolean classicMode;
    private String statusMessage;
    private int[][] winningCells;

    // Power-up mode
    private PowerUp activePowerUp;
    private boolean powerUpMode = false;

    // Animation
    private Timer animationTimer;
    private float pulseValue = 0;
    private java.util.List<CellAnimation> cellAnimations;

    // UI Components
    private JPanel boardPanel;
    private JButton[][] cells;
    private JLabel statusLabel;
    private JLabel p1ScoreLabel;
    private JLabel p2ScoreLabel;
    private JPanel powerUpPanel;
    private JButton bombBtn, shieldBtn, swapBtn;

    public GamePanel(GameFrame frame) {
        this.frame = frame;
        this.cellAnimations = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        animationTimer = new Timer(50, e -> {
            pulseValue += 0.1f;
            if (pulseValue > Math.PI * 2)
                pulseValue = 0;
            updateAnimations();
            repaint();
        });
    }

    public void initGame(int gridSize, boolean vsAI, AIPlayer.Difficulty difficulty, boolean classicMode) {
        removeAll();
        this.classicMode = classicMode;

        board = new GameBoard(gridSize);
        player1 = new HumanPlayer("Player 1", 'X');

        // Only give power-ups in Upnormal mode
        if (!classicMode) {
            player1.resetPowerUps();
        }

        if (vsAI) {
            player2 = new AIPlayer("Computer", 'O', 'X', difficulty);
        } else {
            player2 = new HumanPlayer("Player 2", 'O');
        }

        if (!classicMode) {
            player2.resetPowerUps();
        }

        currentPlayer = player1;
        gameOver = false;
        winningCells = null;
        activePowerUp = null;
        powerUpMode = false;
        statusMessage = currentPlayer.getName() + "'s turn (" + currentPlayer.getSymbol() + ")";

        add(createTopPanel(), BorderLayout.NORTH);
        add(createBoardPanel(gridSize), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        if (!classicMode) {
            updatePowerUpButtons();
        }

        animationTimer.start();
        revalidate();
        repaint();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        GameFrame.NeonButton backBtn = new GameFrame.NeonButton("< Back", GameFrame.PENCIL_LIGHT);
        backBtn.setPreferredSize(new Dimension(85, 30));
        backBtn.setFont(GameFrame.getSketchFont(Font.BOLD, 12));
        backBtn.addActionListener(e -> {
            animationTimer.stop();
            frame.showMenu();
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backBtn);

        // Title shows mode
        String modeText = classicMode ? "Classic Mode" : "Upnormal Mode";
        JLabel title = new JLabel("Tic Tac Toe - " + modeText, SwingConstants.CENTER);
        title.setFont(GameFrame.getSketchFont(Font.BOLD, 20));
        title.setForeground(GameFrame.PENCIL_DARK);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(title, BorderLayout.CENTER);
        panel.add(Box.createHorizontalStrut(85), BorderLayout.EAST);

        return panel;
    }

    private JPanel createBoardPanel(int gridSize) {
        JPanel container = new JPanel(new BorderLayout(15, 0));
        container.setOpaque(false);

        container.add(createPlayerPanel(player1, true), BorderLayout.WEST);

        boardPanel = new JPanel(new GridLayout(gridSize, gridSize, 4, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paper background with shadow
                g2d.setColor(GameFrame.PAPER_SHADOW);
                g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 15, 15);
                g2d.setColor(GameFrame.PAPER_BG);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 15, 15);

                g2d.setColor(GameFrame.PENCIL_LIGHT);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth() - 6, getHeight() - 6, 15, 15);
            }
        };
        boardPanel.setOpaque(false);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        cells = new JButton[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                cells[i][j] = createCell(i, j);
                boardPanel.add(cells[i][j]);
            }
        }

        container.add(boardPanel, BorderLayout.CENTER);
        container.add(createPlayerPanel(player2, false), BorderLayout.EAST);

        return container;
    }

    private JButton createCell(final int row, final int col) {
        JButton cell = new JButton() {
            private float hoverAlpha = 0;
            private boolean hovering = false;
            private Timer hoverTimer;

            {
                hoverTimer = new Timer(30, e -> {
                    if (hovering && hoverAlpha < 0.3f) {
                        hoverAlpha += 0.05f;
                        repaint();
                    } else if (!hovering && hoverAlpha > 0) {
                        hoverAlpha -= 0.05f;
                        repaint();
                    }
                });
                hoverTimer.start();

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovering = false;
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                char cellValue = board.getCell(row, col);
                boolean isWinning = isWinningCell(row, col);
                boolean isShielded = !classicMode && board.isShielded(row, col);

                // Background
                if (isWinning) {
                    g2d.setColor(new Color(200, 255, 200));
                } else if (isShielded) {
                    g2d.setColor(new Color(220, 235, 250));
                } else {
                    g2d.setColor(GameFrame.PAPER_BG);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Hover
                if (hoverAlpha > 0 && !gameOver && cellValue == ' ') {
                    g2d.setColor(new Color(255, 245, 180, (int) (hoverAlpha * 255)));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                // Power-up mode indicator
                if (!classicMode && powerUpMode && activePowerUp != null) {
                    g2d.setColor(new Color(255, 200, 150, 40));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                // Border
                if (isWinning) {
                    g2d.setColor(new Color(100, 180, 100));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else if (isShielded) {
                    g2d.setColor(GameFrame.PENCIL_BLUE);
                    g2d.setStroke(new BasicStroke(2f));
                } else {
                    g2d.setColor(GameFrame.PAPER_LINES);
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);

                if (cellValue != ' ') {
                    drawSymbol(g2d, cellValue, isWinning);
                }

                // Shield text
                if (isShielded) {
                    g2d.setFont(GameFrame.getSketchFont(Font.BOLD, 9));
                    g2d.setColor(GameFrame.PENCIL_BLUE);
                    g2d.drawString("protected", 4, 12);
                }
            }

            private void drawSymbol(Graphics2D g2d, char symbol, boolean isWinning) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int size = Math.min(getWidth(), getHeight()) - 30;

                if (symbol == 'X') {
                    g2d.setColor(isWinning ? new Color(50, 100, 150) : GameFrame.PENCIL_BLUE);
                    g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    int offset = size / 2 - 5;
                    g2d.drawLine(centerX - offset, centerY - offset + 2, centerX + offset, centerY + offset - 1);
                    g2d.drawLine(centerX + offset - 1, centerY - offset, centerX - offset + 1, centerY + offset);

                } else if (symbol == 'O') {
                    g2d.setColor(isWinning ? new Color(180, 80, 80) : GameFrame.PENCIL_RED);
                    g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    int radius = size / 2 - 8;
                    g2d.drawOval(centerX - radius + 1, centerY - radius, radius * 2 - 2, radius * 2);
                }
            }
        };

        cell.setPreferredSize(new Dimension(80, 80));
        cell.setBorderPainted(false);
        cell.setFocusPainted(false);
        cell.setContentAreaFilled(false);
        cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cell.addActionListener(e -> handleCellClick(row, col));

        return cell;
    }

    private JPanel createPlayerPanel(Player player, boolean isLeft) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(GameFrame.PAPER_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                Color borderColor;
                if (currentPlayer == player && !gameOver) {
                    borderColor = (player.getSymbol() == 'X') ? GameFrame.PENCIL_BLUE : GameFrame.PENCIL_RED;
                    g2d.setColor(new Color(255, 250, 200));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                } else {
                    borderColor = GameFrame.PENCIL_LIGHT;
                }

                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(100, 180));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));

        JLabel symbolLabel = new JLabel(String.valueOf(player.getSymbol()));
        symbolLabel.setFont(GameFrame.getSketchFont(Font.BOLD, 32));
        symbolLabel.setForeground(player.getSymbol() == 'X' ? GameFrame.PENCIL_BLUE : GameFrame.PENCIL_RED);
        symbolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setFont(GameFrame.getSketchFont(Font.BOLD, 11));
        nameLabel.setForeground(GameFrame.PENCIL_DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("Score: " + player.getScore());
        scoreLabel.setFont(GameFrame.getSketchFont(Font.PLAIN, 11));
        scoreLabel.setForeground(GameFrame.PENCIL_DARK);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isLeft) {
            p1ScoreLabel = scoreLabel;
        } else {
            p2ScoreLabel = scoreLabel;
        }

        panel.add(symbolLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(scoreLabel);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        statusLabel = new JLabel(statusMessage, SwingConstants.CENTER);
        statusLabel.setFont(GameFrame.getSketchFont(Font.BOLD, 15));
        statusLabel.setForeground(GameFrame.PENCIL_DARK);

        // Power-up panel - only visible in Upnormal mode
        powerUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        powerUpPanel.setOpaque(false);

        if (!classicMode) {
            JLabel powerUpTitle = new JLabel("Power-Ups: ");
            powerUpTitle.setForeground(GameFrame.PENCIL_LIGHT);
            powerUpTitle.setFont(GameFrame.getSketchFont(Font.PLAIN, 11));
            powerUpPanel.add(powerUpTitle);

            bombBtn = createPowerUpButton("Bomb", GameFrame.PENCIL_RED, BombPowerUp.class);
            shieldBtn = createPowerUpButton("Shield", GameFrame.PENCIL_BLUE, ShieldPowerUp.class);
            swapBtn = createPowerUpButton("Swap", GameFrame.PENCIL_DARK, SwapPowerUp.class);

            powerUpPanel.add(bombBtn);
            powerUpPanel.add(shieldBtn);
            powerUpPanel.add(swapBtn);
        }

        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        controlPanel.setOpaque(false);

        GameFrame.NeonButton newGameBtn = new GameFrame.NeonButton("New Game", GameFrame.PENCIL_BLUE);
        newGameBtn.setPreferredSize(new Dimension(120, 35));
        newGameBtn.setFont(GameFrame.getSketchFont(Font.BOLD, 13));
        newGameBtn.addActionListener(
                e -> initGame(frame.getGridSize(), frame.isVsAI(), frame.getAIDifficulty(), classicMode));

        controlPanel.add(newGameBtn);

        JPanel bottomContent = new JPanel();
        bottomContent.setLayout(new BoxLayout(bottomContent, BoxLayout.Y_AXIS));
        bottomContent.setOpaque(false);

        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        powerUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomContent.add(statusLabel);
        bottomContent.add(Box.createVerticalStrut(10));
        if (!classicMode) {
            bottomContent.add(powerUpPanel);
            bottomContent.add(Box.createVerticalStrut(10));
        }
        bottomContent.add(controlPanel);

        panel.add(bottomContent, BorderLayout.CENTER);

        return panel;
    }

    private JButton createPowerUpButton(String text, Color color, Class<? extends PowerUp> type) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean available = currentPlayer != null && currentPlayer.hasPowerUp(type);

                if (available) {
                    g2d.setColor(GameFrame.PAPER_BG);
                } else {
                    g2d.setColor(GameFrame.PAPER_SHADOW);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                g2d.setColor(available ? color : GameFrame.PENCIL_LIGHT);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);

                g2d.setFont(GameFrame.getSketchFont(Font.BOLD, 10));
                g2d.setColor(available ? color : GameFrame.PENCIL_LIGHT);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        btn.setPreferredSize(new Dimension(60, 30));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> activatePowerUp(type));

        return btn;
    }

    private void handleCellClick(int row, int col) {
        if (gameOver)
            return;
        if (currentPlayer instanceof AIPlayer)
            return;

        if (!classicMode && powerUpMode && activePowerUp != null) {
            usePowerUpOnCell(row, col);
            return;
        }

        if (board.isCellEmpty(row, col)) {
            makeMove(row, col);
        }
    }

    private void makeMove(int row, int col) {
        board.setCell(row, col, currentPlayer.getSymbol());
        addCellAnimation(row, col);

        char winner = board.checkWinner();
        if (winner != ' ') {
            handleWin(winner);
        } else if (board.isFull()) {
            handleDraw();
        } else {
            switchPlayer();
        }

        repaint();
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        statusMessage = currentPlayer.getName() + "'s turn (" + currentPlayer.getSymbol() + ")";
        statusLabel.setText(statusMessage);
        statusLabel.setForeground(currentPlayer.getSymbol() == 'X' ? GameFrame.PENCIL_BLUE : GameFrame.PENCIL_RED);

        if (!classicMode) {
            updatePowerUpButtons();
        }

        if (currentPlayer instanceof AIPlayer && !gameOver) {
            Timer aiTimer = new Timer(500, e -> {
                int[] move = currentPlayer.makeMove(board);
                if (move != null) {
                    makeMove(move[0], move[1]);
                }
                ((Timer) e.getSource()).stop();
            });
            aiTimer.setRepeats(false);
            aiTimer.start();
        }
    }

    private void handleWin(char winner) {
        gameOver = true;
        winningCells = board.getWinningCells();

        Player winningPlayer = (winner == player1.getSymbol()) ? player1 : player2;
        Player losingPlayer = (winner == player1.getSymbol()) ? player2 : player1;

        int basePoints = 10;
        winningPlayer.incrementStreak();
        int streakBonus = winningPlayer.getStreak() * 2;
        int totalPoints = basePoints + streakBonus;

        winningPlayer.addScore(totalPoints);
        losingPlayer.resetStreak();

        statusMessage = winningPlayer.getName() + " WINS! (+" + totalPoints + " pts)";
        statusLabel.setText(statusMessage);
        statusLabel.setForeground(new Color(80, 150, 80));

        updateScoreLabels();
    }

    private void handleDraw() {
        gameOver = true;
        statusMessage = "It's a DRAW!";
        statusLabel.setText(statusMessage);
        statusLabel.setForeground(GameFrame.PENCIL_DARK);

        player1.resetStreak();
        player2.resetStreak();
    }

    private void activatePowerUp(Class<? extends PowerUp> type) {
        if (classicMode || gameOver || currentPlayer instanceof AIPlayer)
            return;
        if (!currentPlayer.hasPowerUp(type))
            return;

        powerUpMode = true;

        if (type == BombPowerUp.class) {
            activePowerUp = new BombPowerUp();
            statusMessage = "Select a cell to BOMB (clears 3x3 area)";
        } else if (type == ShieldPowerUp.class) {
            activePowerUp = new ShieldPowerUp();
            statusMessage = "Select YOUR cell to SHIELD";
        } else if (type == SwapPowerUp.class) {
            activePowerUp = new SwapPowerUp();
            statusMessage = "Select OPPONENT's cell to SWAP";
        }

        statusLabel.setText(statusMessage);
        statusLabel.setForeground(GameFrame.PENCIL_RED);
        repaint();
    }

    private void usePowerUpOnCell(int row, int col) {
        if (activePowerUp == null)
            return;

        char playerSymbol = currentPlayer.getSymbol();
        char opponentSymbol = (playerSymbol == 'X') ? 'O' : 'X';

        boolean success = false;

        if (activePowerUp instanceof BombPowerUp) {
            activePowerUp.use(board, row, col, playerSymbol, opponentSymbol);
            currentPlayer.usePowerUp(BombPowerUp.class);
            success = true;
        } else if (activePowerUp instanceof ShieldPowerUp) {
            if (board.getCell(row, col) == playerSymbol) {
                activePowerUp.use(board, row, col, playerSymbol, opponentSymbol);
                currentPlayer.usePowerUp(ShieldPowerUp.class);
                success = true;
            }
        } else if (activePowerUp instanceof SwapPowerUp) {
            if (board.getCell(row, col) == opponentSymbol && !board.isShielded(row, col)) {
                activePowerUp.use(board, row, col, playerSymbol, opponentSymbol);
                currentPlayer.usePowerUp(SwapPowerUp.class);
                success = true;
            }
        }

        if (success) {
            powerUpMode = false;
            activePowerUp = null;

            char winner = board.checkWinner();
            if (winner != ' ') {
                handleWin(winner);
            } else if (board.isFull()) {
                handleDraw();
            } else {
                switchPlayer();
            }
        }

        updatePowerUpButtons();
        repaint();
    }

    private void cancelPowerUp() {
        powerUpMode = false;
        activePowerUp = null;
        statusMessage = currentPlayer.getName() + "'s turn (" + currentPlayer.getSymbol() + ")";
        statusLabel.setText(statusMessage);
        statusLabel.setForeground(GameFrame.PENCIL_DARK);
        repaint();
    }

    private void updatePowerUpButtons() {
        if (bombBtn != null)
            bombBtn.repaint();
        if (shieldBtn != null)
            shieldBtn.repaint();
        if (swapBtn != null)
            swapBtn.repaint();
    }

    private void updateScoreLabels() {
        if (p1ScoreLabel != null)
            p1ScoreLabel.setText("Score: " + player1.getScore());
        if (p2ScoreLabel != null)
            p2ScoreLabel.setText("Score: " + player2.getScore());
    }

    private boolean isWinningCell(int row, int col) {
        if (winningCells == null)
            return false;
        for (int[] cell : winningCells) {
            if (cell[0] == row && cell[1] == col)
                return true;
        }
        return false;
    }

    private void addCellAnimation(int row, int col) {
        cellAnimations.add(new CellAnimation(row, col));
    }

    private void updateAnimations() {
        cellAnimations.removeIf(anim -> anim.isComplete());
        for (CellAnimation anim : cellAnimations) {
            anim.update();
        }
    }

    private class CellAnimation {
        int row, col;
        float scale = 0.5f;
        boolean complete = false;

        CellAnimation(int row, int col) {
            this.row = row;
            this.col = col;
        }

        void update() {
            scale += 0.1f;
            if (scale >= 1.0f) {
                scale = 1.0f;
                complete = true;
            }
        }

        boolean isComplete() {
            return complete;
        }
    }
}
