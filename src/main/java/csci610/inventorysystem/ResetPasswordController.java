/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author nicka
 */
public class ResetPasswordController {

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

    private DatabaseManager dbm = new DatabaseManager();

    private Map<String, String> users = new HashMap<>();
    @FXML
    private Button backButton;
    @FXML
    private Button submitButton;
    @FXML
    private CheckBox disabledUser;
    @FXML
    private CheckBox makeAdministrator;

    public void initialize() {

        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        displayNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        users = dbm.loadUsers();

        ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

        userGrid.setItems(userList);

    }

    private String selectedToReset;

    @FXML
    private void getClickedUser(MouseEvent event) {

        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedUser = userGrid.getSelectionModel().getSelectedItem();

            if (selectedUser != null) {

                selectedUserLabel.setText(selectedUser.getKey());
                selectedToReset = selectedUser.getKey();

                if (dbm.getIsAdmin(selectedToReset)) {

                    makeAdministrator.setSelected(true);

                } else {

                    makeAdministrator.setSelected(false);

                }
                if (dbm.getIsDisabled(selectedToReset)) {

                    disabledUser.setSelected(true);

                } else {

                    disabledUser.setSelected(false);

                }

                enterPassword.clear();
                confirmPassword.clear();

            }

        }

    }

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

            App.setRoot("Dashboard");

        } else if (result.get() == bno) {

            a.close();

        }

    }

    private boolean isDisabledSelected;
    private boolean isAdminSelected;

    @FXML
    private void submit(ActionEvent event) {

        String newPassword;

        isDisabledSelected = disabledUser.isSelected();
        isAdminSelected = makeAdministrator.isSelected();

        if (enterPassword.getText().equals("") && confirmPassword.getText().equals("")) {//basically if we only want to update the permissions, leave the password fields blank
            if (updatePermissions(selectedToReset)) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Permissions Updated Successfully");
                a.showAndWait();
            }
        } else {

            if (enterPassword.getText().equals(confirmPassword.getText())) {
                newPassword = confirmPassword.getText();

                if (dbm.resetPassword(selectedToReset, newPassword)) {

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Password Reset Successfully");
                    a.showAndWait();

                    enterPassword.clear();
                    confirmPassword.clear();

                }

                if (updatePermissions(selectedToReset)) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Permissions Updated Successfully");
                    a.showAndWait();
                }

            } else {

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Passwords do not match");
                a.showAndWait();

                enterPassword.clear();
                confirmPassword.clear();

            }

        }

    }

    private boolean updatePermissions(String username) {

        return dbm.makeAdministrator(username, isAdminSelected) || dbm.disableUser(username, isDisabledSelected);

    }

}
