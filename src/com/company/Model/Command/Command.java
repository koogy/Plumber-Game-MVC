package com.company.Model.Command;

import com.company.Model.Pipe;

public interface Command {

    boolean execute();

    boolean undo();

    boolean redo();

    Pipe getPipe();
}
