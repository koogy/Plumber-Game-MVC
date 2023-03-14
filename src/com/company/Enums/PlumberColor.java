package com.company.Enums;

public enum PlumberColor {
    NEUTRAL,
    RED {
        @Override
        public PlumberColor previous() {
            return values()[YELLOW.ordinal()];
        }
    },
    GREEN,
    BLUE,
    YELLOW {
        @Override
        public PlumberColor next() {
            return values()[1];
        }
    },
    GRAY;

    public int getRow() {
        return this.ordinal() + 1;
    }

    public PlumberColor next() {
        return values()[ordinal() + 1];
    }

    public PlumberColor previous() {
        return values()[ordinal() - 1];
    }
}
