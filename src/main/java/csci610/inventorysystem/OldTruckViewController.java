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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author nicka
 */
public class OldTruckViewController {

    @FXML
    private Button backButton;
    @FXML
    private TableView<Map.Entry<String, String>> SKUTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> SKUCol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> quantityCol;
    @FXML
    private Label deliveryDateLabel;
    @FXML
    private ComboBox<String> truckChoiceBox;

    private Map<String, String> allunpackedTrucks = new HashMap<>();

    private DatabaseManager dbm = new DatabaseManager();
    
    
    public void initialize(){
        
        SKUCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        quantityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allunpackedTrucks = dbm.getAllunPackedTrucks();

        ObservableList<String> Trucks = FXCollections.observableArrayList(allunpackedTrucks.keySet());

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

            App.setRoot("TruckManager");

        } else if (result.get() == bno) {

            a.close();

        }
    }

    private Map<String, String> allSKUsOnTruck = new HashMap<>();
    
    
    @FXML
    private void loadTruckSKUs(ActionEvent event) {
        
        allSKUsOnTruck = dbm.getSKUsonTruck(Integer.parseInt(truckChoiceBox.getValue()));

        deliveryDateLabel.setText(allunpackedTrucks.get(truckChoiceBox.getValue()));

        ObservableList<Map.Entry<String, String>> truckList = FXCollections.observableArrayList(allSKUsOnTruck.entrySet());

        SKUTable.setItems(truckList);
        
    }
    
}
