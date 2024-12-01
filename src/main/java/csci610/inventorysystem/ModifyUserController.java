package csci610.inventorysystem;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

/**
 * Modify user controllers controls the modify user GUI
 * 
 * enables an admin to change aspects of a user (name, pay, etc.).
 * @author nicka
 */
public class ModifyUserController {

    //FXML controls
    @FXML
    private Button BackButton;
    @FXML
    private Button submitButton;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private TextField DOBInput;
    @FXML
    private RadioButton MaleButton;
    @FXML
    private ToggleGroup MFGroup;
    @FXML
    private RadioButton femaleButton;
    @FXML
    private TextField payRateInput;
    @FXML
    private TextField positionInput;
    @FXML
    private TableView<Map.Entry<String, String>> userGrid;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> usernameCol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> displayNameCol;

    //database access
    private DatabaseManager dbm = new DatabaseManager();//database instance

    //map holding all users 
    private Map<String, String> users = new HashMap<>();//map containing all of the users

    /**
     * initialize will run on opening the modify user page
     * 
     * fills user table with data.
     */
    public void initialize() {

        //initialize columns
        usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        displayNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //load tableview with all data
        users = dbm.loadUsers();

        ObservableList<Map.Entry<String, String>> userList = FXCollections.observableArrayList(users.entrySet());

        userGrid.setItems(userList);

    }

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

    //fields used for future use
    private String selectedUsername;
    private List<User> UserData = new ArrayList<>();

    /**
     * loadUserData will get the selected user's data and pre-fill text fields
     * @param event 
     */
    @FXML
    private void loadUserData(MouseEvent event) {

        //double click required to select a user
        if (event.getClickCount() == 2) {

            //get the actual result
            Map.Entry<String, String> selectUser = userGrid.getSelectionModel().getSelectedItem();

            if (selectUser != null) {

                //set the fields to be prefilled with all of the user's data
                selectedUsername = selectUser.getKey();

                UserData = dbm.loadUserData(selectedUsername);

                firstNameInput.setText(UserData.get(0).getFName());
                lastNameInput.setText(UserData.get(0).getLName());
                DOBInput.setText(UserData.get(0).getDOB());
                if (UserData.get(0).getGender().equalsIgnoreCase("Male")) {
                    MaleButton.setSelected(true);
                } else if (UserData.get(0).getGender().equalsIgnoreCase("Female")) {
                    femaleButton.setSelected(true);
                }
                payRateInput.setText(String.valueOf(UserData.get(0).getPay()));
                positionInput.setText(UserData.get(0).getPos());

            }

        }

    }

    /**
     * modifyUser will cause any changes made to the text field to be changed in the database
     * @param event
     * @throws ParseException 
     */
    @FXML
    private void modifyUser(ActionEvent event) throws ParseException {

        //gather all data from the user
        String fName = firstNameInput.getText();
        String lName = lastNameInput.getText();
        String DOB = DOBInput.getText();
        String gender = "";
        if (MaleButton.isSelected()) {
            gender = "Male";
        } else if (femaleButton.isSelected()) {
            gender = "Female";
        }
        float payRate = Float.parseFloat(payRateInput.getText());
        String position = positionInput.getText();
        
        //attempt to update the database, throw an alert and clear fields if successful
        if(dbm.updateUserInfo(fName, lName, DOB, gender, payRate, position, selectedUsername)){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success!");
            a.setHeaderText("user updated");
            a.showAndWait();
            
            firstNameInput.clear();
            lastNameInput.clear();
            DOBInput.clear();
            MaleButton.setSelected(false);
            femaleButton.setSelected(false);
            payRateInput.clear();
            positionInput.clear();
        }

    }

}
