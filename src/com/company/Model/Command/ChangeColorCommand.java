package com.company.Model.Command;

import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;

public class ChangeColorCommand implements Command {
    PlumberGame model;
    BoardGUI board;
    Pipe pipe;
    int row;
    int col;

    public ChangeColorCommand(PlumberGame model, BoardGUI board, Pipe pipe, int row, int col) {
        this.model = model;
        this.board = board;
        this.pipe = pipe;
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean execute() {
        System.out.println("============================================================");
        System.out.println("EXECUTING COMMAND : [CHANGE COLOR COMMAND]");
        System.out.println("============================================================");
        pipe.setPipe_color(pipe.getPipe_color().next());

        char c = '0';
        switch (pipe.getPipe_color()) {
            case RED -> c = 'R';
            case GREEN -> c = 'G';
            case BLUE -> c = 'B';
            case YELLOW -> c = 'Y';
        }
        pipe.setPipe_name(c + pipe.getPipe_name().substring(1, pipe.getPipe_name().length()));
        model.coloringBoard();
        return true;
    }

    @Override
    public boolean undo() {
        pipe.setPipe_color(pipe.getPipe_color().previous());
        model.coloringBoard();
        return true;
    }

    @Override
    public boolean redo() {
        pipe.setPipe_color(pipe.getPipe_color().next());
        model.coloringBoard();
        return true;
    }

    @Override
    public Pipe getPipe() {
        return pipe;
    }
}
