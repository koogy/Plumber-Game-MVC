package com.company.Model.Command;

import com.company.Enums.PipeType;
import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;

public class PlacePipeCommand implements Command {

    PlumberGame model;
    BoardGUI board;
    Pipe pipe;
    Pipe previousPipe;
    int row;
    int col;

    public PlacePipeCommand(PlumberGame model, BoardGUI board, Pipe pipe, int row, int col) {
        this.model = model;
        this.board = board;
        this.pipe = pipe;
        System.out.println("Placepipe " + pipe);
        this.previousPipe = model.getBoard()[row][col];
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean execute() {
        System.out.println("============================================================");
        System.out.println("EXECUTING COMMAND : [PLACE PIPE COMMAND]");
        System.out.println("============================================================");
        if (row > 0 && col > 0 && row < model.getRow() - 1 && col < model.getCol() - 1) {
            if (model.getBoard()[row][col].getPipe_type() == PipeType.EMPTY) {
                model.getBoard()[row][col] = pipe;
                model.getInventory().put(pipe.getPipe_name(), model.getInventory().get(pipe.getPipe_name()) - 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean undo() {
        System.out.println("UNDO : " + pipe.getPipe_name() + " " + pipe);
        model.getInventory().put(pipe.getPipe_name(), model.getInventory().get(pipe.getPipe_name()) + 1);
        board.removePipe(pipe);
        model.getBoard()[row][col] = previousPipe;
        return true;
    }

    @Override
    public boolean redo() {

        model.getInventory().put(pipe.getPipe_name(), model.getInventory().get(pipe.getPipe_name()) - 1);
        board.placePipe(pipe);
        model.getBoard()[row][col] = pipe;

        return true;
    }

    public Pipe getPipe() {
        return pipe;
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }
}
