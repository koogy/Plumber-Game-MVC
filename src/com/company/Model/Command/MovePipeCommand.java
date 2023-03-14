package com.company.Model.Command;

import com.company.Enums.PipeType;
import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;
import com.company.View.PipeGUI;

import java.awt.*;

public class MovePipeCommand implements Command {

    PlumberGame model;
    BoardGUI board;
    PipeGUI pipeGUI;
    Pipe pipe;
    int row;
    int col;

    int oldRow;
    int oldCol;

    public MovePipeCommand(PlumberGame model, BoardGUI board, PipeGUI pipe, int oldRow, int oldCol, int newRow, int newCol) {
        this.model = model;
        this.board = board;
        this.pipeGUI = pipe;
        this.pipe = pipeGUI.getPipe();
        this.row = newRow;
        this.col = newCol;
        this.oldRow = oldRow;
        this.oldCol = oldCol;
    }

    @Override
    public boolean execute() {
        System.out.println("============================================================");
        System.out.println("EXECUTING COMMAND : [MOVE PIPE COMMAND]");
        System.out.println("============================================================");
        if (row > 0 && col > 0 && row < model.getRow() - 1 && col < model.getCol() - 1) {
            if (model.getBoard()[row][col].getPipe_type() == PipeType.EMPTY) {
                model.getBoard()[oldRow][oldCol] = model.getBoard()[row][col];
                model.getBoard()[row][col] = pipe;

                model.displayBoard();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean undo() {
        model.getBoard()[row][col] = model.getBoard()[oldRow][oldCol];
        model.getBoard()[oldRow][oldCol] = pipe;
        pipeGUI.setCoordinate(board.convertIndicesToPositionBoard(new Point(oldCol * board.getPipeHeight() / board.getPipeWidth(), (oldRow + 1) * board.getPipeWidth() / board.getPipeHeight())));

        model.coloringBoard();

        return false;
    }

    @Override
    public boolean redo() {
        Pipe temp = model.getBoard()[oldRow][oldCol];
        model.getBoard()[oldRow][oldCol] = model.getBoard()[row][col];
        model.getBoard()[row][col] = temp;
        pipeGUI.setCoordinate(board.convertIndicesToPositionBoard(new Point(col * board.getPipeHeight() / board.getPipeWidth(), (row + 1) * board.getPipeWidth() / board.getPipeHeight())));
        model.coloringBoard();
        return false;
    }

    @Override
    public Pipe getPipe() {
        return pipe;
    }


}
