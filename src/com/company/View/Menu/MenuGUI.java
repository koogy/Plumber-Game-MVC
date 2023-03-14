package com.company.View.Menu;

import com.company.Enums.MenuState;

import javax.swing.*;
import java.awt.*;

public class MenuGUI extends JPanel {
    private CardLayout cl;

    public MenuGUI() {
        this.cl = new CardLayout();
        this.setLayout(cl);

        MenuButtons menuButtons = new MenuButtons(this);
        LevelSelection levelSelection = new LevelSelection(this);
        LevelSelectionEdit levelSelectionEdit = new LevelSelectionEdit(this);
        this.add(menuButtons, MenuState.HOME.name());
        this.add(levelSelection, MenuState.LEVEL.name());
        this.add(levelSelectionEdit, MenuState.EDIT.name());
        cl.show(this, MenuState.HOME.name());
    }


    public CardLayout getCl() {
        return cl;
    }

    public void setCl(CardLayout cl) {
        this.cl = cl;
    }
}
