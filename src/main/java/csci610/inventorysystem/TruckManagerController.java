/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author nicka
 */
public class TruckManagerController {

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

    private Map<String, String> allUnpackedTrucks = new HashMap<>();

    private DatabaseManager dbm = new DatabaseManager();
    @FXML
    private Button createNewTruck;
    @FXML
    private Button viewOldTrucks;

    public void initialize() {

        SKUCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        quantityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allUnpackedTrucks = dbm.getAllTrucks();

        ObservableList<String> Trucks = FXCollections.observableArrayList(allUnpackedTrucks.keySet());

        truckChoiceBox.setItems(Trucks);

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

            App.setRoot("Dashboard");

        } else if (result.get() == bno) {

            a.close();

        }
    }

    @FXML
    private void processTruck(ActionEvent event) {

        int selectedTruckID = Integer.parseInt(truckChoiceBox.getValue());

        if (dbm.processTruck(selectedTruckID)) {

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success!");
            a.setHeaderText("Truck Successfully Processed");
            a.showAndWait();

            allSKUsOnTruck = dbm.getSKUsonTruck(Integer.parseInt(truckChoiceBox.getValue()));

            deliveryDateLabel.setText("");
            
            SKUTable.setItems(FXCollections.observableArrayList());
            
            allUnpackedTrucks = dbm.getAllTrucks();
            ObservableList<String> Trucks = FXCollections.observableArrayList(allUnpackedTrucks.keySet());

            truckChoiceBox.setItems(Trucks);

        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error truck not Processed");
            a.showAndWait();
        }

    }

    private Map<String, String> allSKUsOnTruck = new HashMap<>();

    @FXML
    private void loadTruckSKUs(ActionEvent event) {

        allSKUsOnTruck = dbm.getSKUsonTruck(Integer.parseInt(truckChoiceBox.getValue()));

        deliveryDateLabel.setText(allUnpackedTrucks.get(truckChoiceBox.getValue()));

        ObservableList<Map.Entry<String, String>> truckList = FXCollections.observableArrayList(allSKUsOnTruck.entrySet());

        SKUTable.setItems(truckList);

    }

    @FXML
    private void createNewTruck(ActionEvent event) throws IOException {
        
        App.setRoot("CreateNewTruck");
        
    }

    @FXML
    private void viewOldTrucks(ActionEvent event) throws IOException {
        
        App.setRoot("OldTruckView");
        
        
    }

}
