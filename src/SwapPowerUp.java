/**
 * SwapPowerUp - Swaps a cell's symbol with the opponent's
 */
public class SwapPowerUp implements PowerUp {
    
    @Override
    public String getName() {
        return "Swap";
    }
    
    @Override
    public String getIcon() {
        return "⚡";
    }
    
    @Override
    public String getDescription() {
        return "Swaps opponent's cell to your symbol";
    }
    
    @Override
    public boolean canUse(GameBoard board, int row, int col) {
        // Can only swap opponent's cells that are not shielded
        return board.isValidPosition(row, col) && 
               board.getCell(row, col) != ' ' && 
               !board.isShielded(row, col);
    }
    
    @Override
    public void use(GameBoard board, int row, int col, char playerSymbol, char opponentSymbol) {
        if (board.getCell(row, col) == opponentSymbol && !board.isShielded(row, col)) {
            board.forceSetCell(row, col, playerSymbol);
        }
    }
}
