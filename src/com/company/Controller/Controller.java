package com.company.Controller;

import com.company.Enums.PipeType;
import com.company.Model.Command.Command;
import com.company.Model.Command.MovePipeCommand;
import com.company.Model.Command.PlacePipeCommand;
import com.company.Model.Command.RemovePipeCommand;
import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;
import com.company.View.PipeGUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Stack;


public class Controller implements MouseListener, MouseMotionListener {

    protected PipeGUI currentPipeDragged;
    protected int offsetX;
    protected int offsetY;
    protected int originMousePositionX;
    protected int originMousePositionY;
    PlumberGame model;
    BoardGUI board;
    boolean insideBoard = false;

    public Controller(PlumberGame model) {
        this.model = model;
    }

    public void setView(BoardGUI board) {
        this.board = board;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /* Test color */
        int x = e.getX();
        int y = e.getY();
        System.out.println("[POSITION] " + x + ":" + y + "--" + y / board.getPipeHeight() + ":" + x / board.getPipeWidth());


        for (PipeGUI p : board.getPipes()) {
            if (isMouseOverPipe(p, x, y)) {
                System.out.println("========= PIPE INFORMATION =========");
                Pipe current_pipe = p.getPipe();
                System.out.println("========= PIPE INFORMATION =========");
                System.out.println(current_pipe.getPipe_name() + " " + current_pipe.getPipe_type() + " " + current_pipe.getPipe_color());
                break;
            }
        }
        board.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        originMousePositionX = x;
        originMousePositionY = y;
        if (isMouseOnInventory(x, y)) {
            board.generatePiece(y / board.getPipeHeight(), x / board.getPipeWidth());
            insideBoard = false;
        } else if (isMouseOnBoard(x, y)) {
            System.out.println("ON BOARD");
            insideBoard = true;
        }

        for (PipeGUI p : board.getPipes()) {
            if (isMouseOverPipe(p, x, y)) {
                offsetX = (int) (x - p.getCoordinate().getX());
                offsetY = (int) (y - p.getCoordinate().getY());
                System.out.println("SET CURRENT : " + p.getPipe().getPipe_name() + " " + p.getPipe().getPipe_type());
                System.out.println(p.getPipe().getPipe_color() + " " + p.getPipe().getPipeUnder_color());

                currentPipeDragged = p;
                break;
            }
        }


        board.repaint();
    }


    public boolean isMouseOnBorder(int px, int py) {
        return ((px == 0 && py != 0 && py != (model.getRow() - 1)) ||
                (px == model.getCol() - 1 && py != 0 && py != (model.getRow() - 1)) ||
                (py == 0 && px != 0 && px != model.getCol() - 1) ||
                (py == model.getRow() - 1 && px != 0 && px != model.getCol() - 1));
    }


    public boolean isMouseOverPipe(PipeGUI d, int x, int y) {
        int dx = (int) d.getCoordinate().getX();
        int dy = (int) d.getCoordinate().getY();
        return x >= dx && x <= dx + board.getPipeWidth() && y >= dy && y <= dy + board.getPipeHeight();
    }

    public boolean isMouseOnBoard(int x, int y) {
        int px = x / board.getPipeWidth();
        int py = y / board.getPipeHeight();
        return px > 0 && py > 0 && px < model.getCol() && py < model.getRow();
    }

    public boolean isMouseOnInventory(int x, int y) {
        return x / board.getPipeWidth() > model.getCol() - 1 && y / board.getPipeHeight() < 6;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        boolean pipePlaced = false;


        if (currentPipeDragged != null) {
            for (PipeGUI p : board.getPipes()) {
                if (currentPipeDragged == p) {
                    p.setCoordinate(board.convertIndicesToPositionBoard(new Point(x / board.getPipeWidth(), y / board.getPipeHeight())));
                    break;
                }
            }
            if (isMouseOnBoard(x, y)) {
                System.out.println("[ON RELEASE] INSIDE BOARD");
                if (!insideBoard) {
                    System.out.println("[ON RELEASE] COMING FROM INVENTORY TO BOARD");
                    Command command = new PlacePipeCommand(model, board, currentPipeDragged.getPipe(), y / board.getPipeHeight(), x / board.getPipeWidth());
                    pipePlaced = model.getCommandManager().addCommand(command);

                } else {
                    System.out.println("[ON RELEASE] COMING FROM BOARD TO BOARD");
                    int oldY = originMousePositionY / board.getPipeHeight();
                    int oldX = originMousePositionX / board.getPipeWidth();
                    int newY = y / board.getPipeHeight();
                    int newX = x / board.getPipeWidth();
                    Command command = new MovePipeCommand(model, board, currentPipeDragged, oldY, oldX, newY, newX);
                    pipePlaced = model.getCommandManager().addCommand(command);

                }

                //System.out.println("IsLinked: " + model.isLinkedToSource(y / 120, x / 120));
                System.out.println("isWinning: " + model.isWinning());
                model.coloringBoard();
            }

            if (!pipePlaced) {
                if (isMouseOnBoard(x, y)) {
                    if (!insideBoard) {
                        board.getPipes().pop();
                    } else {
                        System.out.println("CANT BE PLACED : [ON RELEASE] COMING FROM BOARD TO BOARD");
                        currentPipeDragged.setCoordinate(board.convertIndicesToPositionBoard(new Point(originMousePositionX / board.getPipeWidth(), originMousePositionY / board.getPipeHeight())));

                    }
                } else {
                    if (!insideBoard) {
                        System.out.println("[ON RELEASE] COMING FROM INVENTORY TO INVENTORY");
                        System.out.println("[DESTROY] PIPE");
                        board.getPipes().pop();
                    } else {
                        System.out.println("[ON RELEASE] COMING FROM BOARD TO BLABLA");
                        int row = originMousePositionY / board.getPipeHeight();
                        int col = originMousePositionX / board.getPipeWidth();
                        System.out.println("removing : " + currentPipeDragged.getPipe().getPipe_type());
                        Command command = new RemovePipeCommand(model, board, currentPipeDragged, row, col);
                        model.getCommandManager().addCommand(command);
                    }
                }
            }
            currentPipeDragged = null;
            board.repaint();

        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentPipeDragged != null && currentPipeDragged.getPipe().getPipe_type() != PipeType.SOURCE) {
            currentPipeDragged.setCoordinate(new Point(e.getX() - offsetX, e.getY() - offsetY));
            board.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
