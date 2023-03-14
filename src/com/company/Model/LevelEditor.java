package com.company.Model;

public class LevelEditor extends PlumberGame {

    public LevelEditor(int level) {
        super(level);
        loadInventory();
    }

    public void genenerateBoard(int row, int col) {
        super.row = row;
        super.col = col;
        board = new Pipe[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = new Pipe("X");
            }
        }
    }

    public void loadInventory() {

        inventory.put("C0", 1000);
        inventory.put("O0", 1000);

        inventory.put("L0", 1000);
        inventory.put("L1", 1000);

        inventory.put("T1", 1000);
        inventory.put("T2", 1000);

        inventory.put("T0", 1000);
        inventory.put("T3", 1000);

        inventory.put("F0", 1000);
        inventory.put("F1", 1000);

        inventory.put("F3", 1000);
        inventory.put("F2", 1000);

        char[] colors = {'R', 'G', 'Y', 'B'};
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String key = colors[i] + "" + j;
                inventory.put(key, 1000);
            }
        }
    }


}
