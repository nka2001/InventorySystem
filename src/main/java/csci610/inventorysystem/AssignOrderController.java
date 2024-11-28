package csci610.inventorysystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author nicka
 */
public class AssignOrderController {

    @FXML
    private Button backButton;
    @FXML
    private TableView<Map.Entry<String, String>> userGrid;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> usernameColumn;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> displayNameColumn;
    @FXML
    private Button AssignButton;
    @FXML
    private Label selectedUserLabel;
    @FXML
    private TableView<Map.Entry<String, String>> ordersTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> OrderIDCol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> NotesCol;
    @FXML
    private Label selectedOrderLabel;

    private DatabaseManager dbm = new DatabaseManager();

    private Map<String, String> users = new HashMap<>();
    private Map<String, String> orders = new HashMap<>();

    public void initialize() {

        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        displayNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        OrderIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        NotesCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        users = dbm.loadUsers();

        ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

        userGrid.setItems(userList);

        orders = dbm.getAllOrders();

        ObservableList<Map.Entry<String, String>> orderList = FXCollections.observableArrayList(orders.entrySet());

        ordersTable.setItems(orderList);

    }

    private String selectedToAssign = "";
    private String selectedOrder = "";

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Cancel Action");
        a.setHeaderText("Are you sure you want to cancel?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            App.setRoot("OrderManager");

        } else if (result.get() == bno) {

            a.close();

        }

        
        
    }

    @FXML
    private void getClickedUser(MouseEvent event) {

        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedUser = userGrid.getSelectionModel().getSelectedItem();

            if (selectedUser != null) {

                selectedUserLabel.setText(selectedUser.getKey());
                selectedToAssign = selectedUser.getKey();

            }

        }

    }

    @FXML
    private void AssignOrder(ActionEvent event) {

        if (selectedToAssign.equalsIgnoreCase("") || selectedOrder.equalsIgnoreCase("")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, no user or order specified, please choose a user and order to be assigned");
            a.showAndWait();
        } else {

            if (dbm.insertIntoOrdersPickedByUsers(selectedOrder, selectedToAssign)) {

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Order Assigned");
                a.showAndWait();

            } else {

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error assigning order, consult IT");
                a.showAndWait();
            }

        }

    }

    @FXML
    private void getClickedOrder(MouseEvent event) {

        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

            if (selectedOrder != null) {

                selectedOrderLabel.setText(selectedOrder.getKey());
                this.selectedOrder = selectedOrder.getKey();

            }

        }

    }

}
