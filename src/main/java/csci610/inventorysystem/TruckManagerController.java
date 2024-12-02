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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * truck manager controller, used to create, view, and assign trucks to users
 * @author nicka
 */
public class TruckManagerController {

    //FXML controls
    @FXML
    private Button backButton;
    @FXML
    private TableView<Map.Entry<String, String>> SKUTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> SKUCol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> quantityCol;
    @FXML
    private ComboBox<String> truckChoiceBox;
    @FXML
    private Label deliveryDateLabel;
    @FXML
    private Button processTruck;

    private Map<String, String> allUnpackedTrucks = new HashMap<>();//map containing all trucks that have not been processed

    private DatabaseManager dbm = new DatabaseManager();//database instance
    @FXML
    private Button createNewTruck;
    @FXML
    private Button viewOldTrucks;

    /**
     * initialize will run on opening truck manager controller, sets up tableview for trucks.
     */
    public void initialize() {

        //initialize columns in trucks table
        SKUCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        quantityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //fill the tableview with data
        allUnpackedTrucks = dbm.getAllTrucks();

        ObservableList<String> Trucks = FXCollections.observableArrayList(allUnpackedTrucks.keySet());

        truckChoiceBox.setItems(Trucks);

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

    private SessionManager sm = SessionManager.getInstance();
    
    /**
     * process truck will mark a truck as unpacked and move it to old trucks (which can be viewd).
     * @param event 
     */
    @FXML
    private void processTruck(ActionEvent event) {

        int selectedTruckID = Integer.parseInt(truckChoiceBox.getValue());//get the selected truck ID from unprocessed trucks

        //attempt to process the truck, if the truck is processed, throw an alert
        if (dbm.processTruck(selectedTruckID, sm.getUser(), allSKUsOnTruck)) {

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success!");
            a.setHeaderText("Truck Successfully Processed");
            a.showAndWait();
            
            //reset the tableview, removing the processed truck from it

            allSKUsOnTruck = dbm.getSKUsonTruck(Integer.parseInt(truckChoiceBox.getValue()));

            deliveryDateLabel.setText("");
            
            SKUTable.setItems(FXCollections.observableArrayList());
            
            allUnpackedTrucks = dbm.getAllTrucks();
            ObservableList<String> Trucks = FXCollections.observableArrayList(allUnpackedTrucks.keySet());

            truckChoiceBox.setItems(Trucks);

        } else {//if the truck is not processed for some reason, throw an error
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error truck not Processed");
            a.showAndWait();
        }

    }

    private Map<String, String> allSKUsOnTruck = new HashMap<>();//map containing all SKUs and the quantity on a given truck

    /**
     * loadTruckSKUs will load the contents of a truck into the tableview
     * @param event 
     */
    @FXML
    private void loadTruckSKUs(ActionEvent event) {

        //get and set all SKUs on a given truck into the tableview
        allSKUsOnTruck = dbm.getSKUsonTruck(Integer.parseInt(truckChoiceBox.getValue()));

        deliveryDateLabel.setText(allUnpackedTrucks.get(truckChoiceBox.getValue()));

        ObservableList<Map.Entry<String, String>> truckList = FXCollections.observableArrayList(allSKUsOnTruck.entrySet());

        SKUTable.setItems(truckList);

    }

    /**
     * moves to create new truck (all users).
     * @param event
     * @throws IOException 
     */
    @FXML
    private void createNewTruck(ActionEvent event) throws IOException {
        
        App.setRoot("CreateNewTruck");
        
    }

    /**
     * moves to view old trucks (all users).
     * @param event
     * @throws IOException 
     */
    @FXML
    private void viewOldTrucks(ActionEvent event) throws IOException {
        
        App.setRoot("OldTruckView");
        
        
    }

}
