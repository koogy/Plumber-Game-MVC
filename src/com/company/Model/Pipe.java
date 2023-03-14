package com.company.Model;

import com.company.Enums.PipeType;
import com.company.Enums.PlumberColor;

public class Pipe {

    String pipe_name;
    PipeType pipe_type;
    PlumberColor pipe_color;

    PlumberColor under_color = PlumberColor.NEUTRAL;

    int rotation = 0;
    boolean fixed = false;

    /* TOP RIGHT BOT LEFT*/
    boolean[] orientation;


    /* CONSTRUCTOR */
    public Pipe(String pipe_name) {
        this.pipe_name = pipe_name;
        initPipe(pipe_name);
    }

    /* METHODS */

    public void initPipe(String pipe_name) {

        if (pipe_name.compareTo("X") == 0 || pipe_name.compareTo(".") == 0) {
            this.pipe_type = PipeType.EMPTY;
            this.pipe_color = PlumberColor.GRAY;
            return;
        }

        char[] pipe_data = pipe_name.toCharArray();
        int index = 0;
        if (pipe_data.length > 1) {

            if (pipe_data.length > 2) {
                if (pipe_data[0] == '*') {
                    this.fixed = true;
                    index++;

                }
            }

            this.pipe_type = getType(pipe_data[index]);
            this.pipe_color = getColor(pipe_data[index]);

            index++;

            this.rotation = pipe_data[index] - '0';

            this.orientation = pipe_type.getOrientation();
            rotate(rotation);
        }
    }

    public PlumberColor getColor(char t) {
        return switch (t) {
            case 'R' -> PlumberColor.RED;
            case 'G' -> PlumberColor.GREEN;
            case 'B' -> PlumberColor.BLUE;
            case 'Y' -> PlumberColor.YELLOW;
            default -> PlumberColor.NEUTRAL;
        };
    }

    public PipeType getType(char t) {
        return switch (t) {
            case 'L' -> PipeType.LINE;
            case 'O' -> PipeType.OVER;
            case 'T' -> PipeType.TURN;
            case 'F' -> PipeType.FORK;
            case 'C' -> PipeType.CROSS;
            case 'X' -> PipeType.EMPTY;
            default -> PipeType.SOURCE;
        };
    }

    public void rotate(int r) {
        for (int i = 0; i < r; i++) {
            boolean last = orientation[orientation.length - 1];
            System.arraycopy(orientation, 0, orientation, 1, orientation.length - 1);
            orientation[0] = last;
        }
    }


    /* GETTERS AND SETTERS */
    public String getPipe_name() {
        return pipe_name;
    }

    public void setPipe_name(String pipe_name) {
        this.pipe_name = pipe_name;
    }

    public PipeType getPipe_type() {
        return pipe_type;
    }

    public void setPipe_type(PipeType pipe_type) {
        this.pipe_type = pipe_type;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean[] getOrientation() {
        return orientation;
    }

    public void setOrientation(boolean[] orientation) {
        this.orientation = orientation;
    }

    public PlumberColor getPipe_color() {
        return pipe_color;
    }

    public void setPipe_color(PlumberColor pipe_color) {
        this.pipe_color = pipe_color;
    }

    public PlumberColor getPipeUnder_color() {
        return under_color;
    }

    public void setPipeUnder_color(PlumberColor pipe_color) {
        this.under_color = pipe_color;
    }
}
