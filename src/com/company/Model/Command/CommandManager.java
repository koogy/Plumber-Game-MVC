package com.company.Model.Command;

import java.util.LinkedList;

public class CommandManager {

    LinkedList<Command> commandHistoryUndo;
    LinkedList<Command> commandHistoryRedo;

    public CommandManager() {
        commandHistoryUndo = new LinkedList<>();
        commandHistoryRedo = new LinkedList<>();
    }

    public boolean addCommand(Command command) {

        boolean result = command.execute();
        /*       System.out.println("hha");*/
        if (!result) {
            /*       System.out.println("ohoho");*/
            return false;
        }
        commandHistoryUndo.push(command);


        if (commandHistoryUndo.size() > commandHistoryRedo.size()) {
            commandHistoryRedo.clear();
        }

        return result;
    }

    public void undo() {

        if (commandHistoryUndo.size() <= 0) {
            return;
        }
        commandHistoryUndo.peek().undo();
        commandHistoryRedo.push(commandHistoryUndo.peek());
        commandHistoryUndo.pop();
    }

    public void redo() {
        if (commandHistoryRedo.size() <= 0) {
            return;
        }
        commandHistoryRedo.peek().redo();          // redo most recently executed command
        commandHistoryUndo.push(commandHistoryRedo.peek()); // add undone command to redo stack
        commandHistoryRedo.pop();                  // remove top entry from redo stack
    }

    public LinkedList<Command> getCommandHistoryUndo() {
        return commandHistoryUndo;
    }

    public void setCommandHistoryUndo(LinkedList<Command> commandHistory) {
        this.commandHistoryUndo = commandHistory;
    }

    public LinkedList<Command> getCommandHistoryRedo() {
        return commandHistoryRedo;
    }

    public void setCommandHistoryRedo(LinkedList<Command> commandHistoryRedo) {
        this.commandHistoryRedo = commandHistoryRedo;
    }
}
