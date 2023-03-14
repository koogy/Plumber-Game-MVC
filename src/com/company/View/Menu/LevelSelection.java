package com.company.View.Menu;

import com.company.Controller.Controller;
import com.company.Enums.MenuState;
import com.company.Model.PlumberGame;
import com.company.View.BoardGUI;

import javax.swing.*;
import java.awt.*;

public class LevelSelection extends JPanel {

    public LevelSelection(MenuGUI mainMenu) {

        JButton[] level = new JButton[6];
        JButton backButton = new JButton("BACK");

        this.add(backButton);
        setStyle(backButton);

        for (int i = 0; i < 6; i++) {
            level[i] = new JButton(Integer.toString(i + 1));
            setStyle(level[i]);
            int currentLevel = i;
            level[i].addActionListener(arg0 -> {
                PlumberGame game = new PlumberGame(currentLevel + 1);
                game.resetLevel();
                Controller controller = new Controller(game);
                BoardGUI view = new BoardGUI(game, controller);
                view.set(mainMenu);
                controller.setView(view);
                mainMenu.add(view, MenuState.GAME.name());
                CardLayout mainMenuLayout = (CardLayout) (mainMenu.getLayout());
                mainMenuLayout.show(mainMenu, MenuState.GAME.name());


            });
            this.add(level[i]);
        }

        backButton.addActionListener(arg0 -> {
            JButton button = (JButton) arg0.getSource();
            JPanel buttonPanel = (JPanel) button.getParent();
            JPanel cardLayoutPanel = (JPanel) buttonPanel.getParent();
            CardLayout layout = (CardLayout) cardLayoutPanel.getLayout();
            layout.show(mainMenu, MenuState.HOME.name());

        });

        this.setLayout(new GridLayout(3, 2));
        this.setBackground(Color.BLACK);

    }

    private void setStyle(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Times New Roman", Font.PLAIN, 30));
    }
}
