import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AIPlayer - AI opponent with different difficulty levels
 * Uses Minimax algorithm with alpha-beta pruning for Hard mode
 */
public class AIPlayer extends Player {
    
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    private Difficulty difficulty;
    private Random random;
    private char opponentSymbol;
    
    public AIPlayer(String name, char symbol, char opponentSymbol, Difficulty difficulty) {
        super(name, symbol);
        this.difficulty = difficulty;
        this.opponentSymbol = opponentSymbol;
        this.random = new Random();
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    @Override
    public int[] makeMove(GameBoard board) {
        switch (difficulty) {
            case EASY:
                return makeEasyMove(board);
            case MEDIUM:
                return makeMediumMove(board);
            case HARD:
                return makeHardMove(board);
            default:
                return makeEasyMove(board);
        }
    }
    
    private int[] makeEasyMove(GameBoard board) {
        // Random move
        List<int[]> emptyCells = getEmptyCells(board);
        if (emptyCells.isEmpty()) return null;
        return emptyCells.get(random.nextInt(emptyCells.size()));
    }
    
    private int[] makeMediumMove(GameBoard board) {
        // 50% chance to make a smart move, 50% random
        if (random.nextBoolean()) {
            return makeHardMove(board);
        }
        return makeEasyMove(board);
    }
    
    private int[] makeHardMove(GameBoard board) {
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int size = board.getSize();
        int maxDepth = size <= 3 ? 9 : (size == 4 ? 5 : 4);
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isCellEmpty(i, j)) {
                    GameBoard copy = board.copy();
                    copy.setCell(i, j, symbol);
                    int score = minimax(copy, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE, maxDepth);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        
        return bestMove != null ? bestMove : makeEasyMove(board);
    }
    
    private int minimax(GameBoard board, int depth, boolean isMaximizing, int alpha, int beta, int maxDepth) {
        char winner = board.checkWinner();
        
        if (winner == symbol) {
            return 100 - depth;
        } else if (winner == opponentSymbol) {
            return depth - 100;
        } else if (board.isFull() || depth >= maxDepth) {
            return evaluateBoard(board);
        }
        
        int size = board.getSize();
        
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board.isCellEmpty(i, j)) {
                        GameBoard copy = board.copy();
                        copy.setCell(i, j, symbol);
                        int eval = minimax(copy, depth + 1, false, alpha, beta, maxDepth);
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha) break;
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board.isCellEmpty(i, j)) {
                        GameBoard copy = board.copy();
                        copy.setCell(i, j, opponentSymbol);
                        int eval = minimax(copy, depth + 1, true, alpha, beta, maxDepth);
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha) break;
                    }
                }
            }
            return minEval;
        }
    }
    
    private int evaluateBoard(GameBoard board) {
        int score = 0;
        int size = board.getSize();
        int center = size / 2;
        
        // Prefer center
        if (board.getCell(center, center) == symbol) {
            score += 3;
        } else if (board.getCell(center, center) == opponentSymbol) {
            score -= 3;
        }
        
        // Prefer corners
        int[][] corners = {{0, 0}, {0, size-1}, {size-1, 0}, {size-1, size-1}};
        for (int[] corner : corners) {
            if (board.getCell(corner[0], corner[1]) == symbol) {
                score += 2;
            } else if (board.getCell(corner[0], corner[1]) == opponentSymbol) {
                score -= 2;
            }
        }
        
        return score;
    }
    
    private List<int[]> getEmptyCells(GameBoard board) {
        List<int[]> emptyCells = new ArrayList<>();
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isCellEmpty(i, j)) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells;
    }
}
