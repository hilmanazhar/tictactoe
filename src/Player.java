import java.util.ArrayList;
import java.util.List;

/**
 * Player - Abstract class representing a player
 */
public abstract class Player {
    protected String name;
    protected char symbol;
    protected int score;
    protected int streak;
    protected List<PowerUp> powerUps;
    
    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
        this.score = 0;
        this.streak = 0;
        this.powerUps = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public char getSymbol() {
        return symbol;
    }
    
    public int getScore() {
        return score;
    }
    
    public void addScore(int points) {
        this.score += points;
    }
    
    public int getStreak() {
        return streak;
    }
    
    public void incrementStreak() {
        this.streak++;
    }
    
    public void resetStreak() {
        this.streak = 0;
    }
    
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
    
    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }
    
    public boolean hasPowerUp(Class<? extends PowerUp> type) {
        for (PowerUp p : powerUps) {
            if (type.isInstance(p)) {
                return true;
            }
        }
        return false;
    }
    
    public PowerUp usePowerUp(Class<? extends PowerUp> type) {
        for (int i = 0; i < powerUps.size(); i++) {
            if (type.isInstance(powerUps.get(i))) {
                return powerUps.remove(i);
            }
        }
        return null;
    }
    
    public void resetPowerUps() {
        powerUps.clear();
        powerUps.add(new BombPowerUp());
        powerUps.add(new ShieldPowerUp());
        powerUps.add(new SwapPowerUp());
    }
    
    public abstract int[] makeMove(GameBoard board);
}
