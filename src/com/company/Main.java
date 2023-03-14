package com.company;

import com.company.Controller.Controller;
import com.company.Controller.EditController;
import com.company.Enums.MenuState;
import com.company.Model.LevelEditor;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;
import com.company.View.EditorGUI;
import com.company.View.Menu.MenuGUI;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        MenuGUI menuGUI = new MenuGUI();
        JFrame frame = new JFrame("Plumber");

        frame.setPreferredSize(new Dimension(855,1000));
        frame.add(menuGUI);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);


    }

}
