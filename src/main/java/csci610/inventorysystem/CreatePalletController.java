package csci610.inventorysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.SimpleIntegerProperty;
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
 * createPalletController will insert a new pallet created by a user
 *
 * @author nicka
 */
public class CreatePalletController {

    //FXML controls
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

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private Map<String, String> allSKUs;//allSKUs table will hold all available SKUs into the database

    private Map<String, String> palletSKUs = new HashMap<>();//palletSKUs table will hold SKUs to be inserted into the database 
    @FXML
    private TextField quantityTextField;

    private String selectedSKUs;
    @FXML
    private TextField subBayTF;
    @FXML
    private TextField bayTF;
    @FXML
    private TextField aisleTF;
    @FXML
    private Button createPalletButton;
    @FXML
    private TableView<String> openLocationsTable;
    @FXML
    private TableColumn<String, String> locationsCol;

    private List<String> allLocs = new ArrayList<>();//all locations list that will contain all available locations from the database

    private String selectedItem;

    /**
     * initialize will run on opening the create pallet GUI, it will initialize
     * the table columns and fill the tables with their values.
     */
    public void initialize() {

        //initalize the available SKU table columns
        availableSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        prodTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //initialize the pallet SKU table columns
        palletSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityOfSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));
        locationsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        allLocs = dbm.loadFreeLocations();//load the free pallet locations from the database

        ObservableList<String> allFreeLocs = FXCollections.observableArrayList(allLocs);//observable array list for all the free locations

        openLocationsTable.setItems(allFreeLocs);//show the array list in the table view

        allSKUs = dbm.getAllSKUs();//load all the SKUs from the database

        ObservableList<Map.Entry<String, String>> SKUList = FXCollections.observableArrayList(allSKUs.entrySet());//create an observable array list for the all SKUs

        availableSKUTable.setItems(SKUList);//load the availableSKUtable with the observable arraylist

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
     * addItemToPallet will add an item to the palletTagTable table along with
     * its quantity
     *
     * @param event
     */
    @FXML
    private void addItemToPallet(ActionEvent event) {

        //if the quantity text field is empty, throw an error
        if (quantityTextField.getText().equals("")) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a quantity for the SKU being added");
            a.showAndWait();

        } else {//if there is a quantity, then add the SKU and quantity to the pallet tag table

            palletSKUs.put(selectedSKUs, quantityTextField.getText());//put the SKU and quanity into the palletSKUs map for later insertion

            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());//create an observable arraylist

            palletTagTable.setItems(addedList);//then set the pallettagtable with the values of palletSKUs

        }

    }

    /**
     * removeItemFromPallet will remove an item from the pallet tag table
     *
     * @param event
     */
    @FXML
    private void removeItemFromPallet(ActionEvent event) {

        palletSKUs.remove(selectedSKUs);//remove the SKU from the palletSKUs map

        ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());//re-create the observable arraylist

        palletTagTable.setItems(addedList);//refresh the table

    }

    //input to be gathered later, by either selection or manual input
    private int aisleInt;
    private int aisleBay;
    private int aisleSubBay;

    /**
     * createPallet will attempt to insert the values gathered from the pallet
     * tag table and other spots into the database
     *
     * @param event
     */
    @FXML
    private void createpallet(ActionEvent event) {

        //first gather the location input
        aisleInt = Integer.parseInt(aisleTF.getText());
        aisleBay = Integer.parseInt(bayTF.getText());
        aisleSubBay = Integer.parseInt(subBayTF.getText());

        //then, check if that location is occupied, if it is, throw an error
        if (dbm.locationOccupied(aisleInt, aisleBay, aisleSubBay)) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, location is already occupied");
            a.showAndWait();

            //throw alert since location has a pallet
        } else {//if the location is free, attempt to insert it into the database

            int ID = dbm.insertPalletIntoPallet(aisleInt, aisleBay, aisleSubBay);//first, get the location ID based on the Aisle, Bay, and SubBay

            //then insert the palletSKUs into the SKUsOnPallets table, basically the entire palletSKUs map is inserted into the DB
            if (dbm.insertPalletIntoSKUsOnPallets(palletSKUs)) {

                //if it works, throw a success alert
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Pallet added successfully, your pallet ID is: " + ID);
                a.showAndWait();

                dbm.setLocationOccupied(aisleInt, aisleBay, aisleSubBay);

            }

        }

    }

    /**
     * palletTable will get the clicked on SKU in the pallet tag table
     * @param event
     */
    @FXML
    private void palletTable(MouseEvent event) {
        
        //only a single click is required to select a SKU
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = palletTagTable.getSelectionModel().getSelectedItem();//get the selected item clicked on

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//then set the selected SKU for later insertion/removal

            }

        }

    }

    /**
     * availableSKUsTable will get the clicked on SKU in the all SKUs table
     * @param event 
     */
    @FXML
    private void availableSKUsTable(MouseEvent event) {

        //only a single click is required to get a SKU
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = availableSKUTable.getSelectionModel().getSelectedItem();//get the actual SKU

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//set the selected SKU for later insertion/removal

            }

        }

    }

    /**
     * getLocation will get the location clicked on from the all available locations table
     * @param event 
     */
    @FXML
    private void getLocation(MouseEvent event) {

        //double click is required to select a location
        if (event.getClickCount() == 2) {

            selectedItem = openLocationsTable.getSelectionModel().getSelectedItem();//get the selected item

            String[] dividedLocs = selectedItem.split("-");//split the item that is selected based on "-"

            //set all three text fields to be pre-filled values based on the selected locations
            subBayTF.setText(dividedLocs[2]);
            bayTF.setText(dividedLocs[1]);
            aisleTF.setText(dividedLocs[0]);

        }

    }

}
