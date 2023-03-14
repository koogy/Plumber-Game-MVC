package com.company.Model.Command;

import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;
import com.company.View.PipeGUI;

public class RemoveSourceCommand implements Command {

    PlumberGame model;
    BoardGUI board;
    PipeGUI pipeGUI;
    Pipe source;

    int row;
    int col;

    public RemoveSourceCommand(PlumberGame model, BoardGUI board, PipeGUI pipeGUI, int row, int col) {
        this.model = model;
        this.board = board;
        this.row = row;
        this.col = col;

        this.pipeGUI = pipeGUI;
        this.source = pipeGUI.getPipe();
    }

    @Override
    public boolean execute() {
        System.out.println("============================================================");
        System.out.println("EXECUTING COMMAND : [REMOVE SOURCE COMMAND]");
        System.out.println("============================================================");
        model.getBoard()[row][col] = new Pipe("X");
        board.removePipe(source);
        return true;
    }

    @Override
    public boolean undo() {
        model.getBoard()[row][col] = source;
        board.getPipes().add(pipeGUI);
        return true;
    }

    @Override
    public boolean redo() {
        model.getBoard()[row][col] = new Pipe("X");
        board.removePipe(source);
        return true;
    }

    @Override
    public Pipe getPipe() {
        return source;
    }
}
