package com.company.Model.Command;

import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.EditorGUI;
import com.company.View.PipeGUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class ResetBoardCommand implements Command {

    PlumberGame model;
    EditorGUI boardGUI;

    Pipe[][] board;
    Stack<PipeGUI> pipes;
    ArrayList<Point> sources;


    public ResetBoardCommand(PlumberGame model, EditorGUI board) {
        this.model = model;
        this.boardGUI = board;
        this.board = new Pipe[model.getRow()][model.getCol()];
        this.pipes = (Stack<PipeGUI>) board.getPipes().clone();
        this.sources = (ArrayList<Point>) model.getSources().clone();

        /*copy array*/
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                this.board[i][j] = model.getBoard()[i][j];
            }
        }
    }

    @Override
    public boolean execute() {
        System.out.println("============================================================");
        System.out.println("EXECUTING COMMAND : [RESET BOARD COMMAND]");
        System.out.println("============================================================");
        model.resetBoard();
        this.boardGUI.getPipes().clear();
        return true;
    }

    @Override
    public boolean undo() {

        model.setSources(sources);
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                System.out.print(board[i][j].getPipe_name() + " ");
            }
            System.out.println();
        }

        model.setBoard(board);
        boardGUI.setPipes(pipes);
        return true;
    }

    @Override
    public boolean redo() {
        model.resetBoard();
        this.boardGUI.getPipes().clear();
        return true;
    }

    @Override
    public Pipe getPipe() {
        return null;
    }


}
