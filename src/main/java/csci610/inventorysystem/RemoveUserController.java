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
 * removeusercontroller will remove a user from the users table and other tables
 * in the database
 *
 * @author nicka
 */
public class RemoveUserController {

    //FXML controls
    @FXML
    private Button backButton;
    @FXML
    private TableView<Map.Entry<String, String>> userGrid;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> usernameColumn;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> displayNameColumn;
    @FXML
    private Button RemoveButton;
    @FXML
    private Label selectedUserLabel;

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private Map<String, String> users = new HashMap<>();//map for holding all users

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
     * initialize will run upon opening the removeuser GUI, it will fill the users table with all the users in the database for selection
     */
    public void initialize() {

        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        displayNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        users = dbm.loadUsers();//load the users from the database

        ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

        userGrid.setItems(userList);//add it to the tableview

    }

    private String selectedToRemove;//selected to remove will hold the clicked user to be removed

    /**
     * remove will remove the user from the database if possible
     * @param event 
     */
    @FXML
    private void Remove(ActionEvent event) {

        //throw a confirmation alert prompting the user to confirm if they want to proceed with removal
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Remove Action");
        a.setHeaderText("Are you sure you want to remove this user?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        //if yes, remove the user
        if (result.isPresent() && result.get() == byes) {

            //if the removal is good, then throw an alert
            if (dbm.removeUser(selectedToRemove)) {

                Alert removed = new Alert(Alert.AlertType.INFORMATION);
                removed.setTitle("Success");
                removed.setHeaderText("User removed successfully");
                removed.showAndWait();

                //then refresh the alert
                users = dbm.loadUsers();

                ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

                userGrid.setItems(userList);

            }
            //if no, just close the alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

    /**
     * getClickedUser will get the clicked user and store it for use later 
     * @param event 
     */
    @FXML
    private void getClickedUser(MouseEvent event) {

        //double click used to select a user
        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedUser = userGrid.getSelectionModel().getSelectedItem();//get the selected user

            if (selectedUser != null) {

                selectedUserLabel.setText(selectedUser.getKey());//set the label to show the user who was selected
                selectedToRemove = selectedUser.getKey();//then set the remove value for later deletion

            }

        }

    }

}
