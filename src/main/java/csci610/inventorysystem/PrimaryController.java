package csci610.inventorysystem;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * primary controller is the login page for this application, will ask a user
 * for their username and password and authenticate them before advancing to the
 * dashboard
 *
 * @author nicka
 */
public class PrimaryController {

    //FXML controllers
    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Button loginButton;

    private String username;
    private String password;

    private DatabaseManager dbm = new DatabaseManager();//database access instance

    private UserManager um = UserManager.getInstance();//usermanager session to be used across multiple classes

    private SessionManager sm = SessionManager.getInstance();//session manager session allows values to be access across multiple classes

    /**
     * openDashboard will attempt to authenticate the user with the database and then advance them to the dashboard should it work
     * @param event
     * @throws IOException 
     */
    @FXML
    private void OpenDashboard(ActionEvent event) throws IOException {

        
        //first, check if the username field is empty, if it is throw an alert
        if (!usernameInput.getText().equalsIgnoreCase("")) {

            //get the username and password specified
            username = usernameInput.getText();
            password = passwordInput.getText();

            //check if the login is good, used later on
            boolean goodLogin = checkLogin(username, password);

            //check if the user exists, if not throw an error
            if (!um.findUser(username)) {

                //if the login is valid, then advance
                if (goodLogin) {
                    
                    //add the user into the session manager
                    sm.setUser(username);

                    //if the user requires a password change, open the password set breakout page forcing the user to change their password
                    if (dbm.reuqiresChange(username)) {
                        
                        try {
                            //attempt to load the breakout page, setting the title and size
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResetPasswordUser.fxml"));
                            Parent root = loader.load();

                            Stage s = new Stage();
                            s.setTitle("Reset Password");
                            s.setScene(new Scene(root));

                            //send the scene over to the passwordsetcontroller, so that page can close on a good password reset
                            PasswordSetController c = loader.getController();
                            c.setStage(s);

                            //disable all fields in the login page while the breakout page is open
                            passwordInput.setDisable(true);
                            usernameInput.setDisable(true);
                            loginButton.setDisable(true);

                            //use an event handler to re-enable the fields when the breakout page is closed
                            s.setOnHidden(e -> {
                                passwordInput.setDisable(false);
                                usernameInput.setDisable(false);
                                loginButton.setDisable(false);
                                passwordInput.clear();//also clear the password field so the user can sign in again

                            });

                            s.showAndWait();//show the breakout page until it is closed on a good password reset

                        } catch (IOException e) {
                            System.out.println("error in primary controller");
                            e.printStackTrace();
                        }

                    } else {//if the user does not require a password change, just push them through
                        um.addUser(username);//add the user into the user manager (used to track who is online)
                        App.setRoot("Dashboard");//move the user to the dashboard page
                    }

                } else {//if the password is incorrect or if the username is incorrect

                    makeAlert(AlertType.ERROR, "Error Logging In", "Incorrect Authentication", "Error, the username or password is incorrect, or the user is disabled");

                }

            } else {//if the user is already logged in somewhere
                makeAlert(AlertType.ERROR, "Error Logging In", "User Already Logged In", "Error, the user specified is already logged in");
            }

        } else {//no username found
            makeAlert(AlertType.ERROR, "Error Logging In", "No Username Found", "Error, no username entered, please enter a valid username");
        }

    }

    /**
     * checkLogin will attempt to authenticate the user with the database, returns true if the user and password are good
     * @param username
     * @param password
     * @return 
     */
    private boolean checkLogin(String username, String password) {

        return dbm.init_ConnectionToDB(username, password);

    }
    
    /**
     * attempt to generalize the alerts, by passing the alert type, text, and title
     * @param at
     * @param Title
     * @param header
     * @param content 
     */
    private void makeAlert(AlertType at, String Title, String header, String content) {

        Alert a = new Alert(at);
        a.setTitle(Title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();

    }

    /**
     * possibly legacy
     * @return 
     */
    public String getLoggedInUser() {
        return username;
    }

}
