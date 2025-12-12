/**
 * GameBoard - Represents the game board with dynamic size
 * Supports 3x3, 4x4, and 5x5 grids
 */
public class GameBoard {
    private char[][] board;
    private int size;
    private boolean[][] shielded;
    private int winCondition;
    
    public GameBoard(int size) {
        this.size = size;
        this.board = new char[size][size];
        this.shielded = new boolean[size][size];
        this.winCondition = (size == 3) ? 3 : (size == 4) ? 3 : 4;
        initializeBoard();
    }
    
    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = ' ';
                shielded[i][j] = false;
            }
        }
    }
    
    public void reset() {
        initializeBoard();
    }
    
    public int getSize() {
        return size;
    }
    
    public char getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return ' ';
    }
    
    public boolean setCell(int row, int col, char symbol) {
        if (isValidPosition(row, col) && board[row][col] == ' ') {
            board[row][col] = symbol;
            return true;
        }
        return false;
    }
    
    public boolean forceSetCell(int row, int col, char symbol) {
        if (isValidPosition(row, col) && !shielded[row][col]) {
            board[row][col] = symbol;
            return true;
        }
        return false;
    }
    
    public boolean clearCell(int row, int col) {
        if (isValidPosition(row, col) && !shielded[row][col]) {
            board[row][col] = ' ';
            return true;
        }
        return false;
    }
    
    public boolean isShielded(int row, int col) {
        if (isValidPosition(row, col)) {
            return shielded[row][col];
        }
        return false;
    }
    
    public void setShield(int row, int col, boolean value) {
        if (isValidPosition(row, col)) {
            shielded[row][col] = value;
        }
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
    
    public boolean isCellEmpty(int row, int col) {
        return isValidPosition(row, col) && board[row][col] == ' ';
    }
    
    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
    
    public char checkWinner() {
        // Check rows
        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= size - winCondition; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i][j + k] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return first;
                }
            }
        }
        
        // Check columns
        for (int i = 0; i <= size - winCondition; i++) {
            for (int j = 0; j < size; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i + k][j] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return first;
                }
            }
        }
        
        // Check diagonals (top-left to bottom-right)
        for (int i = 0; i <= size - winCondition; i++) {
            for (int j = 0; j <= size - winCondition; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i + k][j + k] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return first;
                }
            }
        }
        
        // Check diagonals (top-right to bottom-left)
        for (int i = 0; i <= size - winCondition; i++) {
            for (int j = winCondition - 1; j < size; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i + k][j - k] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return first;
                }
            }
        }
        
        return ' ';
    }
    
    public int[][] getWinningCells() {
        // Check rows
        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= size - winCondition; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i][j + k] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        int[][] cells = new int[winCondition][2];
                        for (int k = 0; k < winCondition; k++) {
                            cells[k] = new int[]{i, j + k};
                        }
                        return cells;
                    }
                }
            }
        }
        
        // Check columns
        for (int i = 0; i <= size - winCondition; i++) {
            for (int j = 0; j < size; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i + k][j] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        int[][] cells = new int[winCondition][2];
                        for (int k = 0; k < winCondition; k++) {
                            cells[k] = new int[]{i + k, j};
                        }
                        return cells;
                    }
                }
            }
        }
        
        // Check diagonals (top-left to bottom-right)
        for (int i = 0; i <= size - winCondition; i++) {
            for (int j = 0; j <= size - winCondition; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i + k][j + k] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        int[][] cells = new int[winCondition][2];
                        for (int k = 0; k < winCondition; k++) {
                            cells[k] = new int[]{i + k, j + k};
                        }
                        return cells;
                    }
                }
            }
        }
        
        // Check diagonals (top-right to bottom-left)
        for (int i = 0; i <= size - winCondition; i++) {
            for (int j = winCondition - 1; j < size; j++) {
                char first = board[i][j];
                if (first != ' ') {
                    boolean win = true;
                    for (int k = 1; k < winCondition; k++) {
                        if (board[i + k][j - k] != first) {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        int[][] cells = new int[winCondition][2];
                        for (int k = 0; k < winCondition; k++) {
                            cells[k] = new int[]{i + k, j - k};
                        }
                        return cells;
                    }
                }
            }
        }
        
        return null;
    }
    
    public int countEmpty() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ' ') count++;
            }
        }
        return count;
    }
    
    public GameBoard copy() {
        GameBoard copy = new GameBoard(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy.board[i][j] = this.board[i][j];
                copy.shielded[i][j] = this.shielded[i][j];
            }
        }
        return copy;
    }
}
