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
 *
 * @author nicka
 */
public class CreateTruckController {

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

    private DatabaseManager dbm = new DatabaseManager();

    private Map<String, String> allSKUs;

    private Map<String, String> palletSKUs = new HashMap<>();

    private String selectedSKUs;
    @FXML
    private TextField DeliveryDateTF;
    @FXML
    private TextField truckIDNumTF;

    public void initialize() {

        availableSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        prodTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        palletSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityOfSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allSKUs = dbm.getAllSKUs();

        ObservableList<Map.Entry<String, String>> SKUList = FXCollections.observableArrayList(allSKUs.entrySet());

        availableSKUTable.setItems(SKUList);

    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Cancel Action");
        a.setHeaderText("Are you sure you want to cancel?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            App.setRoot("TruckManager");

        } else if (result.get() == bno) {

            a.close();

        }
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

    @FXML
    private void createTruck(ActionEvent event) throws ParseException {

        String ddate = DeliveryDateTF.getText();

        int tID = Integer.parseInt(truckIDNumTF.getText());

        if (dbm.getTruckExists(tID)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, Truck ID exists");
            a.showAndWait();
        } else {

            if (dbm.insertNewTruck(tID, ddate)) {

                if (dbm.insertIntoSKUsOnTrucks(palletSKUs, tID)) {

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Truck Created Successfully");
                    a.showAndWait();

                }

            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error!");
                a.setHeaderText("Truck not created");
                a.showAndWait();
            }

        }

    }

}
