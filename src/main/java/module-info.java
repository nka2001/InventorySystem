module csci610.inventorysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens csci610.inventorysystem to javafx.fxml;
    exports csci610.inventorysystem;
}
