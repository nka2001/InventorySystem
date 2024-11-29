package csci610.inventorysystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * AddSKUController controls the add SKU GUI, this will insert a new product (SKU) into the database
 * @author nicka
 */
public class AddSKUController {

    //FXML Controllers
    @FXML
    private Button backButton;
    @FXML
    private TextField SKUTF;
    @FXML
    private TextField ProdTitleTF;
    @FXML
    private TextField ProdDescTF;
    @FXML
    private TextField PriceTF;
    @FXML
    private TextField StockTF;

    @FXML
    private Button addProduct;

    private DatabaseManager dbm = new DatabaseManager();//database access instance
    @FXML
    private Label selectedUserLabel;
    @FXML
    private ComboBox<String> departmentCombo;

    private Map<String, String> allDepts = new HashMap<>();//map for holding all the departments and their IDs (used for input collection)

    /**
     * initialize will run on opening the addSKU GUI, will load the department choicebox with all the current departments in the database.
     */
    public void initialize() {

        allDepts = dbm.getAllDepts();//get all of the departments in the database
        ObservableList<String> deps = FXCollections.observableArrayList(allDepts.values());//create an observable arraylist from the alldepartments arraylist
        departmentCombo.setItems(deps);//load the department combo with all the department values

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
    
    //all fields represent columns in the database, will be set from user input
    private String newSKU;
    private String newProductTitle;
    private String newProductDesc;
    private float newPri;
    private int newStock;
    private int newDept = -1;

    
    /**
     * addProdToDB will add a new product to the database based on user input
     * @param event 
     */
    @FXML
    private void addProdToDB(ActionEvent event) {

        newSKU = SKUTF.getText();//first, get the new SKU entered

        if (!dbm.findSKU(newSKU)) {//then, check if that SKU exists, if it does, throw an error

            //collect input from the user (cast if needed)
            newProductTitle = ProdTitleTF.getText();
            newProductDesc = ProdDescTF.getText();
            String newPrice = PriceTF.getText();
            newPri = Float.parseFloat(newPrice);

            String stock = StockTF.getText();
            newStock = Integer.parseInt(stock);

            newDept = findDeptID(departmentCombo.getValue());//since the department combo box is the value, not key, get the key based on the value from the getDeptID method then set it

            //validate the input, make sure no text field or choice is empty, if it is, throw an error
            if (validateInput()) {

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error adding product, one or many of the text fields are empty, please go back and fill it in");
                a.showAndWait();

            } else {//if the input is good, begin insertion

                //insert the new SKU into the database, throw a success alert
                if (dbm.addSKU(newSKU, newProductTitle, newProductDesc, newPri, newStock, newDept)) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Product Added");
                    a.setHeaderText("Product Added Successfully!");
                    a.showAndWait();

                    //clear all the text fields 
                    SKUTF.clear();
                    ProdTitleTF.clear();
                    ProdDescTF.clear();
                    PriceTF.clear();
                    StockTF.clear();

                }

            }

        } else {//if the SKU exists in the database, throw an alert

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error SKU already in database");
            a.showAndWait();

        }

    }

    /**
     * validateInput will check all the text fields for some kind of input, if it is empty, then it will throw an error
     * @return 
     */
    private boolean validateInput() {

        //returns true if at least one text field is empty (0 or -1 for the number based fields)
        return newSKU.equalsIgnoreCase("") || newProductTitle.equalsIgnoreCase("") || newProductDesc.equalsIgnoreCase("") || newPri == 0 || newDept == -1;
    }

    /**
     * findDeptID will search the Map based on a value for its coresponding key
     * @param deptName
     * @return 
     */
    private int findDeptID(String deptName) {

        int ID = -1;//-1 is used if the department ID cannot be found based on the provided department name

        //iterate through the map, searching key by key for the correct value
        for (Map.Entry<String, String> entry : allDepts.entrySet()) {

            //if the current entry's value equals the provided department name, set the ID
            if (entry.getValue().equals(deptName)) {
                ID = Integer.parseInt(entry.getKey());
            }

        }

        return ID;//return the ID -1 for not found
    }

}
