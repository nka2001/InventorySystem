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
 * oldTruckViewController controls the already processed trucks page, no
 * database actions occur here
 *
 * @author nicka
 */
public class OldTruckViewController {

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
    private Label deliveryDateLabel;
    @FXML
    private ComboBox<String> truckChoiceBox;

    private Map<String, String> allunpackedTrucks = new HashMap<>();//map containing all processed trucks

    private DatabaseManager dbm = new DatabaseManager();//database instance

    /**
     * initialize will run on opening the old truck view page, will fill the
     * combo box with all of the processed trucks.
     */
    public void initialize() {

        //initialize the columns
        SKUCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        quantityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allunpackedTrucks = dbm.getAllunPackedTrucks();//gather all of the processed trucks from the database

        ObservableList<String> Trucks = FXCollections.observableArrayList(allunpackedTrucks.keySet());//create an observable array list

        truckChoiceBox.setItems(Trucks);//load the observable arraylist into the combobox

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

            App.setRoot("TruckManager");

            //if no, just close the alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

    private Map<String, String> allSKUsOnTruck = new HashMap<>();//map used to display all of the SKUs that were on a given truck

    /**
     * loadtruckskus will load all of the SKUs that were on a truck picked from
     * the combo box
     *
     * @param event
     */
    @FXML
    private void loadTruckSKUs(ActionEvent event) {

        if (truckChoiceBox.getItems().size() <= 1) {//first, if the combo box is less than 1, or 0, do nothing

            allSKUsOnTruck = dbm.getSKUsonTruck(Integer.parseInt(truckChoiceBox.getValue()));//get the truck ID from the combo box and retrive all SKUs associated with it

            deliveryDateLabel.setText(allunpackedTrucks.get(truckChoiceBox.getValue()));//get and set the delivery date retrieved from the database

            ObservableList<Map.Entry<String, String>> truckList = FXCollections.observableArrayList(allSKUsOnTruck.entrySet());//create an observable arraylist

            SKUTable.setItems(truckList);//then refresh the tableview
        }

    }

}
