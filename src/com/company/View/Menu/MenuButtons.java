package com.company.View.Menu;

import com.company.Enums.MenuState;

import javax.swing.*;
import java.awt.*;

public class MenuButtons extends JPanel {

    public MenuButtons(MenuGUI mainMenu) {
        this.setBackground(Color.BLACK);
        JButton levelsBtn = new JButton("Levels");
        JButton edit_levelsBtn = new JButton("Edit Levels");
        this.add(levelsBtn);
        this.add(edit_levelsBtn);

        setStyle(levelsBtn);
        setStyle(edit_levelsBtn);


        levelsBtn.addActionListener(arg0 -> {
            mainMenu.getCl().show(mainMenu, MenuState.LEVEL.name());
        });

        edit_levelsBtn.addActionListener(arg0 -> {
            mainMenu.getCl().show(mainMenu, MenuState.EDIT.name());
        });

        this.setLayout(new GridLayout(2, 1));

    }

    private void setStyle(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Times New Roman", Font.PLAIN, 30));
    }
}
