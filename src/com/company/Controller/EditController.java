package com.company.Controller;

import com.company.Enums.PipeType;
import com.company.Model.Command.*;
import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.PipeGUI;

import java.awt.*;
import java.awt.event.MouseEvent;

public class EditController extends Controller {
    private Pipe[][] editBoard = model.getBoard();
    private boolean toggleFix = false;

    public EditController(PlumberGame model) {
        super(model);
    }

    public boolean isMouseOverSource(int px, int py) {
        return editBoard[py][px].getPipe_type() == PipeType.SOURCE;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int px = x / board.getPipeWidth();
        int py = y / board.getPipeHeight();

        model.displayBoard();

        if (isMouseOnBorder(px, py)) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (!isMouseOverSource(px, py)) {
                    createSource(px, py);
                } else {
                    Pipe pipe = model.getBoard()[py][px];
                    Command command = new ChangeColorCommand(model, board, pipe, py, px);
                    model.getCommandManager().addCommand(command);
                }
            } else {
                Pipe source = model.getBoard()[py][px];
                Command command = new RemoveSourceCommand(model, board, currentPipeDragged, py, px);
                model.getCommandManager().addCommand(command);
            }
        } else {
            if (toggleFix) {
                for (PipeGUI p : board.getPipes()) {
                    if (isMouseOverPipe(p, x, y)) {
                        p.getPipe().setFixed(!p.getPipe().isFixed());
                    }
                }
            }
        }

        board.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        boolean pipePlaced = false;

        if (currentPipeDragged != null) {
            if (currentPipeDragged.getPipe().getPipe_type() != PipeType.SOURCE) {
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
                            /* board.getPipes().pop();*/
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
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }


    private void createSource(int px, int py) {
        String s = "";

        boolean placed = true;
        /*left*/
        if (px == 0 && py != 0 && py != (model.getRow() - 1)) {
            s = "R1";
        }
        /*right*/
        else if (px == model.getCol() - 1 && py != 0 && py != (model.getRow() - 1)) {
            s = "R3";
        }
        /*top*/
        else if (py == 0 && px != 0 && px != model.getCol() - 1) {
            s = "R2";
        } else if (py == model.getRow() - 1 && px != 0 && px != model.getCol() - 1) {
            s = "R0";
        } else {
            placed = false;
        }

        if (placed) {
            Pipe source = new Pipe(s);
            Command command = new PlaceSourceCommand(model, board, source, py, px);
            model.getCommandManager().addCommand(command);
        }
    }

    public boolean isToggleFix() {
        return toggleFix;
    }

    public void toggleFixed() {
        this.toggleFix = !toggleFix;
    }
}
