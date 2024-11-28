package csci610.inventorysystem;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PrimaryController {

   
    
    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Button loginButton;

    private String username;
    private String password;

    private DatabaseManager dbm = new DatabaseManager();
    
    private UserManager um = UserManager.getInstance();
    
    private SessionManager sm = SessionManager.getInstance();
    
    

    @FXML
    private void OpenDashboard(ActionEvent event) throws IOException {

        System.out.println("login clicked");

        if (!usernameInput.getText().equalsIgnoreCase("")) {

            username = usernameInput.getText();
            password = passwordInput.getText();

            boolean goodLogin = checkLogin(username, password);
            
            if(!um.findUser(username)){
                
                if(goodLogin){
                    
                    um.addUser(username);
                    sm.setUser(username);
                    
                    App.setRoot("Dashboard");
                    
                    
                } else {
                    
                    makeAlert(AlertType.ERROR, "Error Logging In", "Incorrect Authentication", "Error, the username or password is incorrect, or the user is disabled");
                    
                }
                
                
                
            } else {
                makeAlert(AlertType.ERROR, "Error Logging In", "User Already Logged In", "Error, the user specified is already logged in");
            }
            

        } else {
            makeAlert(AlertType.ERROR, "Error Logging In", "No Username Found", "Error, no username entered, please enter a valid username");
        }

    }

    private boolean checkLogin(String username, String password) {

        return dbm.init_ConnectionToDB(username, password);

    }

    private void makeAlert(AlertType at, String Title, String header, String content) {

        Alert a = new Alert(at);
        a.setTitle(Title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();

    }
    
    public String getLoggedInUser(){
        return username;
    }

}
