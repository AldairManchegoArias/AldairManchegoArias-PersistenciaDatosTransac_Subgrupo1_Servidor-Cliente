module org.example.stockedserverclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.stockedserverclient to javafx.fxml;
    exports org.example.stockedserverclient;
}