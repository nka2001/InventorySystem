package csci610.inventorysystem;

import java.io.IOException;
import java.text.ParseException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * createTruckController controls the create truck GUI
 *
 * @author nicka
 */
public class CreateTruckController {

    //FXML controllers
    @FXML
    private Button backButton;
    @FXML
    private Button addItem;
    @FXML
    private Button removeItem;
    @FXML
    private TableView<Map.Entry<String, String>> palletTagTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> palletSKU;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> QuantityOfSKU;
    @FXML
    private TableView<Map.Entry<String, String>> availableSKUTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> availableSKU;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> prodTitle;
    @FXML
    private Button createTruckButton;
    @FXML
    private TextField quantityTextField;

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private Map<String, String> allSKUs;//map for holding all the SKUs

    private Map<String, String> palletSKUs = new HashMap<>();//skus to be added to the truck

    private String selectedSKUs;
    @FXML
    private TextField DeliveryDateTF;
    @FXML
    private TextField truckIDNumTF;

    /**
     * initialize method will be ran on opening the create truck controller, it
     * fills the tableviews with values.
     */
    public void initialize() {

        //initialize the columns in the allSKU table
        availableSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        prodTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //initialize the columns in the truck SKU table
        palletSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityOfSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allSKUs = dbm.getAllSKUs();//get all of the SKUs from the database

        ObservableList<Map.Entry<String, String>> SKUList = FXCollections.observableArrayList(allSKUs.entrySet());//create an observable array list

        availableSKUTable.setItems(SKUList);//set the table view with all the values from the observable arraylist

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

    /**
     * addItemToPallet will attempt to add an Item into the truck order table
     *
     * @param event
     */
    @FXML
    private void addItemToPallet(ActionEvent event) {

        //first, check if the quantity text field is empty
        if (quantityTextField.getText().equals("")) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a quantity for the SKU being added");
            a.showAndWait();

        } else {//if the quantity is not empty, insert it into the truckSKU table

            palletSKUs.put(selectedSKUs, quantityTextField.getText());//add the SKU into the truckSKU list

            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());

            palletTagTable.setItems(addedList);//then set the items based on the observablearraylist

        }
    }

    /**
     * removeItemFromPallet will remove an item from the truck SKU table
     *
     * @param event
     */
    @FXML
    private void removeItemFromPallet(ActionEvent event) {

        palletSKUs.remove(selectedSKUs);//first remove the item from the list

        ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());

        palletTagTable.setItems(addedList);//then remove it from the tableview

    }

    /**
     * palletTable will get the selected item that is clicked on in the pallet table
     * @param event 
     */
    @FXML
    private void palletTable(MouseEvent event) {
        //only a single click is needed to select a SKU in the pallet tag table
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = palletTagTable.getSelectionModel().getSelectedItem();//get the actual item

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//set the selected SKU for insertion/removal

            }

        }
    }

    /**
     * availableSKUsTable will get the selected SKU based on which one the user clicks on
     * @param event 
     */
    @FXML
    private void availableSKUsTable(MouseEvent event) {
        
        //only a single click is needed to select a SKU
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = availableSKUTable.getSelectionModel().getSelectedItem();//get the selected item

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//set the value for later insertion/deletion

            }

        }
    }

    /**
     * createTruck will be used to insert the newly created truck into the database
     * @param event
     * @throws ParseException 
     */
    @FXML
    private void createTruck(ActionEvent event) throws ParseException {

        String ddate = DeliveryDateTF.getText();//delivery date

        int tID = Integer.parseInt(truckIDNumTF.getText());//truck number, casted to int

        //first check if the truckID exists, if it does, throw an error
        if (dbm.getTruckExists(tID)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, Truck ID exists");
            a.showAndWait();
        } else {//if the truckID does not exist, insert it into the DB

            //attempt to insert the truck into the database
            if (dbm.insertNewTruck(tID, ddate)) {
                
                //if the insert works, throw an alert
                if (dbm.insertIntoSKUsOnTrucks(palletSKUs, tID)) {

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Truck Created Successfully");
                    a.showAndWait();

                }

            } else {//if the insert fails, throw an error
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error!");
                a.setHeaderText("Truck not created");
                a.showAndWait();
            }

        }

    }

}
