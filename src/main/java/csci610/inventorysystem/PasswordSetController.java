package csci610.inventorysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author nicka
 */
public class PasswordSetController {

    private Stage s;

    @FXML
    private Button submitButton;
    @FXML
    private TextField enterPassword;
    @FXML
    private TextField confirmPassword;

    private String newPassword;

    private DatabaseManager dbm = new DatabaseManager();

    private SessionManager session = SessionManager.getInstance();

    public void setStage(Stage s) {
        this.s = s;
    }

    @FXML
    private void submit(ActionEvent event) {

        if (enterPassword.getText().equals(confirmPassword.getText())) {

            newPassword = confirmPassword.getText();
            if (dbm.resetPassword(session.getUser(), newPassword)) {

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Password Reset Successfully");
                a.showAndWait();

                enterPassword.clear();
                confirmPassword.clear();
                
                dbm.removePWChange(session.getUser());

                if (s != null) {
                    s.close();
                }
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error!");
            a.setHeaderText("Error Passwords do not match");
            a.showAndWait();
        }
    }
}
