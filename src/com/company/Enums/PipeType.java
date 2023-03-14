package com.company.Enums;

public enum PipeType {
    SOURCE {
        @Override
        public boolean[] getOrientation() {
            return new boolean[]{true, false, false, false};
        }
    }, LINE {
        @Override
        public boolean[] getOrientation() {
            return new boolean[]{true, false, true, false};
        }
    }, OVER {
        @Override
        public boolean[] getOrientation() {
            return new boolean[]{true, true, true, true};
        }
    }, TURN {
        @Override
        public boolean[] getOrientation() {
            return new boolean[]{true, true, false, false};
        }
    }, FORK {
        @Override
        public boolean[] getOrientation() {
            return new boolean[]{true, true, true, false};
        }
    }, CROSS {
        @Override
        public boolean[] getOrientation() {
            return new boolean[]{true, true, true, true};
        }
    }, EMPTY {
        @Override
        public boolean[] getOrientation() {
            return new boolean[]{false, false, false, false};
        }
    };

    public abstract boolean[] getOrientation();

    public int getCol() {
        return this.ordinal() + 1;
    }
}