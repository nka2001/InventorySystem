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
 *
 * @author nicka
 */
public class updatePalletSKUController {

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

    private DatabaseManager dbm = new DatabaseManager();

    private Map<String, String> allSKUs;

    private Map<String, String> palletSKUs = new HashMap<>();

    private SessionManager sm = SessionManager.getInstance();
    @FXML
    private Label palletID;

    private String selectedSKUs;

    
    
    
    public void initialize() {

        palletID.setText(String.valueOf(sm.getPalletID()));

        availableSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        prodTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        palletSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityOfSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allSKUs = dbm.getAllSKUs();
        palletSKUs = dbm.getAllPalletTagSKUs(sm.getPalletID());

        ObservableList<Map.Entry<String, String>> SKUList = FXCollections.observableArrayList(allSKUs.entrySet());
        ObservableList<Map.Entry<String, String>> PalletList = FXCollections.observableArrayList(palletSKUs.entrySet());

        availableSKUTable.setItems(SKUList);
        palletTagTable.setItems(PalletList);

    }

    @FXML
    private void addItemToPallet(ActionEvent event) {

        if (quantityTextField.getText().equals("")) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a quantity for the SKU being added");
            a.showAndWait();

        } else {

            palletSKUs.put(selectedSKUs, quantityTextField.getText());

            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());

            palletTagTable.setItems(addedList);

        }

    }

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

            palletSKUs.remove(selectedSKUs);

            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(palletSKUs.entrySet());

            palletTagTable.setItems(addedList);

        }

    }

    @FXML
    private void updatePallet(ActionEvent event) {

        if (dbm.updatePalletSKUs(palletSKUs, sm.getPalletID())) {

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success!");
            a.setHeaderText("Pallet updated successfully, your pallet ID is: " + sm.getPalletID());
            a.showAndWait();

        }

    }

    @FXML
    private void palletTable(MouseEvent event) {

        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = palletTagTable.getSelectionModel().getSelectedItem();

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();

            }

        }

    }

    @FXML
    private void availableSKUsTable(MouseEvent event) {

        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = availableSKUTable.getSelectionModel().getSelectedItem();

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();

            }

        }

    }

}
