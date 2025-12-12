/**
 * BombPowerUp - Clears a 3x3 area around the selected cell
 */
public class BombPowerUp implements PowerUp {
    
    @Override
    public String getName() {
        return "Bomb";
    }
    
    @Override
    public String getIcon() {
        return "💣";
    }
    
    @Override
    public String getDescription() {
        return "Clears a 3x3 area around the selected cell";
    }
    
    @Override
    public boolean canUse(GameBoard board, int row, int col) {
        // Can use bomb on any valid position
        return board.isValidPosition(row, col);
    }
    
    @Override
    public void use(GameBoard board, int row, int col, char playerSymbol, char opponentSymbol) {
        // Clear 3x3 area around the cell
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (board.isValidPosition(i, j) && !board.isShielded(i, j)) {
                    board.clearCell(i, j);
                }
            }
        }
    }
}
