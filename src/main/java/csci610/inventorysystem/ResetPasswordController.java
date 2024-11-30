
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * resetPasswordController controls the reset password GUI, will allow an administrator to reset a password for a user
 * @author nicka
 */
public class ResetPasswordController {

    //FXML controls
    @FXML
    private TableView<Map.Entry<String, String>> userGrid;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> usernameColumn;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> displayNameColumn;
    @FXML
    private TextField enterPassword;
    @FXML
    private TextField confirmPassword;
    @FXML
    private Label selectedUserLabel;

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private Map<String, String> users = new HashMap<>();//map containing all of the users
    @FXML
    private Button backButton;
    @FXML
    private Button submitButton;
    @FXML
    private CheckBox disabledUser;
    @FXML
    private CheckBox makeAdministrator;

    /**
     * initialize will run when opening the reset password GUI, it will fill the tableview with all the users in the database
     */
    public void initialize() {

        //initialize the all users table view
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        displayNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //then retrieve and set the users to the tableview
        users = dbm.loadUsers();

        ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

        userGrid.setItems(userList);

    }

    private String selectedToReset;//holds the user who needs a password reset

    /**
     * getClickedUser will allow a user to select a user to reset their password
     * @param event 
     */
    @FXML
    private void getClickedUser(MouseEvent event) {

        //double click required for selection
        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedUser = userGrid.getSelectionModel().getSelectedItem();//get the selected user

            
            if (selectedUser != null) {

                selectedUserLabel.setText(selectedUser.getKey());//show the selected user to the user
                selectedToReset = selectedUser.getKey();//set the user to be reset
                
                //used to get permissions, if the user is admin, the administrator checkbox will be set
                if (dbm.getIsAdmin(selectedToReset)) {

                    makeAdministrator.setSelected(true);
                    
                } else {//false otherwise

                    makeAdministrator.setSelected(false);

                }
                //used to get if the account is disabled, true if the user is disabled, false otherwise
                if (dbm.getIsDisabled(selectedToReset)) {

                    disabledUser.setSelected(true);

                } else {

                    disabledUser.setSelected(false);

                }

                //reset both fields 
                enterPassword.clear();
                confirmPassword.clear();

            }

        }

    }

    /**
     * goback method will allow a user to return to the previous page
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

    //used to set permissions
    private boolean isDisabledSelected;
    private boolean isAdminSelected;

    
    /**
     * submit will cause the password to be reset
     * @param event 
     */
    @FXML
    private void submit(ActionEvent event) {

        String newPassword;//new password for resetting

        //check permissions
        isDisabledSelected = disabledUser.isSelected();
        isAdminSelected = makeAdministrator.isSelected();

        
        if (enterPassword.getText().equals("") && confirmPassword.getText().equals("")) {//basically if we only want to update the permissions, leave the password fields blank
            //if the permissions update works, throw an alert
            if (updatePermissions(selectedToReset)) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Permissions Updated Successfully");
                a.showAndWait();
            }
        } else {//if more than just permissions need to be set, proceed below

            //if the enter and confirm passwords match, then proceed
            if (enterPassword.getText().equals(confirmPassword.getText())) {
                newPassword = confirmPassword.getText();//set the new password

                //perform the password reset, throw an alert if successful
                if (dbm.resetPassword(selectedToReset, newPassword)) {

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Password Reset Successfully");
                    a.showAndWait();

                    enterPassword.clear();
                    confirmPassword.clear();

                }
                //perform any permission updates, throw an alert if successful
                if (updatePermissions(selectedToReset)) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Permissions Updated Successfully");
                    a.showAndWait();
                }

            } else {//alert if passwords do not match

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Passwords do not match");
                a.showAndWait();

                enterPassword.clear();
                confirmPassword.clear();

            }

        }

    }

    /**
     * update permissions will update the permissions based on who the user is
     * @param username
     * @return 
     */
    private boolean updatePermissions(String username) {

        return dbm.makeAdministrator(username, isAdminSelected) || dbm.disableUser(username, isDisabledSelected);//returns true if the admin or disabled permissions are updated

    }

}
