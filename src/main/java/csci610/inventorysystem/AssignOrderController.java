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
 * AssignOrderController will assign an order to a user based on input
 *
 * @author nicka
 */
public class AssignOrderController {

    //FXML controllers
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

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private Map<String, String> users = new HashMap<>();//map for loading all users from the database
    private Map<String, String> orders = new HashMap<>();//map for loading all the orders from the database

    /**
     * initialize will run on opening the assignOrder GUI, will initalize and
     * fill all of the tables in the assignorder GUI.
     */
    public void initialize() {

        //initialize the user table columns
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        displayNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //initialize the order table columns
        OrderIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        NotesCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        users = dbm.loadUsers();//get all of the users from the database

        ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());//create an observable array list

        userGrid.setItems(userList);//set the table with the observable array list

        orders = dbm.getAllOrders();//get all of the orders from the database

        ObservableList<Map.Entry<String, String>> orderList = FXCollections.observableArrayList(orders.entrySet());//create an observable array list

        ordersTable.setItems(orderList);//set the table with the observable array list

    }

    //two fields used to gather input from the user
    private String selectedToAssign = "";
    private String selectedOrder = "";

    /**
     * goback method will allow a user to return to the previous page
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void goBack(ActionEvent event) throws IOException {

        //create an alert that will prompt the user to choose between returning to the previous page or just canceling the action
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Cancel Action");
        a.setHeaderText("Are you sure you want to cancel?");

        //add two buttons, yes will cause the user to go back, no will simply close the alert
        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        //if yes, return to previous page
        if (result.isPresent() && result.get() == byes) {

            App.setRoot("Dashboard");

            //if no, just close the alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

    /**
     * getClickedUser is used to get the clicked on user from the users table
     *
     * @param event
     */
    @FXML
    private void getClickedUser(MouseEvent event) {

        //double click is required to select a user
        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedUser = userGrid.getSelectionModel().getSelectedItem();//get the clicked on item

            if (selectedUser != null) {

                selectedUserLabel.setText(selectedUser.getKey());//set the label so the user sees what is clicked on
                selectedToAssign = selectedUser.getKey();//then set the user variable

            }

        }

    }

    /**
     * AssignOrder will assign the specified user to the specified order and
     * write to DB with that data
     *
     * @param event
     */
    @FXML
    private void AssignOrder(ActionEvent event) {

        //first, check if a user and an order has been clicked on, if not throw an error
        if (selectedToAssign.equalsIgnoreCase("") || selectedOrder.equalsIgnoreCase("")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, no user or order specified, please choose a user and order to be assigned");
            a.showAndWait();
        } else {//otherwise go with the insert

            //insert the assigned user and selected order into the database, if it works throw success alert
            if (dbm.insertIntoOrdersPickedByUsers(selectedOrder, selectedToAssign)) {

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Order Assigned");
                a.showAndWait();

            } else {//otherwise throw an error saying there is an issue

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error assigning order, consult IT");
                a.showAndWait();
            }

        }

    }

    /**
     * getClickedOrder is used to get the clicked on order from the orders table
     * @param event
     */
    @FXML
    private void getClickedOrder(MouseEvent event) {

        //double click required to select an order
        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedOrder = ordersTable.getSelectionModel().getSelectedItem();//get the selected order from the table

            if (selectedOrder != null) {

                selectedOrderLabel.setText(selectedOrder.getKey());//set the label so the user sees what was clicked
                this.selectedOrder = selectedOrder.getKey();//set the variable used to insert into the DB

            }

        }

    }

}
