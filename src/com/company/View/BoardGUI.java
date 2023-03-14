package com.company.View;

import com.company.Controller.Controller;
import com.company.Enums.MenuState;
import com.company.Enums.PipeType;
import com.company.Enums.PlumberColor;
import com.company.Model.Pipe;
import com.company.Model.PlumberGame;
import com.company.View.Menu.MenuGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

public class BoardGUI extends JPanel {

    /* Inventory*/
    final int inventory_row = 6;
    final int inventory_col = 2;
    public PlumberGame model;
    public Controller controller;
    /* PIPE/CELL DIMENSION */
    int width = 120;
    int height = 120;
    /**/
    int targetWidth = 840;
    int targetHeight = 960;
    BufferedImage sprite;

    Pipe[][] template_inventory;
    Stack<PipeGUI> pipes;
    Stack<PipeGUI> sourcesPipes;
    Stack<PipeGUI> removedPipes;

    int startingHeight = 0;

    MenuGUI mainMenu;
    JButton back_to_menu;
    JButton undo;
    JButton redo;

    public BoardGUI(PlumberGame model, Controller controller) {
        this.model = model;
        this.controller = controller;
        this.setBackground(Color.black);

        pipes = new Stack<>();
        sourcesPipes = new Stack<>();
        removedPipes = new Stack<>();
        template_inventory = new Pipe[inventory_row][inventory_col];

        loadSprite();
        setupInventoryTemplate();
        loadInventory();
        setScalingRatio();
        initListeners();
        initButtons();

    }


    private void initListeners() {
        this.addMouseListener(controller);
        this.addMouseMotionListener(controller);
    }

    private void initButtons() {
        back_to_menu = new JButton("MENU");
        undo = new JButton("UNDO");
        redo = new JButton("REDO");

        setStyle(back_to_menu);
        setStyle(undo);
        setStyle(redo);

        this.add(back_to_menu);
        this.add(undo);
        this.add(redo);

        back_to_menu.addActionListener(e -> {
            JButton button = (JButton) e.getSource();
            JPanel buttonPanel = (JPanel) button.getParent();
            JPanel cardLayoutPanel = (JPanel) buttonPanel.getParent();
            CardLayout layout = (CardLayout) cardLayoutPanel.getLayout();
            layout.show(cardLayoutPanel, MenuState.HOME.name());

            model.displayBoard();
        });

        undo.addActionListener(e -> {
            if (model.getCommandManager().getCommandHistoryUndo().size() > 0) {
                model.getCommandManager().undo();
                BoardGUI.this.repaint();
            }
        });

        redo.addActionListener(e -> {

            if (model.getCommandManager().getCommandHistoryRedo().size() > 0) {
                model.getCommandManager().redo();
                BoardGUI.this.repaint();
            }
        });
    }

    private void setScalingRatio() {
        int maxCol = model.getCol() + inventory_col;
        int maxRow = Math.max(model.getRow(), inventory_row);
        float ratioW = (float) targetWidth / ((maxCol) * 120);
        float ratioH = (float) targetHeight / (maxRow * 120);

        height = (int) (120 * ratioH);
        width = (int) (120 * ratioW);
    }

    public void m_drawImageScale(Graphics g, BufferedImage image, int x, int y) {
        g.drawImage(image, x, y + startingHeight, width, height, null);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);


        popUpWinning(g);
        drawBoard(g);
        drawInventory(g);
        drawPieces(g);


    }


    public void popUpWinning(Graphics g) {
        if (model.isWinning()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int res = JOptionPane.showOptionDialog(null, "Level cleared !", "Plumber", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, null, null);

                    if (res == JOptionPane.OK_OPTION) {
                        CardLayout mainMenuLayout = (CardLayout) (mainMenu.getLayout());
                        mainMenuLayout.show(mainMenu, MenuState.LEVEL.name());
                    }
                }
            });
        }

    }

    public void drawBoard(Graphics g) {
        for (int i = 0; i < model.getRow(); i++) {
            for (int j = 0; j < model.getCol(); j++) {
                /*    Draw background*/
                if ((i > 0 && i < model.getRow() - 1 && j > 0 && j < model.getCol() - 1)) {
                    m_drawImageScale(g, getImage(7, 1), j * width, i * height);
                }

                /* Draw corners*/

                if (i == 0 && j == 0)
                    m_drawImageScale(g, rotate(getImage(7, 4), 0), 0, 0);
                if (i == model.getRow() - 1 && j == 0)
                    m_drawImageScale(g, rotate(getImage(7, 4), 270), 0, i * height);
                if (i == 0 && j == model.getCol() - 1) {
                    m_drawImageScale(g, rotate(getImage(7, 4), 90), j * width, 0);
                }

                if (i == model.getRow() - 1 && j == model.getCol() - 1) {
                    m_drawImageScale(g, rotate(getImage(7, 4), 180), j * width, i * height);
                }

                /*   Draw Border */
                if ((i == 0) && j != 0 && j != model.getCol() - 1) {
                    m_drawImageScale(g, rotate(getImage(7, 5), 0), j * width, 0);
                }
                if ((i == model.getRow() - 1) && j != 0 && j != model.getCol() - 1) {
                    m_drawImageScale(g, rotate(getImage(7, 5), 180), j * width, i * height);
                }
                if (j == 0 && i > 0 && i < model.getRow() - 1) {
                    m_drawImageScale(g, rotate(getImage(7, 5), 180 + 90), 0, i * height);
                }
                if (j == model.getCol() - 1 && i > 0 && i < model.getRow() - 1) {
                    m_drawImageScale(g, rotate(getImage(7, 5), 90), j * width, i * height);
                }

                if (model.getBoard()[i][j].isFixed()) {
                    drawFixedPipes(g, model.getBoard()[i][j], j * width, i * height);

                } else {
                    drawSources(g, model.getBoard()[i][j], j * width, i * height);
                }
            }
        }
    }

    public void drawInventory(Graphics g) {
        int startX = model.getCol() * width;
        int startY = startingHeight;

        HashMap<String, Integer> inventory = model.getInventory();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                BufferedImage background = getImage(7, 1);

                m_drawImageScale(g, background, startX + j * width, startY + i * height);


                String current_pipe_name = template_inventory[i][j].getPipe_name();

                int n = model.getInventory().getOrDefault(template_inventory[i][j].getPipe_name(), 0);

                if (n > 100) {
                    g.drawString("∞", startX + j * width + 5, startY + i * height + height - 5);

                } else {
                    g.drawString(Integer.toString(n), startX + j * width + 5, startY + i * height + height - 5);

                }

                m_drawImageScale(g, getPipeImage(PlumberColor.GRAY, current_pipe_name), startX + j * width, startY + i * height);

                if (inventory.containsKey(current_pipe_name) && inventory.get(current_pipe_name) > 0) {
                    m_drawImageScale(g, getPipeImage(PlumberColor.NEUTRAL, current_pipe_name), startX + j * width, startY + i * height);
                }
            }
        }
    }

    public void drawPieces(Graphics g) {
        for (PipeGUI p : pipes) {
            BufferedImage current_pipe_image = getImage(p.getPipe().getPipe_color().getRow(), p.getPipe().getPipe_type().getCol());
            if (p.getPipe().getPipe_type() == PipeType.OVER) {
                current_pipe_image = combineImage(getImage(p.getPipe().getPipeUnder_color().getRow(), PipeType.LINE.getCol()), current_pipe_image);
            }
            current_pipe_image = p.rotate(current_pipe_image, p.getPipe().getRotation() * 90);
            p.setImage(current_pipe_image);
            m_drawImageScale(g, p.getImage(), (int) p.getCoordinate().getX(), (int) p.getCoordinate().getY());
        }
    }


    public void drawSources(Graphics g, Pipe pipe, int row, int col) {
        if (pipe.getPipe_type() == PipeType.SOURCE) {
            BufferedImage current_image = rotate(getImage(pipe.getPipe_color().getRow(), pipe.getPipe_type().getCol()), pipe.getRotation() * 90);
            m_drawImageScale(g, current_image, row, col);
        }
    }

    public void drawFixedPipes(Graphics g, Pipe pipe, int row, int col) {
        BufferedImage current_pipeGUI;
        if (pipe.getPipe_type() == PipeType.OVER) {
            current_pipeGUI = getPipeOverImage(pipe.getPipe_color(), pipe.getPipeUnder_color());
        } else {
            current_pipeGUI = getPipeImage(pipe.getPipe_color(), pipe.getPipe_name());
        }
        BufferedImage fixed = getImage(7, 6);
        BufferedImage fixed_pipe = combineImage(fixed, current_pipeGUI);
        m_drawImageScale(g, fixed_pipe, row, col);
    }


    public boolean generatePiece(int row, int col) {
        Pipe template_pipe = template_inventory[row][col - model.getCol()];
        String current_pipe_name = template_pipe.getPipe_name();
        HashMap<String, Integer> inventory = model.getInventory();

        if (inventory.containsKey(current_pipe_name) && inventory.get(current_pipe_name) > 0) {
            Point coordinate = convertIndicesToPosition(getDefaultPipeInventoryCoordinate(template_pipe.getPipe_name()));
            Pipe current_pipe = new Pipe(template_inventory[row][col - model.getCol()].getPipe_name());
            BufferedImage image;
            image = getPipeImage(PlumberColor.NEUTRAL, current_pipe.getPipe_name());
            PipeGUI pipeGUI = new PipeGUI(current_pipe);
            pipeGUI.setImage(image);
            pipeGUI.setCoordinate(coordinate);
            pipes.add(pipeGUI);
            return true;
        }
        return false;
    }

    public void createPieceGUI(Pipe pipe, int row, int col) {
        String current_pipe_name = pipe.getPipe_name();
        HashMap<String, Integer> inventory = model.getInventory();
        if (inventory.containsKey(current_pipe_name) && inventory.get(current_pipe_name) > 0) {
            Point coordinate = convertIndicesToPositionBoard(new Point(col, row));
            BufferedImage image;
            image = getPipeImage(PlumberColor.NEUTRAL, pipe.getPipe_name());
            PipeGUI pipeGUI = new PipeGUI(pipe);
            pipeGUI.setImage(image);
            pipeGUI.setCoordinate(coordinate);
            pipes.add(pipeGUI);
        }
    }

    public void removePipe(Pipe pipe) {
        int index = 0;
        for (PipeGUI pipeGUI : pipes) {
            if (pipeGUI.getPipe() == pipe) {

                removedPipes.push(pipeGUI);
                pipes.remove(index);
                return;
            }
            index++;
        }
    }

    public void placePipe(Pipe pipe) {
        for (PipeGUI pipeGUI : removedPipes) {
            if (pipeGUI.getPipe() == pipe) {
                pipes.push(pipeGUI);
                int row = (int) Math.round(pipeGUI.getCoordinate().getY() / getPipeHeight());
                int col = (int) Math.round(pipeGUI.getCoordinate().getX() / getPipeWidth());
                System.out.println("Placing pipe at : " + row + " " + col);
                model.getBoard()[row][col] = pipe;
                break;
            }
        }
    }

    /* LOADING DATA*/
    public void loadInventory() {
        HashMap<String, Integer> inventory = model.getInventory();
        inventory.forEach((id, name) -> {
            Pipe current_pipe = new Pipe(id);
            BufferedImage image = getImage(PlumberColor.NEUTRAL.getRow(), current_pipe.getPipe_type().getCol());
            PipeGUI pipeGUI;
            if (id.compareTo("O0") == 0 || id.compareTo("*O0") == 0) {
                BufferedImage c = combineImage(getImage(PlumberColor.NEUTRAL.getRow(), PipeType.LINE.getCol()), image);
                pipeGUI = new PipeGUI(current_pipe, c);
            } else {
                pipeGUI = new PipeGUI(current_pipe, image);
            }
            pipeGUI.setCoordinate(convertIndicesToPosition(getDefaultPipeInventoryCoordinate(id)));
        });
    }

    public void loadSprite() {
        try {
            sprite = ImageIO.read(new File("src/com/company/resources/pipes.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* HELPERS */
    public BufferedImage rotate(BufferedImage image, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = image.getWidth();
        int h = image.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public void setupInventoryTemplate() {
        template_inventory[0][0] = new Pipe("C0");
        template_inventory[0][1] = new Pipe("O0");

        template_inventory[1][0] = new Pipe("L0");
        template_inventory[1][1] = new Pipe("L1");

        template_inventory[2][0] = new Pipe("T1");
        template_inventory[2][1] = new Pipe("T2");

        template_inventory[3][0] = new Pipe("T0");
        template_inventory[3][1] = new Pipe("T3");

        template_inventory[4][0] = new Pipe("F0");
        template_inventory[4][1] = new Pipe("F1");

        template_inventory[5][0] = new Pipe("F3");
        template_inventory[5][1] = new Pipe("F2");
    }

    public BufferedImage getPipeImage(PlumberColor color, String name) {

        if (name.charAt(0) == '*') {
            name = name.substring(1);
        }

        return switch (name) {
            case "C0" -> rotate(getImage(color.getRow(), template_inventory[0][0].getPipe_type().getCol()), template_inventory[0][0].getRotation() * 90);
            case "O0" -> combineImage(getImage(color.getRow(), PipeType.LINE.getCol()), rotate(getImage(color.getRow(), template_inventory[0][1].getPipe_type().getCol()), template_inventory[0][1].getRotation() * 90));
            case "L0" -> rotate(getImage(color.getRow(), template_inventory[1][0].getPipe_type().getCol()), template_inventory[1][0].getRotation() * 90);
            case "L1" -> rotate(getImage(color.getRow(), template_inventory[1][1].getPipe_type().getCol()), template_inventory[1][1].getRotation() * 90);
            case "T1" -> rotate(getImage(color.getRow(), template_inventory[2][0].getPipe_type().getCol()), template_inventory[2][0].getRotation() * 90);
            case "T2" -> rotate(getImage(color.getRow(), template_inventory[2][1].getPipe_type().getCol()), template_inventory[2][1].getRotation() * 90);
            case "T0" -> rotate(getImage(color.getRow(), template_inventory[3][0].getPipe_type().getCol()), template_inventory[3][0].getRotation() * 90);
            case "T3" -> rotate(getImage(color.getRow(), template_inventory[3][1].getPipe_type().getCol()), template_inventory[3][1].getRotation() * 90);
            case "F0" -> rotate(getImage(color.getRow(), template_inventory[4][0].getPipe_type().getCol()), template_inventory[4][0].getRotation() * 90);
            case "F1" -> rotate(getImage(color.getRow(), template_inventory[4][1].getPipe_type().getCol()), template_inventory[4][1].getRotation() * 90);
            case "F3" -> rotate(getImage(color.getRow(), template_inventory[5][0].getPipe_type().getCol()), template_inventory[5][0].getRotation() * 90);
            case "F2" -> rotate(getImage(color.getRow(), template_inventory[5][1].getPipe_type().getCol()), template_inventory[5][1].getRotation() * 90);
            default -> null;
        };

    }

    public BufferedImage getPipeOverImage(PlumberColor color, PlumberColor underColor) {
        return combineImage(getImage(underColor.getRow(), PipeType.LINE.getCol()), rotate(getImage(color.getRow(), template_inventory[0][1].getPipe_type().getCol()), template_inventory[0][1].getRotation() * 90));
    }

    public Point getDefaultPipeInventoryCoordinate(String pipe) {
        return switch (pipe) {
            /* COLONNE / LIGNE */
            case "C0" -> new Point(0, 0);
            case "O0" -> new Point(1, 0);

            case "L0" -> new Point(0, 1);
            case "L1" -> new Point(1, 1);

            case "T1" -> new Point(0, 2);
            case "T2" -> new Point(1, 2);

            case "T0" -> new Point(0, 3);
            case "T3" -> new Point(1, 3);

            case "F0" -> new Point(0, 4);
            case "F1" -> new Point(1, 4);

            case "F3" -> new Point(0, 5);
            case "F2" -> new Point(1, 5);

            default -> new Point(0, 0);
        };

    }

    public BufferedImage combineImage(BufferedImage image_one, BufferedImage image_two) {
        BufferedImage c = new BufferedImage(image_one.getWidth(), image_one.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = c.getGraphics();
        g.drawImage(image_one, 0, 0, null);
        g.drawImage(image_two, 0, 0, null);
        g.dispose();
        return c;
    }

    public Point convertIndicesToPosition(Point coordinate) {

        int x = (int) coordinate.getX(); /* C EST LA COLONNE*/
        int y = (int) coordinate.getY(); /* C EST LA LIGNE*/

        int startX = model.getCol() * width;
        return new Point(startX + x * width, y * height + startingHeight);
    }

    public Point convertIndicesToPositionBoard(Point coordinate) {

        int x = (int) coordinate.getX(); /* C EST LA COLONNE*/
        int y = (int) coordinate.getY(); /* C EST LA LIGNE*/
        return new Point(x * width, y * height);
    }


    public void setStyle(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    }

    /* GETTERS AND SETTERS */
    public void setModel(PlumberGame game) {
        this.model = game;
    }

    public Stack<PipeGUI> getPipes() {
        return pipes;
    }

    public void setPipes(Stack<PipeGUI> pipes) {
        this.pipes = (Stack<PipeGUI>) pipes.clone();
    }

    public BufferedImage getImage(int row, int col) {
        return sprite.getSubimage((col * 140) - 140, (row * 140) - 140, 120, 120);
    }

    public Pipe[][] getTemplate_inventory() {
        return template_inventory;
    }

    public void setTemplate_inventory(Pipe[][] template_inventory) {
        this.template_inventory = template_inventory;
    }

    public int getPipeWidth() {
        return width;
    }

    public int getPipeHeight() {
        return height;
    }


    public void set(MenuGUI mainMenu) {
        this.mainMenu = mainMenu;
    }
}
