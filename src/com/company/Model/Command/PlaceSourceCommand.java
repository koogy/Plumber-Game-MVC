package com.company.Model.Command;

import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;

import java.awt.*;

public class PlaceSourceCommand implements Command {

    PlumberGame model;
    BoardGUI board;
    Pipe source;
    Pipe previousPipe;
    int row;
    int col;

    public PlaceSourceCommand(PlumberGame model, BoardGUI board, Pipe source, int row, int col) {
        this.model = model;
        this.board = board;
        this.source = source;
        this.previousPipe = model.getBoard()[row][col];
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean execute() {
        System.out.println("============================================================");
        System.out.println("EXECUTING COMMAND : [PLACE SOURCE COMMAND]");
        System.out.println("============================================================");
        model.getBoard()[row][col] = source;
        board.createPieceGUI(model.getBoard()[row][col], row, col);

        model.getSources().add(new Point(row, col));
        return true;
    }

    @Override
    public boolean undo() {
        model.getBoard()[row][col] = new Pipe("X");
        model.getSources().remove(model.getSources().size() - 1);
        board.getPipes().pop();
        return true;
    }

    @Override
    public boolean redo() {
        model.getBoard()[row][col] = source;
        board.createPieceGUI(model.getBoard()[row][col], row, col);
        model.getSources().add(new Point(row, col));
        return true;
    }

    @Override
    public Pipe getPipe() {
        return null;
    }
}
