# Laporan Proyek: Tic Tac Toe - Paper & Pencil Edition

---

## 1. Judul

**Tic Tac Toe - Paper & Pencil Edition**

Game Tic Tac Toe dengan tampilan minimalis seperti coretan di buku tulis, dilengkapi dengan dua mode permainan: Classic dan Upnormal.

---

## 2. Deskripsi

### Gambaran Umum
Aplikasi ini adalah permainan Tic Tac Toe yang dikembangkan menggunakan Java Swing dengan tema visual "Paper & Pencil" - tampilan minimalis yang menyerupai coretan pensil di buku tulis.

### Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
| **Mode Classic** | Permainan standar 3x3 tanpa power-ups |
| **Mode Upnormal** | Permainan 5x5 atau 7x7 dengan power-ups |
| **vs Computer** | Bermain melawan AI dengan 3 tingkat kesulitan |
| **2 Players** | Bermain berdua secara bergantian |
| **Power-Ups** | Bomb, Shield, Swap (hanya mode Upnormal) |

### Power-Ups (Mode Upnormal)

| Power-Up | Fungsi |
|----------|--------|
| **Bomb** | Membersihkan area 3x3 dari semua simbol |
| **Shield** | Melindungi cell milik sendiri dari Swap |
| **Swap** | Mengubah simbol lawan menjadi milik sendiri |

### Tingkat Kesulitan AI

| Level | Algoritma |
|-------|-----------|
| Easy | Random move |
| Medium | 50% Minimax, 50% random |
| Hard | Minimax dengan Alpha-Beta Pruning |

---

## 3. Rancangan Kelas

### Class Diagram

```mermaid
classDiagram
    class Player {
        <<abstract>>
        #String name
        #char symbol
        #int score
        #int streak
        #List~PowerUp~ powerUps
        +getName() String
        +getSymbol() char
        +getScore() int
        +addScore(int points)
        +hasPowerUp(Class type) boolean
        +usePowerUp(Class type) PowerUp
        +resetPowerUps()
        +makeMove(GameBoard board)* int[]
    }
    
    class HumanPlayer {
        +makeMove(GameBoard board) int[]
    }
    
    class AIPlayer {
        -Difficulty difficulty
        -Random random
        -char opponentSymbol
        +makeMove(GameBoard board) int[]
        -makeEasyMove(GameBoard board) int[]
        -makeMediumMove(GameBoard board) int[]
        -makeHardMove(GameBoard board) int[]
        -minimax() int
    }
    
    class GameBoard {
        -char[][] board
        -int size
        -boolean[][] shielded
        -int winCondition
        +getCell(int row, int col) char
        +setCell(int row, int col, char symbol)
        +isShielded(int row, int col) boolean
        +checkWinner() char
        +getWinningCells() int[][]
        +isFull() boolean
        +copy() GameBoard
    }
    
    class PowerUp {
        <<interface>>
        +getName() String
        +getDescription() String
        +canUse(GameBoard, int, int) boolean
        +use(GameBoard, int, int, char, char)
    }
    
    class BombPowerUp {
        +use() : clears 3x3 area
    }
    
    class ShieldPowerUp {
        +use() : protects cell
    }
    
    class SwapPowerUp {
        +use() : converts enemy cell
    }
    
    class GameFrame {
        -CardLayout cardLayout
        -MenuPanel menuPanel
        -GamePanel gamePanel
        -int gridSize
        -boolean vsAI
        -boolean classicMode
        +showMenu()
        +startGame()
    }
    
    class MenuPanel {
        -GameFrame frame
        -JComboBox gridSizeCombo
        -JToggleButton classicBtn
        -JToggleButton upnormalBtn
    }
    
    class GamePanel {
        -GameFrame frame
        -GameBoard board
        -Player player1
        -Player player2
        -Player currentPlayer
        -boolean classicMode
        +initGame()
        -handleCellClick()
        -makeMove()
        -switchPlayer()
    }
    
    class TicTacToe {
        +main(String[] args)$
    }
    
    Player <|-- HumanPlayer
    Player <|-- AIPlayer
    PowerUp <|.. BombPowerUp
    PowerUp <|.. ShieldPowerUp
    PowerUp <|.. SwapPowerUp
    Player "1" o-- "*" PowerUp
    GamePanel --> GameBoard
    GamePanel --> Player
    GameFrame --> MenuPanel
    GameFrame --> GamePanel
    TicTacToe --> GameFrame
```

### Penjelasan Relasi Kelas

| Relasi | Deskripsi |
|--------|-----------|
| `Player` → `HumanPlayer`, `AIPlayer` | Inheritance - Player adalah abstract class |
| `PowerUp` → `BombPowerUp`, `ShieldPowerUp`, `SwapPowerUp` | Implementation - PowerUp adalah interface |
| `Player` ◇ `PowerUp` | Aggregation - Player memiliki list PowerUp |
| `GamePanel` → `GameBoard`, `Player` | Association - GamePanel menggunakan kedua class |
| `GameFrame` → `MenuPanel`, `GamePanel` | Composition - GameFrame memiliki kedua panel |

---

## 4. Gambar Aplikasi dan Penjelasan

### 4.1 Menu Utama (Classic Mode)

![Menu Utama](menu_classic.png)

**Penjelasan:**
- **Game Type**: Pilihan antara Classic (3x3) dan Upnormal (5x5/7x7)
- **Player Mode**: vs Computer atau 2 Players
- **AI Difficulty**: Easy, Medium, atau Hard
- Tampilan menggunakan tema paper & pencil dengan warna cream

---

### 4.2 Menu Utama (Upnormal Mode)

![Menu Upnormal](menu_upnormal.png)

**Penjelasan:**
- Muncul pilihan **Grid Size** (5x5 atau 7x7)
- Muncul informasi **Power-Ups** yang tersedia
- Power-ups: Bomb, Shield, Swap

---

### 4.3 Permainan Classic Mode

![Game Classic](game_classic.png)

**Penjelasan:**
- Grid 3x3 standar
- X = warna biru pensil
- O = warna merah pensil
- Tidak ada power-ups
- Panel kiri/kanan menampilkan score pemain

---

### 4.4 Permainan Upnormal Mode

![Game Upnormal](game_upnormal.png)

**Penjelasan:**
- Grid 5x5 atau 7x7
- Terdapat tombol power-up di bagian bawah
- Player dapat menggunakan Bomb, Shield, atau Swap
- Sel yang di-shield ditandai dengan warna biru muda

---

### 4.5 Tampilan Kemenangan

![Win Screen](game_win.png)

**Penjelasan:**
- Sel pemenang ditandai dengan warna hijau
- Status menampilkan pemenang dan poin yang didapat
- Streak bonus ditambahkan jika menang berturut-turut

---

## Teknologi yang Digunakan

| Teknologi | Penggunaan |
|-----------|------------|
| Java SE | Bahasa pemrograman utama |
| Java Swing | Framework GUI |
| AWT Graphics2D | Custom rendering untuk tema |
| Minimax Algorithm | AI untuk mode Hard |
| Alpha-Beta Pruning | Optimasi algoritma Minimax |

---

*Laporan ini dibuat untuk mata kuliah PBO B*
