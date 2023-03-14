package com.company.Model;

import com.company.Enums.PipeType;
import com.company.Enums.PlumberColor;
import com.company.Model.Command.CommandManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class PlumberGame {

    int row;
    int col;

    Pipe[][] board;
    HashMap<String, Integer> inventory;
    ArrayList<Point> sources;

    CommandManager commandManager;

    int level;


    public PlumberGame() {
        commandManager = new CommandManager();
        inventory = new HashMap<>();
        sources = new ArrayList<>();
    }

    public PlumberGame(int level) {
        commandManager = new CommandManager();
        inventory = new HashMap<>();
        sources = new ArrayList<>();
        this.level = level;
        loadLevel(level);
        /*        resetBoard();*/

    }

    public void loadLevel(int level) {
        String filename = "src/com/company/levels/level" + level + ".p";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String current_line = br.readLine();

            Integer[] size = Arrays.stream(current_line.split(" "))
                    .map(Integer::valueOf)
                    .toArray(Integer[]::new);

            row = size[0];
            col = size[1];
            board = new Pipe[row][col];
            int current_row = 0;
            while ((current_line = br.readLine()) != null) {
                Pipe[] pieces = Arrays.stream(current_line.split(" "))
                        .map(pipe -> {
                            Pipe p = new Pipe(pipe.trim());
                            PipeType type = p.getPipe_type();
                            String key = p.getPipe_name();

                            if (type != PipeType.EMPTY && !key.isEmpty()) {
                                inventory.merge(key, 1, Integer::sum);
                            }

                            return p;
                        })
                        .filter(s -> !(s.getPipe_name().isEmpty()))
                        .toArray(Pipe[]::new);

                board[current_row] = pieces;

                for (int j = 0; j < pieces.length; j++) {
                    if (pieces[j].getPipe_type() == PipeType.SOURCE) {
                        sources.add(new Point(current_row, j));
                    }
                }
                current_row++;

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*
     * PARCOURIR LE BOARD
     * POUR CHAQUE PIPE, CREER UN
     *
     *
     *
     *
     * */


    public void printInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ":" + value);
        }
    }

    public void removePipe(int row, int col) {
        inventory.put(board[row][col].getPipe_name(), inventory.get(board[row][col].getPipe_name()) + 1);
        board[row][col] = new Pipe("X");
    }

    public void resetBoard() {
        sources.clear();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = new Pipe("X");
            }
        }
    }

    public void resetLevel() {

        for (int i = 1; i < row - 1; i++) {
            for (int j = 1; j < col - 1; j++) {
                if (!board[i][j].fixed)
                    board[i][j] = new Pipe("X");
            }
        }
    }


    public void displayBoard() {
        System.out.println("======================================");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.print(board[i][j].getPipe_name() + " ");
            }
            System.out.println();
        }
        System.out.println("======================================");
    }


    public boolean isWinning() {
        Stack<PlumberColor> stack = new Stack<>();

        for (Point p : sources) {
            int x = p.x;
            int y = p.y;

            Pipe pipe = board[x][y];
            if (!_isWinning(x, y, new boolean[row][col], stack, x, y))
                return false;
            stack.remove(pipe.getPipe_color());
        }
        return stack.isEmpty();
    }

    private boolean _isWinning(int x, int y, boolean[][] marks, Stack<PlumberColor> stack, int oldX, int oldY) {
        if (x >= 0 && y >= 0 && x < row && y < col) {
            if (marks[x][y])
                return true;
            if (!isLinked(x, y, oldX, oldY))
                return false;
            Pipe pipe = board[x][y];
            marks[x][y] = true;

            switch (pipe.getPipe_type()) {
                case SOURCE -> {
                    if (!stack.contains(pipe.getPipe_color())) {
                        stack.add(pipe.getPipe_color());
                        if (Arrays.equals(pipe.orientation, new boolean[]{true, false, false, false}))
                            return _isWinning(x - 1, y, marks, stack, x, y);
                        else if (Arrays.equals(pipe.orientation, new boolean[]{false, true, false, false}))
                            return _isWinning(x, y + 1, marks, stack, x, y);
                        else if (Arrays.equals(pipe.orientation, new boolean[]{false, false, false, true}))
                            return _isWinning(x, y - 1, marks, stack, x, y);
                        else
                            return _isWinning(x + 1, y, marks, stack, x, y);
                    } else {
                        return true;
                    }
                }
                case LINE -> {
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, false})) {
                        return _isWinning(x + 1, y, marks, stack, x, y) &&
                                _isWinning(x - 1, y, marks, stack, x, y);
                    } else {
                        return _isWinning(x, y + 1, marks, stack, x, y) &&
                                _isWinning(x, y - 1, marks, stack, x, y);
                    }
                }
                case OVER -> {
                    marks[x][y] = false;
                    if (x == oldX && y == oldY + 1)
                        return _isWinning(x, y + 1, marks, stack, x, y);
                    else if (x == oldX && y == oldY - 1)
                        return _isWinning(x, y - 1, marks, stack, x, y);
                    else if (x == oldX + 1 && y == oldY)
                        return _isWinning(x + 1, y, marks, stack, x, y);
                    else
                        return _isWinning(x - 1, y, marks, stack, x, y);
                }
                case TURN -> {
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, true, false, false})) {
                        return _isWinning(x - 1, y, marks, stack, x, y) &&
                                _isWinning(x, y + 1, marks, stack, x, y);
                    } else if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, false})) {
                        return _isWinning(x, y + 1, marks, stack, x, y) &&
                                _isWinning(x + 1, y, marks, stack, x, y);
                    } else if (Arrays.equals(pipe.orientation, new boolean[]{false, false, true, true})) {
                        return _isWinning(x + 1, y, marks, stack, x, y) &&
                                _isWinning(x, y - 1, marks, stack, x, y);
                    } else {
                        return _isWinning(x, y - 1, marks, stack, x, y) &&
                                _isWinning(x - 1, y, marks, stack, x, y);
                    }
                }
                case FORK -> {
                    if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, true})) {
                        return _isWinning(x, y + 1, marks, stack, x, y) &&
                                _isWinning(x, y - 1, marks, stack, x, y) &&
                                _isWinning(x + 1, y, marks, stack, x, y);
                    } else if (Arrays.equals(pipe.orientation, new boolean[]{true, true, true, false})) {
                        return _isWinning(x, y + 1, marks, stack, x, y) &&
                                _isWinning(x - 1, y, marks, stack, x, y) &&
                                _isWinning(x + 1, y, marks, stack, x, y);
                    } else if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, true})) {
                        return _isWinning(x - 1, y, marks, stack, x, y) &&
                                _isWinning(x, y - 1, marks, stack, x, y) &&
                                _isWinning(x + 1, y, marks, stack, x, y);
                    } else {
                        return _isWinning(x - 1, y, marks, stack, x, y) &&
                                _isWinning(x, y - 1, marks, stack, x, y) &&
                                _isWinning(x, y + 1, marks, stack, x, y);
                    }
                }
                case CROSS -> {
                    return _isWinning(x - 1, y, marks, stack, x, y) &&
                            _isWinning(x + 1, y, marks, stack, x, y) &&
                            _isWinning(x, y - 1, marks, stack, x, y) &&
                            _isWinning(x, y + 1, marks, stack, x, y);
                }

                default -> {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean isLinked(int x, int y, int oldX, int oldY) {
        if (x >= 0 && y >= 0 && x < row && y < col
                && (x != oldX || y != oldY)) {
            Pipe pipe = board[x][y];
            switch (pipe.getPipe_type()) {
                case SOURCE -> {
                    // isTop
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, false, false, false}))
                        return x == oldX + 1;
                        // isRight
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, true, false, false}))
                        return y == oldY - 1;
                        // isLeft
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, false, false, true}))
                        return y == oldY + 1;
                        // isBot
                    else
                        return x == oldX - 1;
                }
                case LINE -> {
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, false}))
                        return x == oldX + 1 || x == oldX - 1;
                    else
                        return y == oldY + 1 || y == oldY - 1;
                }
                case OVER, CROSS -> {
                    return true;
                }
                case TURN -> {
                    // isTop && isRight
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, true, false, false}))
                        return x == oldX + 1 || y == oldY - 1;
                        // isRight && isBot
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, false}))
                        return y == oldY - 1 || x == oldX - 1;
                        // isBot && isLeft
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, false, true, true}))
                        return x == oldX - 1 || y == oldY + 1;
                        // isLeft && isTop
                    else
                        return y == oldY + 1 || x == oldX + 1;
                }
                case FORK -> {
                    // !isTop
                    if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, true}))
                        return y == oldY + 1 || y == oldY - 1 || x == oldX - 1;
                        // !isLeft
                    else if (Arrays.equals(pipe.orientation, new boolean[]{true, true, true, false}))
                        return y == oldY - 1 || x == oldX - 1 || x == oldX + 1;
                        // !isRight
                    else if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, true}))
                        return x == oldX - 1 || y == oldY + 1 || x == oldX + 1;
                        // !isBot
                    else
                        return x == oldX + 1 || y == oldY - 1 || y == oldY + 1;
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLinkedToSource(int x, int y) {
        boolean[][] marks = new boolean[row][col];
        if (x < 0 || y < 0 || x >= row || y >= col)
            throw new IndexOutOfBoundsException("isLinkedToSource");
        return _isLinkedToSource(x, y, marks, x, y);
    }

    private boolean _isLinkedToSource(int x, int y, boolean[][] marks, int oldX, int oldY) {
        if (x >= 0 && y >= 0 && x < row && y < col && !marks[x][y]) {
            if (!isLinked(x, y, oldX, oldY))
                return false;
            Pipe pipe = board[x][y];
            marks[x][y] = true;
            //System.out.println("isLinked: X = " + x + " - Y = " + y);

            switch (pipe.getPipe_type()) {
                case SOURCE -> {
                    spreadColor(x, y, pipe.getPipe_color());
                    return true;
                }
                case LINE -> {
                    // isTop && isBot
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, false})) {
                        return _isLinkedToSource(x - 1, y, marks, x, y) | _isLinkedToSource(x + 1, y, marks, x, y);
                    }
                    return _isLinkedToSource(x, y - 1, marks, x, y) | _isLinkedToSource(x, y + 1, marks, x, y);
                }
                case OVER -> {
                    if (oldX == x && oldY == y)
                        return _isLinkedToSource(x - 1, y, marks, x, y) | _isLinkedToSource(x + 1, y, marks, x, y)
                                | _isLinkedToSource(x, y - 1, marks, x, y) | _isLinkedToSource(x, y + 1, marks, x, y);

                    if (x == oldX && y == oldY + 1)
                        return _isLinkedToSource(x, y + 1, marks, x, y);
                    else if (x == oldX && y == oldY - 1)
                        return _isLinkedToSource(x, y - 1, marks, x, y);
                    else if (x == oldX + 1 && y == oldY)
                        return _isLinkedToSource(x + 1, y, marks, x, y);
                    else
                        return _isLinkedToSource(x - 1, y, marks, x, y);

                }
                case CROSS -> {
                    return _isLinkedToSource(x - 1, y, marks, x, y) | _isLinkedToSource(x + 1, y, marks, x, y)
                            | _isLinkedToSource(x, y - 1, marks, x, y) | _isLinkedToSource(x, y + 1, marks, x, y);
                }
                case FORK -> {
                    // !isTop
                    if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, true})) {
                        return _isLinkedToSource(x, y - 1, marks, x, y) | _isLinkedToSource(x, y + 1, marks, x, y)
                                | _isLinkedToSource(x + 1, y, marks, x, y);
                    }
                    // !isLeft
                    if (!Arrays.equals(pipe.orientation, new boolean[]{true, true, true, false})) {
                        return _isLinkedToSource(x, y + 1, marks, x, y) | _isLinkedToSource(x - 1, y, marks, x, y)
                                | _isLinkedToSource(x + 1, y, marks, x, y);
                    }
                    // !isRight
                    if (!Arrays.equals(pipe.orientation, new boolean[]{true, false, true, true})) {
                        return _isLinkedToSource(x, y - 1, marks, x, y) | _isLinkedToSource(x - 1, y, marks, x, y)
                                | _isLinkedToSource(x + 1, y, marks, x, y);
                    }
                    // !isBot
                    return _isLinkedToSource(x - 1, y, marks, x, y) | _isLinkedToSource(x, y - 1, marks, x, y)
                            | _isLinkedToSource(x, y + 1, marks, x, y);
                }
                case TURN -> {
                    // isTop && isRight
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, false}))
                        return _isLinkedToSource(x - 1, y, marks, x, y) | _isLinkedToSource(x, y + 1, marks, x, y);
                    // isRight && isBot
                    if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, false}))
                        return _isLinkedToSource(x, y + 1, marks, x, y) | _isLinkedToSource(x + 1, y, marks, x, y);
                    // isBot && isLeft
                    if (Arrays.equals(pipe.orientation, new boolean[]{false, false, true, true}))
                        return _isLinkedToSource(x, y - 1, marks, x, y) | _isLinkedToSource(x + 1, y, marks, x, y);
                    // isLeft && isTop
                    return _isLinkedToSource(x, y - 1, marks, x, y) | _isLinkedToSource(x - 1, y, marks, x, y);
                }
                default -> {
                    return false;
                }
            }
        } else
            return false;
    }

    public void coloringBoard() {
        boolean[][] marks = new boolean[row][col];
        for (int x = 1; x < row - 1; x++) {
            for (int y = 1; y < col - 1; y++) {
                if (!marks[x][y]) {
                    if (board[x][y].getPipe_type() == PipeType.OVER)
                        board[x][y].setPipeUnder_color(PlumberColor.NEUTRAL);
                    board[x][y].setPipe_color(PlumberColor.NEUTRAL);
                }
            }
        }

        for (Point p : sources) {
            int x = p.x;
            int y = p.y;
            spreadColor(x, y, board[x][y].getPipe_color());
        }
    }

    // Called when isLinkedToSource() == true -> Spread the color of the source it is linked
    public void spreadColor(int x, int y, PlumberColor color) {
        boolean[][] marks = new boolean[row][col];
        _spreadColor(x, y, color, marks, x, y);
    }

    public void _spreadColor(int x, int y, PlumberColor color, boolean[][] marks, int oldX, int oldY) {
        if (x >= 0 && y >= 0 && x < row && y < col) {
            if (!isLinked(x, y, oldX, oldY))
                return;
            /*    System.out.println("SpreadColor: X = " + x + " - Y = " + y);*/
            if (marks[x][y])
                return;
            marks[x][y] = true;
            Pipe pipe = board[x][y];

            // 2nd source is another color -> skip
            if (pipe.getPipe_type() == PipeType.SOURCE && pipe.getPipe_color() != color)
                return;
            // Pipe is in the board
            if (pipe.getPipe_type() != PipeType.SOURCE && pipe.getPipe_type() != PipeType.OVER) {
                // PipeType == NEUTRAL or spreadColor(PlumberColor.GRAY)
                if (pipe.getPipe_color() == PlumberColor.NEUTRAL
                        || color == PlumberColor.GRAY
                        || pipe.getPipe_color() == color)
                    pipe.setPipe_color(color);

                else
                    _spreadColor(x, y, PlumberColor.GRAY, new boolean[row][col], x, y);
            }

            switch (pipe.getPipe_type()) {
                case SOURCE -> {
                    // isTop
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, false, false, false}))
                        _spreadColor(x - 1, y, color, marks, x, y);
                        // isRight
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, true, false, false}))
                        _spreadColor(x, y + 1, color, marks, x, y);
                        // isLeft
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, false, false, true}))
                        _spreadColor(x, y - 1, color, marks, x, y);
                        // isBot
                    else
                        _spreadColor(x + 1, y, color, marks, x, y);
                }
                case LINE -> {
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, false})) {
                        _spreadColor(x + 1, y, color, marks, x, y);
                        _spreadColor(x - 1, y, color, marks, x, y);
                    } else {
                        _spreadColor(x, y + 1, color, marks, x, y);
                        _spreadColor(x, y - 1, color, marks, x, y);
                    }
                }
                case OVER -> {
                    if (x == oldX && y == oldY + 1) {
                        pipe.setPipe_color(color);
                        _spreadColor(x, y + 1, color, marks, x, y);
                    } else if (x == oldX && y == oldY - 1) {
                        pipe.setPipe_color(color);
                        _spreadColor(x, y - 1, color, marks, x, y);
                    } else {
                        pipe.setPipeUnder_color(color);
                        if (x == oldX + 1 && y == oldY)
                            _spreadColor(x + 1, y, color, marks, x, y);
                        else
                            _spreadColor(x - 1, y, color, marks, x, y);
                    }
                }
                case FORK -> {
                    // !isTop
                    if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, true})) {
                        _spreadColor(x, y + 1, color, marks, x, y);
                        _spreadColor(x, y - 1, color, marks, x, y);
                        _spreadColor(x + 1, y, color, marks, x, y);
                    }
                    // !isLeft
                    else if (Arrays.equals(pipe.orientation, new boolean[]{true, true, true, false})) {
                        _spreadColor(x, y + 1, color, marks, x, y);
                        _spreadColor(x - 1, y, color, marks, x, y);
                        _spreadColor(x + 1, y, color, marks, x, y);
                    }
                    // !isRight
                    else if (Arrays.equals(pipe.orientation, new boolean[]{true, false, true, true})) {
                        _spreadColor(x - 1, y, color, marks, x, y);
                        _spreadColor(x, y - 1, color, marks, x, y);
                        _spreadColor(x + 1, y, color, marks, x, y);
                    }
                    // !isBot
                    else {
                        _spreadColor(x - 1, y, color, marks, x, y);
                        _spreadColor(x, y - 1, color, marks, x, y);
                        _spreadColor(x, y + 1, color, marks, x, y);
                    }
                }
                case TURN -> {
                    // isTop && isRight
                    if (Arrays.equals(pipe.orientation, new boolean[]{true, true, false, false})) {
                        _spreadColor(x - 1, y, color, marks, x, y);
                        _spreadColor(x, y + 1, color, marks, x, y);
                    }
                    // isRight && isBot
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, true, true, false})) {
                        _spreadColor(x, y + 1, color, marks, x, y);
                        _spreadColor(x + 1, y, color, marks, x, y);
                    }
                    // isBot && isLeft
                    else if (Arrays.equals(pipe.orientation, new boolean[]{false, false, true, true})) {
                        _spreadColor(x + 1, y, color, marks, x, y);
                        _spreadColor(x, y - 1, color, marks, x, y);
                    } else {
                        _spreadColor(x, y - 1, color, marks, x, y);
                        _spreadColor(x - 1, y, color, marks, x, y);
                    }
                }
                case CROSS -> {
                    _spreadColor(x - 1, y, color, marks, x, y);
                    _spreadColor(x + 1, y, color, marks, x, y);
                    _spreadColor(x, y - 1, color, marks, x, y);
                    _spreadColor(x, y + 1, color, marks, x, y);
                }
                default -> pipe.setPipe_color(PlumberColor.NEUTRAL);
            }
        }
    }

    /* GETTERS AND SETTERS */
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public ArrayList<Point> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Point> sources) {
        this.sources = (ArrayList<Point>) sources.clone();
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Pipe[][] getBoard() {
        return board;
    }

    public void setBoard(Pipe[][] board) {
        /*copy array*/
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                this.board[i][j] = board[i][j];
            }
        }
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
