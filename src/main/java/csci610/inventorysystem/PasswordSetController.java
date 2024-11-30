package csci610.inventorysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * password set controller used by login page to force a user to change their
 * password if the password needs to be changed
 *
 * @author nicka
 */
public class PasswordSetController {

    private Stage s;//stage is needed by the primary controller in order to be closed on successful password reset

    //FXML controllers
    @FXML
    private Button submitButton;
    @FXML
    private TextField enterPassword;
    @FXML
    private TextField confirmPassword;

    private String newPassword;

    private DatabaseManager dbm = new DatabaseManager();//database access instance

    private SessionManager session = SessionManager.getInstance();//session manager instance (used to store information to be used across different classes)

    /**
     * set stage will set the breakout stage by the primary controller page,
     * will be used later when it is time to close
     *
     * @param s
     */
    public void setStage(Stage s) {
        this.s = s;
    }

    /**
     * submit will attempt to reset the user password, if the reset is
     * successful, then the page is closed and the user is allowed to login
     *
     * @param event
     */
    @FXML
    private void submit(ActionEvent event) {

        //first, make sure the password is equal between two text fields
        if (enterPassword.getText().equals(confirmPassword.getText())) {

            //then set the new password
            newPassword = confirmPassword.getText();
            
            //attempt to reset the password, if successful throw alert and close page
            if (dbm.resetPassword(session.getUser(), newPassword)) {

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Password Reset Successfully");
                a.showAndWait();

                //clear both fields
                enterPassword.clear();
                confirmPassword.clear();

                //remove the password change field (boolean) from the database
                dbm.removePWChange(session.getUser());

                //then close the page
                if (s != null) {
                    s.close();
                }
            }
        } else {//if the passwords do not match between the enter and confirm fields, throw an error
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error!");
            a.setHeaderText("Error Passwords do not match");
            a.showAndWait();
        }
    }
}
