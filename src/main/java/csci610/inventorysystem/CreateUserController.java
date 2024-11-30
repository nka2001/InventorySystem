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
 * create user controller controls the create user GUI, used to insert a new user into the database
 * @author nicka
 */
public class CreateUserController {

    //FXML controllers
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

    private DatabaseManager dbm = new DatabaseManager();//database access instance

    
    //user input fields
    private String password = "";
    private String fName = "";
    private String lName = "";
    private String displayName = "";
    private String DateOfBirth = "";
    private String gender = "";
    private String payRate = "";
    private String position = "";

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
    
    /**
     * createNewUser will insert a new user into the database
     * @param event 
     */
    @FXML
    private void createNewUser(ActionEvent event) {

        String username = usernameInput.getText();//get the username from the page

        if (!username.equalsIgnoreCase("")) {//first check if the username field is blank, if so, throw an error

            if (!dbm.findUser(username)) {//then, check if the user exists, throw an error if true

                //gather input from the page, cast if needed
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

                //validate the input, check for any empty text fields/missing info, throw an error if this is the case
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
                        //attempt to add the user into the database, try-catch used for date data type conversion
                        if (dbm.addUser(username, password, displayName, fName, lName, DateOfBirth, gender, pay, isAdmin, position, isDisabled)) {
                            //throw an alert if successful
                            Alert a = new Alert(Alert.AlertType.INFORMATION);
                            a.setTitle("User Added");
                            a.setContentText("User Added Successfully!");
                            a.showAndWait();

                        }
                    } catch (ParseException ex) {
                        System.out.println("parse error in createUser");
                        ex.printStackTrace();
                    }

                }

            } else {//if the user exists already

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("ERROR CREATING USER");
                a.setContentText("Error, the user already exists in the database");
                a.showAndWait();
            }
            
        } else {//if a text field is empty, throw an error
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
