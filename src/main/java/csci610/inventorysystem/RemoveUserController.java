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
public class RemoveUserController {

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

    private DatabaseManager dbm = new DatabaseManager();

    private Map<String, String> users = new HashMap<>();

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

    public void initialize() {

        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        displayNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        users = dbm.loadUsers();

        ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

        userGrid.setItems(userList);

    }

    private String selectedToRemove;

    @FXML
    private void Remove(ActionEvent event) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Remove Action");
        a.setHeaderText("Are you sure you want to remove this user?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            if (dbm.removeUser(selectedToRemove)) {

                Alert removed = new Alert(Alert.AlertType.INFORMATION);
                removed.setTitle("Success");
                removed.setHeaderText("User removed successfully");
                removed.showAndWait();

                users = dbm.loadUsers();

                ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

                userGrid.setItems(userList);

            }

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
                selectedToRemove = selectedUser.getKey();
                

            }

        }

    }

}
