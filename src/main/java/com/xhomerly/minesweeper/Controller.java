package com.xhomerly.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.Objects;

public class Controller {
    @FXML private GridPane grid; // The grid for placing cells
    @FXML private Label timeBox; // Label to display elapsed time
    @FXML private Label mineBox; // Label to display remaining mines
    @FXML private Label endLabel; // Label for game end messages
    @FXML private AnchorPane endDialog;  // Pane to display the game over dialog

    private boolean hasGameStarted = false;
    private int time = 0;
    private byte mineCount = 0;
    private boolean gameOver = false;

    public Cell[][] cellArray = new Cell[10][10];

    public Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            time++;
            timeBox.setText("" + time);
        }
    }));

    public void initialize() {
        for (byte x = 0; x < grid.getRowCount(); x++) {
            for (byte y = 0; y < grid.getColumnCount(); y++) {
                byte random = (Math.random() > 0.125) ? (byte) 0 : (byte) 1; // Randomly assign mines (12.5% probability)
                boolean hasMine = random == 1;
                if (hasMine) mineCount++;
                Button button = new Button();
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button.getStyleClass().add("button");
                cellArray[x][y] = new Cell(hasMine, button);
                grid.add(button, x, y);

                // Store x and y for the mouse click handler
                byte xx = x;
                byte yy = y;

                cellArray[x][y].getCellButton().setOnMouseClicked(event -> {
                    if (!gameOver) {
                        if (!hasGameStarted && event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                            hasGameStarted = true;
                            startTimer();
                        }
                        if (event.getButton() == MouseButton.PRIMARY && cellArray[xx][yy].hasMine() && !cellArray[xx][yy].isFlagged() && !cellArray[xx][yy].isUncovered()) {
                            endGame(xx, yy); // Game over if the cell has a mine
                        } else if (event.getButton() == MouseButton.PRIMARY && !cellArray[xx][yy].hasMine() && !cellArray[xx][yy].isFlagged() && !cellArray[xx][yy].isUncovered()) {
                            uncover(xx, yy); // Uncover the cell if it doesn't have a mine
                            if (isAllUncovered()) { // Check if all non-mine cells are uncovered
                                timeline.stop();
                                endLabel.setText("YOU WON!");
                                endDialog.setVisible(true);
                            }
                        } else if (event.getButton() == MouseButton.SECONDARY && !cellArray[xx][yy].isFlagged() && !cellArray[xx][yy].isUncovered()) {
                            flag(xx, yy, cellArray[xx][yy].isFlagged()); // Place a flag
                            if (isAllUncovered()) { // Check if all non-mine cells are uncovered
                                timeline.stop();
                                endLabel.setText("YOU WON!");
                                endDialog.setVisible(true);
                            }
                        } else if (event.getButton() == MouseButton.SECONDARY && cellArray[xx][yy].isFlagged() && !cellArray[xx][yy].isUncovered()) {
                            flag(xx, yy, cellArray[xx][yy].isFlagged()); // Remove a flag
                        }
                    }
                });
            }
        }

        mineBox.setText("" + mineCount);
    }

    public void startTimer() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Method to count neighboring mines for a given cell
    public byte checkMinesCount(byte x, byte y) {
        byte mineCount = 0;

        for (byte row = -1; row <= 1; row++) {
            for (byte col = -1; col <= 1; col++) {
                byte neighborX = (byte) (x + row);
                byte neighborY = (byte) (y + col);

                // Ensure neighbors are within bounds and aren't the current cell
                if (neighborX >= 0 && neighborY >= 0 && neighborX <= 9 && neighborY <= 9) {
                    if (neighborX != x || neighborY != y) {
                        if (cellArray[neighborX][neighborY].hasMine()) {
                            mineCount++;
                        }
                    }
                }
            }
        }

        return mineCount;
    }

    // Method to uncover a cell and display the number of neighboring mines
    public void uncover(byte x, byte y) {
        byte mineCount = checkMinesCount(x, y);
        cellArray[x][y].getCellButton().setText("" + mineCount);
        cellArray[x][y].getCellButton().getStyleClass().add("numberOfMines");

        // Add specific colors based on the number of neighboring mines
        if (mineCount == 0) cellArray[x][y].getCellButton().getStyleClass().add("zero");
        if (mineCount == 1) cellArray[x][y].getCellButton().getStyleClass().add("one");
        if (mineCount == 2) cellArray[x][y].getCellButton().getStyleClass().add("two");
        if (mineCount == 3) cellArray[x][y].getCellButton().getStyleClass().add("three");
        if (mineCount == 4) cellArray[x][y].getCellButton().getStyleClass().add("four");
        if (mineCount == 5) cellArray[x][y].getCellButton().getStyleClass().add("five");
        if (mineCount == 6) cellArray[x][y].getCellButton().getStyleClass().add("six");
        if (mineCount == 7) cellArray[x][y].getCellButton().getStyleClass().add("seven");
        if (mineCount == 8) cellArray[x][y].getCellButton().getStyleClass().add("eight");
        cellArray[x][y].setUncovered(true);
    }

    // Method to place or remove a flag on a cell
    public void flag(byte x, byte y, boolean isFlagged) {
        if (isFlagged) {
            cellArray[x][y].getCellButton().setGraphic(null);
            cellArray[x][y].setFlagged(false);
            mineCount++;
            mineBox.setText("" + mineCount);
        } else {
            ImageView flag = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("flag.png"))));
            flag.setPreserveRatio(true);
            flag.setFitHeight(30);
            flag.setFitWidth(30);
            cellArray[x][y].getCellButton().setGraphic(flag);
            cellArray[x][y].setFlagged(true);
            mineCount--;
            mineBox.setText("" + mineCount);
        }
    }

    // Method to end the game when a mine is uncovered
    public void endGame(byte x, byte y) {
        cellArray[x][y].setUncovered(true);
        timeline.stop();
        ImageView bum = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("bomb.png"))));
        bum.setPreserveRatio(true);
        bum.setFitHeight(30);
        bum.setFitWidth(30);
        cellArray[x][y].getCellButton().setGraphic(bum);
        cellArray[x][y].getCellButton().setStyle("-fx-background-color: red");
        gameOver = true;

        // Reveal all mines and mark flagged/unflagged cells accordingly to player's actions (correctly flagged, incorrectly flagged, or not flagged)
        for (byte xx = 0; xx < grid.getRowCount(); xx++) {
            for (byte yy = 0; yy < grid.getColumnCount(); yy++) {
                if (cellArray[xx][yy].hasMine() && cellArray[xx][yy].isFlagged()) {
                    cellArray[xx][yy].getCellButton().setStyle("-fx-background-color: green");
                } else if (!cellArray[xx][yy].hasMine() && cellArray[xx][yy].isFlagged()) {
                    cellArray[xx][yy].getCellButton().setStyle("-fx-background-color: #a50000");
                } else if (cellArray[xx][yy].hasMine()) {
                    ImageView bumbum = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("bomb.png"))));
                    bum.setPreserveRatio(true);
                    bum.setFitHeight(30);
                    bum.setFitWidth(30);
                    cellArray[xx][yy].getCellButton().setGraphic(bumbum);
                }
            }
        }

        endLabel.setText("YOU DIED");
        endDialog.setVisible(true);
    }

    public void exitApp() {
        Platform.exit();
        System.exit(0);
    }

    public void restartGame() {
        endDialog.setVisible(false);

        hasGameStarted = false;
        time = 0;
        mineCount = 0;
        gameOver = false;
        timeBox.setText("0");

        cellArray = new Cell[10][10];

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                time++;
                timeBox.setText("" + time);
            }
        }));

        initialize();
    }

    // Method to check if all non-mine cells are uncovered, indicating a win
    public boolean isAllUncovered() {
        for (byte x = 0; x < grid.getRowCount(); x++) {
            for (byte y = 0; y < grid.getColumnCount(); y++) {
                if (!cellArray[x][y].isUncovered() && !cellArray[x][y].hasMine()) {
                    return false;
                } else if (!cellArray[x][y].isUncovered() && cellArray[x][y].hasMine() && !cellArray[x][y].isFlagged()) {
                    return false;
                }
            }
        }
        return true;
    }
}