/**
 * PowerUp - Interface for all power-ups
 */
public interface PowerUp {
    String getName();
    String getIcon();
    String getDescription();
    boolean canUse(GameBoard board, int row, int col);
    void use(GameBoard board, int row, int col, char playerSymbol, char opponentSymbol);
}
