package csci610.inventorysystem;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author nicka
 */
public class CreateUserController {

    @FXML
    private Button BackButton;
    @FXML
    private Button submitButton;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
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
    private CheckBox AdministratorInput;
    @FXML
    private CheckBox AccountDisabledInput;

    private DatabaseManager dbm = new DatabaseManager();

    private String password = "";
    private String fName = "";
    private String lName = "";
    private String displayName = "";
    private String DateOfBirth = "";
    private String gender = "";
    private String payRate = "";
    private String position = "";

    @FXML
    private void GoBack(ActionEvent event) throws IOException {

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

    @FXML
    private void createNewUser(ActionEvent event) {

        String username = usernameInput.getText();

        if (!username.equalsIgnoreCase("")) {

            if (!dbm.findUser(username)) {

                password = passwordInput.getText();
                fName = firstNameInput.getText();
                lName = lastNameInput.getText();
                displayName = fName + " " + lName;
                DateOfBirth = DOBInput.getText();
                gender = "";
                if (MaleButton.isSelected()) {
                    gender = "Male";
                } else if (femaleButton.isSelected()) {
                    gender = "Female";
                }
                payRate = payRateInput.getText();
                position = positionInput.getText();

                boolean isAdmin = false;

                if (AdministratorInput.isSelected()) {
                    isAdmin = true;
                }

                boolean isDisabled = false;

                if (AccountDisabledInput.isSelected()) {
                    isDisabled = true;
                }

                if (validateInput()) {

                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error Creating User");
                    a.setHeaderText("Error");
                    a.setContentText("Error creating user, one or many of the input fields are empty, please go back and fill it in");
                    a.showAndWait();

                    //if a text field is not empty, then add it to DB
                } else {

                    float pay = Float.parseFloat(payRate);

                    try {
                        if (dbm.addUser(username, password, displayName, fName, lName, DateOfBirth, gender, pay, isAdmin, position, isDisabled)) {

                            Alert a = new Alert(Alert.AlertType.INFORMATION);
                            a.setTitle("User Added");
                            a.setContentText("User Added Successfully!");
                            a.showAndWait();

                        }
                    } catch (ParseException ex) {

                    }

                }

            } else {

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("ERROR CREATING USER");
                a.setContentText("Error, the user already exists in the database");
                a.showAndWait();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error Creating User");
            a.setHeaderText("Error");
            a.setContentText("Error creating user, one or many of the input fields are empty, please go back and fill it in");
            a.showAndWait();
        }
    }

    /**
     * just looking for empty values, nothing should be empty
     */
    private boolean validateInput() {

        return password.equalsIgnoreCase("") || fName.equalsIgnoreCase("") || lName.equalsIgnoreCase("") || DateOfBirth.equalsIgnoreCase("") || gender.equalsIgnoreCase("") || payRate.equalsIgnoreCase("") || position.equalsIgnoreCase("");

    }

}
