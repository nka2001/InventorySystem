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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * update pallet SKU controller is a breakout page that is used to add or remove SKUs on pallets
 * @author nicka
 */
public class updatePalletSKUController {

    //FXML controls
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
    private Button updatePalletButton;
    @FXML
    private TextField quantityTextField;

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private Map<String, String> allSKUs;//all SKUs table

    private Map<String, String> palletSKUs = new HashMap<>();//palletSKUs to be modified

    private SessionManager sm = SessionManager.getInstance();//session manager instance
    @FXML
    private Label palletID;

    private String selectedSKUs;

    
    
    /**
     * initialize will run when update pallet SKU controller is opened, it will fill and initialize all tableviews.
     */
    public void initialize() {

        palletID.setText(String.valueOf(sm.getPalletID()));//set the palletID of the pallet being updated

        //initialize all SKU columns
        availableSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        prodTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //initialize pallet SKU columns
        palletSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityOfSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //get all SKUs in database and all SKU on given pallet ID
        allSKUs = dbm.getAllSKUs();
        palletSKUs = dbm.getAllPalletTagSKUs(sm.getPalletID());

        //make observable lists for both
        ObservableList<Map.Entry<String, String>> SKUList = FXCollections.observableArrayList(allSKUs.entrySet());
        ObservableList<Map.Entry<String, String>> PalletList = FXCollections.observableArrayList(palletSKUs.entrySet());

        //fill the tableviews with data
        availableSKUTable.setItems(SKUList);
        palletTagTable.setItems(PalletList);

    }

    /**
     * add item to pallet will add a selected item to the existing pallet
     * @param event 
     */
    @FXML
    private void addItemToPallet(ActionEvent event) {

        //if there is no quanity, throw an error
        if (quantityTextField.getText().equals("")) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a quantity for the SKU being added");
            a.showAndWait();

        } else {//otherwise begin processing

            //refresh the pallet tag with the newly added SKU
            palletSKUs.put(selectedSKUs, quantityTextField.getText());

            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());

            palletTagTable.setItems(addedList);

        }

    }

    /**
     * remove Item from pallet will remove a selected item from the pallet if it is not the last item in the pallet
     * @param event 
     */
    @FXML
    private void removeItemFromPallet(ActionEvent event) {

        
        if (palletSKUs.size() <= 1) {//if the sku on the pallet is the last sku, just delete the whole pallet, for now just throw an alert prompting the user to just delete the pallet

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("error");
            a.setHeaderText("Error, there is only one SKU in the database, please use the remove entire pallet option, or add the new SKU first");
            a.showAndWait();

        } else {//if there are multiple SKUs and we are removing one of the many SKUs, just remove it from the table

            if (palletSKUs.containsKey(selectedSKUs)) {

                dbm.removeSKUFromPallet(selectedSKUs);
                palletSKUs.remove(selectedSKUs);

            }

            //remove and refresh the pallet tag table
            palletSKUs.remove(selectedSKUs);

            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());

            palletTagTable.setItems(addedList);

        }

    }

    /**
     * update pallet will write the modified pallet to the database
     * @param event 
     */
    @FXML
    private void updatePallet(ActionEvent event) {

        //essentially what happens is the pallet contents are deleted and re-added
        if (dbm.updatePalletSKUs(palletSKUs, sm.getPalletID())) {

            //if successful, throw alert
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success!");
            a.setHeaderText("Pallet updated successfully, your pallet ID is: " + sm.getPalletID());
            a.showAndWait();

        }

    }

    /**
     * pallet table will get the clicked SKU
     * @param event 
     */
    @FXML
    private void palletTable(MouseEvent event) {

        //single click required for selection
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = palletTagTable.getSelectionModel().getSelectedItem();//get the actual value

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//set the value for future insertion/deletion

            }

        }

    }

    /**
     * availableSKU table will get the selected SKU from the all SKUs table
     * @param event 
     */
    @FXML
    private void availableSKUsTable(MouseEvent event) {

        //single click required for selection
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = availableSKUTable.getSelectionModel().getSelectedItem();//get selected value

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//set the value for future insertion/deletion

            }

        }

    }

}
