/**
 * ShieldPowerUp - Protects a cell from being modified
 */
public class ShieldPowerUp implements PowerUp {
    
    @Override
    public String getName() {
        return "Shield";
    }
    
    @Override
    public String getIcon() {
        return "🛡️";
    }
    
    @Override
    public String getDescription() {
        return "Protects your cell from bombs and swaps";
    }
    
    @Override
    public boolean canUse(GameBoard board, int row, int col) {
        // Can only shield cells that belong to the player
        return board.isValidPosition(row, col) && 
               board.getCell(row, col) != ' ' && 
               !board.isShielded(row, col);
    }
    
    @Override
    public void use(GameBoard board, int row, int col, char playerSymbol, char opponentSymbol) {
        if (board.getCell(row, col) == playerSymbol) {
            board.setShield(row, col, true);
        }
    }
}
