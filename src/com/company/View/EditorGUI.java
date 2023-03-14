package com.company.View;

import com.company.Controller.EditController;
import com.company.Model.Command.Command;
import com.company.Model.Command.ResetBoardCommand;
import com.company.Model.LevelEditor;
import com.company.Model.Pipe;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EditorGUI extends BoardGUI {


    EditController controller;

    public EditorGUI(LevelEditor model, EditController controller) {
        super(model, controller);
        this.controller = controller;
        generatePieceEditMode();
        initResetButton();
        initToggleFixedButton();
        initBackMenu();
    }

    private void initBackMenu() {
        back_to_menu.addActionListener(e -> {

            if (model.isWinning()) {
                String filename = "src/com/company/levels/level" + model.getLevel() + ".p";
                File file = new File(filename);
                try {
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write(model.getRow() + " " + model.getCol());
                    bw.write("\n");

                    for (int i = 0; i < model.getRow(); i++) {
                        for (int j = 0; j < model.getCol(); j++) {
                            System.out.print(model.getBoard()[i][j].getPipe_name() + " ");
                            Pipe current_pipe = model.getBoard()[i][j];
                            if(current_pipe.isFixed()){
                                if(!current_pipe.getPipe_name().contains("*")){
                                    current_pipe.setPipe_name("*" + current_pipe.getPipe_name());
                                }
                            } else {
                                if(current_pipe.getPipe_name().contains("*")){
                                    current_pipe.setPipe_name(current_pipe.getPipe_name().substring(1));
                                }
                            }
                            bw.write(current_pipe.getPipe_name() + " ");
                        }
                        bw.write("\n");
                    }

                    bw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initToggleFixedButton() {
        JButton toggleFixedButton = new JButton("Fixed : OFF");
        super.setStyle(toggleFixedButton);
        toggleFixedButton.addActionListener(arg0 -> {
            controller.toggleFixed();
            if (controller.isToggleFix()) {
                toggleFixedButton.setText("Fixed : ON");
            } else {
                toggleFixedButton.setText("Fixed : OFF");
            }
        });

        this.add(toggleFixedButton);

    }


    private void initResetButton() {

        JButton resetBoardBtn = new JButton("Reset Board");
        super.setStyle(resetBoardBtn);
        resetBoardBtn.addActionListener(arg0 -> {
            Command command = new ResetBoardCommand(model, this);
            model.getCommandManager().addCommand(command);
            this.repaint();
        });

        this.add(resetBoardBtn);
    }

    public void generatePieceEditMode() {
        for (int i = 0; i < model.getRow(); i++) {
            for (int j = 0; j < model.getCol(); j++) {
                Pipe template_pipe = model.getBoard()[i][j];
                createPieceGUI(template_pipe, i, j);
            }
        }
        model.coloringBoard();
    }

    @Override
    public void drawSources(Graphics g, Pipe pipe, int row, int col) {
        /**/
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        g.drawString("EDIT MODE", 45, 25);

    }

    @Override
    public void popUpWinning(Graphics g) {

    }
}
