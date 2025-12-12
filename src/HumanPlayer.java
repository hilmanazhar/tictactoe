/**
 * HumanPlayer - Represents a human player
 */
public class HumanPlayer extends Player {
    
    public HumanPlayer(String name, char symbol) {
        super(name, symbol);
    }
    
    @Override
    public int[] makeMove(GameBoard board) {
        // Human moves are handled through GUI
        return null;
    }
}
