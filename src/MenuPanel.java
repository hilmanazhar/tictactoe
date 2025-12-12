import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * MenuPanel - Main menu with Paper & Pencil theme
 * Seperti halaman pertama buku tulis
 */
public class MenuPanel extends GameFrame.GradientPanel {

    private GameFrame frame;
    private JComboBox<String> gridSizeCombo;
    private JComboBox<String> difficultyCombo;
    private JToggleButton vsAIButton;
    private JToggleButton vs2PButton;
    private JToggleButton classicBtn;
    private JToggleButton upnormalBtn;
    private JPanel powerUpInfoPanel;
    private JPanel gridSizePanel;

    public MenuPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Title Panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Options Panel
        JPanel optionsPanel = createOptionsPanel();
        add(optionsPanel, BorderLayout.CENTER);

        // Start Button
        JPanel startPanel = createStartPanel();
        add(startPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Main title - handwritten style
        JLabel titleLabel = new JLabel("Tic Tac Toe") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent();

                // Pencil shadow effect
                g2d.setColor(new Color(180, 175, 160));
                g2d.drawString(getText(), x + 2, y + 2);

                // Main text - dark pencil
                g2d.setColor(GameFrame.PENCIL_DARK);
                g2d.drawString(getText(), x, y);

                // Underline
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int lineY = y + 8;
                g2d.drawLine(x, lineY, x + fm.stringWidth(getText()), lineY);
            }
        };
        titleLabel.setFont(GameFrame.getSketchFont(Font.BOLD, 42));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setPreferredSize(new Dimension(400, 70));

        // Subtitle
        JLabel subTitle = new JLabel("Paper & Pencil Edition");
        subTitle.setFont(GameFrame.getSketchFont(Font.ITALIC, 16));
        subTitle.setForeground(GameFrame.PENCIL_LIGHT);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Decorative line (simple dashes, no emoji)
        JLabel doodle = new JLabel("- - - - - - - - - - - - - - - -");
        doodle.setFont(GameFrame.getSketchFont(Font.PLAIN, 12));
        doodle.setForeground(GameFrame.PENCIL_LIGHT);
        doodle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(doodle);

        return panel;
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Game Type Selection (Classic vs Upnormal)
        JLabel typeLabel = createLabel("Game Type");
        panel.add(typeLabel);
        panel.add(Box.createVerticalStrut(8));

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        typePanel.setOpaque(false);

        classicBtn = createToggleButton("Classic", true);
        upnormalBtn = createToggleButton("Upnormal", false);

        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(classicBtn);
        typeGroup.add(upnormalBtn);

        classicBtn.addActionListener(e -> {
            frame.setClassicMode(true);
            frame.setGridSize(3); // Classic is always 3x3
            gridSizePanel.setVisible(false);
            powerUpInfoPanel.setVisible(false);
            revalidate();
            repaint();
        });
        upnormalBtn.addActionListener(e -> {
            frame.setClassicMode(false);
            gridSizeCombo.setSelectedIndex(0); // Default to 5x5
            frame.setGridSize(5);
            gridSizePanel.setVisible(true);
            powerUpInfoPanel.setVisible(true);
            revalidate();
            repaint();
        });

        typePanel.add(classicBtn);
        typePanel.add(upnormalBtn);
        panel.add(typePanel);
        panel.add(Box.createVerticalStrut(15));

        // Game Mode Selection (vs AI / 2P)
        JLabel modeLabel = createLabel("Player Mode");
        panel.add(modeLabel);
        panel.add(Box.createVerticalStrut(8));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        modePanel.setOpaque(false);

        vsAIButton = createToggleButton("vs Computer", true);
        vs2PButton = createToggleButton("2 Players", false);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(vsAIButton);
        modeGroup.add(vs2PButton);

        vsAIButton.addActionListener(e -> {
            frame.setVsAI(true);
            difficultyCombo.setEnabled(true);
        });
        vs2PButton.addActionListener(e -> {
            frame.setVsAI(false);
            difficultyCombo.setEnabled(false);
        });

        modePanel.add(vsAIButton);
        modePanel.add(vs2PButton);
        panel.add(modePanel);
        panel.add(Box.createVerticalStrut(15));

        // Grid Size Selection (only for Upnormal mode)
        gridSizePanel = new JPanel();
        gridSizePanel.setOpaque(false);
        gridSizePanel.setLayout(new BoxLayout(gridSizePanel, BoxLayout.Y_AXIS));

        JLabel gridLabel = createLabel("Grid Size");
        gridLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gridSizePanel.add(gridLabel);
        gridSizePanel.add(Box.createVerticalStrut(8));

        // Upnormal mode: 5x5 and 7x7 only
        String[] gridOptions = { "5 x 5", "7 x 7" };
        gridSizeCombo = createStyledComboBox(gridOptions);
        gridSizeCombo.addActionListener(e -> {
            int index = gridSizeCombo.getSelectedIndex();
            // 0 = 5x5, 1 = 7x7
            frame.setGridSize(index == 0 ? 5 : 7);
        });

        JPanel gridComboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gridComboPanel.setOpaque(false);
        gridComboPanel.add(gridSizeCombo);
        gridSizePanel.add(gridComboPanel);

        gridSizePanel.setVisible(false); // Hidden by default (Classic mode)
        panel.add(gridSizePanel);
        panel.add(Box.createVerticalStrut(15));

        // AI Difficulty
        JLabel diffLabel = createLabel("AI Difficulty");
        panel.add(diffLabel);
        panel.add(Box.createVerticalStrut(8));

        String[] diffOptions = { "Easy", "Medium", "Hard" };
        difficultyCombo = createStyledComboBox(diffOptions);
        difficultyCombo.setSelectedIndex(1);
        difficultyCombo.addActionListener(e -> {
            int index = difficultyCombo.getSelectedIndex();
            AIPlayer.Difficulty diff = AIPlayer.Difficulty.values()[index];
            frame.setAIDifficulty(diff);
        });

        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        diffPanel.setOpaque(false);
        diffPanel.add(difficultyCombo);
        panel.add(diffPanel);
        panel.add(Box.createVerticalStrut(15));

        // Power-up features info (only for Upnormal mode)
        powerUpInfoPanel = createFeaturesPanel();
        powerUpInfoPanel.setVisible(false); // Hidden by default (Classic mode)
        panel.add(powerUpInfoPanel);

        return panel;
    }

    private JPanel createFeaturesPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(createFeatureCard("Bomb", "Clear area", GameFrame.PENCIL_RED));
        panel.add(createFeatureCard("Shield", "Protect", GameFrame.PENCIL_BLUE));
        panel.add(createFeatureCard("Swap", "Convert", GameFrame.PENCIL_DARK));

        return panel;
    }

    private JPanel createFeatureCard(String title, String desc, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paper background
                g2d.setColor(GameFrame.PAPER_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border
                g2d.setColor(GameFrame.PENCIL_LIGHT);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        card.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(GameFrame.getSketchFont(Font.BOLD, 12));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(GameFrame.getSketchFont(Font.PLAIN, 10));
        descLabel.setForeground(GameFrame.PENCIL_LIGHT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(descLabel);

        return card;
    }

    private JPanel createStartPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        GameFrame.NeonButton startButton = new GameFrame.NeonButton("Start Game!", GameFrame.PENCIL_BLUE);
        startButton.setPreferredSize(new Dimension(250, 50));
        startButton.setFont(GameFrame.getSketchFont(Font.BOLD, 20));
        startButton.addActionListener(e -> frame.startGame());

        panel.add(startButton);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel("-- " + text + " --");
        label.setFont(GameFrame.getSketchFont(Font.BOLD, 13));
        label.setForeground(GameFrame.PENCIL_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JToggleButton createToggleButton(String text, boolean selected) {
        JToggleButton button = new JToggleButton(text, selected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected()) {
                    g2d.setColor(GameFrame.HIGHLIGHT);
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                } else {
                    g2d.setColor(GameFrame.PAPER_BG);
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                }

                g2d.setColor(isSelected() ? GameFrame.PENCIL_DARK : GameFrame.PENCIL_LIGHT);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 8, 8);

                g2d.setColor(isSelected() ? GameFrame.PENCIL_DARK : GameFrame.PENCIL_LIGHT);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        button.setFont(GameFrame.getSketchFont(Font.BOLD, 13));
        button.setPreferredSize(new Dimension(130, 38));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(GameFrame.getSketchFont(Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(200, 35));
        combo.setBackground(GameFrame.PAPER_BG);
        combo.setForeground(GameFrame.PENCIL_DARK);
        combo.setBorder(BorderFactory.createLineBorder(GameFrame.PENCIL_LIGHT, 1));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? GameFrame.HIGHLIGHT : GameFrame.PAPER_BG);
                setForeground(GameFrame.PENCIL_DARK);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                setFont(GameFrame.getSketchFont(Font.PLAIN, 13));
                return this;
            }
        });

        return combo;
    }
}
