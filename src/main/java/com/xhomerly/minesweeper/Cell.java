package com.xhomerly.minesweeper;

import javafx.scene.control.Button;

public class Cell {
    private boolean hasMine;
    private Button cellButton;
    private boolean isFlagged = false;
    private boolean isUncovered = false;

    public Cell(Button cellButton) {
        this.cellButton = cellButton;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public void setMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public Button getCellButton() {
        return cellButton;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public boolean isUncovered() {
        return isUncovered;
    }

    public void setUncovered(boolean uncovered) {
        isUncovered = uncovered;
    }
}
