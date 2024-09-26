package com.xhomerly.minesweeper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Minesweeper");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        Font.loadFont(getClass().getResourceAsStream("pixel-font.ttf"), 12);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("bomb.png"))));
    }

    public static void main(String[] args) {
        launch();
    }
}