package com.company.Model.Command;

import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;
import com.company.View.PipeGUI;

import java.awt.*;

public class RemovePipeCommand implements Command{

    PlumberGame model;
    BoardGUI board;
    PipeGUI pipeGUI;
    Pipe pipe;
    int row;
    int col;

    public RemovePipeCommand(PlumberGame model, BoardGUI board, PipeGUI pipe, int row, int col){
        this.model = model;
        this.board = board;
        this.pipeGUI = pipe;
        this.pipe = pipeGUI.getPipe();

        System.out.println("Remove " + pipe + " " + row + "  " + col);
        this.row = row;
        this.col = col;

    }

    @Override
    public boolean execute() {
        System.out.println("============================================================");
        System.out.println("EXECUTING COMMAND : [RESET PIPE COMMAND]");
        System.out.println("============================================================");
        board.removePipe(pipe);
        model.removePipe(row, col);
        return true;
    }

    @Override
    public boolean undo() {
        System.out.println(row + " " + col);
        model.getInventory().put(pipe.getPipe_name(), model.getInventory().get(pipe.getPipe_name()) + 1);
        model.getBoard()[row][col] = pipe;
        pipeGUI.setCoordinate(board.convertIndicesToPositionBoard(new Point(col *board.getPipeHeight()/ board.getPipeWidth(), (row+1)*board.getPipeWidth() / board.getPipeHeight())));
        board.getPipes().push(pipeGUI);

        return true;
    }

    @Override
    public boolean redo() {
        model.getInventory().put(pipe.getPipe_name(), model.getInventory().get(pipe.getPipe_name()) - 1);
        board.removePipe(pipe);
        model.removePipe(row, col);
        return true;
    }

    @Override
    public Pipe getPipe() {
        return null;
    }
}
