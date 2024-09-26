module com.xhomerly.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.xhomerly.minesweeper to javafx.fxml;
    exports com.xhomerly.minesweeper;
}