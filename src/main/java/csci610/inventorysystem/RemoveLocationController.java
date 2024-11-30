package csci610.inventorysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * removelocationcontroller controls the remove location GUI and removes locations from the database assuming they are unoccuipied
 * @author nicka
 */
public class RemoveLocationController {

    //FXML controls
    @FXML
    private Button backButton;
    @FXML
    private Label selectedLocation;
    @FXML
    private TableView<String> allLocsTable;
    @FXML
    private TableColumn<String, String> existingLocationsCol;

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private List<String> arr = new ArrayList<>();//list for holding all current locations in the database

    private String selectedItem;

    /**
     * initialize method will run upon opening the remove location GUI, loads all locations into a tableview for processing.
     */
    public void initialize() {

        //initialize the tableview column
        existingLocationsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        
        
        arr = dbm.loadLocations();//get all locations from the database

        ObservableList<String> allLocs = FXCollections.observableArrayList(arr);//create an observable arraylist

        allLocsTable.setItems(allLocs);//add the arraylist into the tableview 

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

    /**
     * removelocationfromDB will remove a given location should it be free, if it is occupied, the pallet must be reassigned first
     * @param event 
     */
    @FXML
    private void removeLocationFromDB(ActionEvent event) {

        //split the input from the selected tableview value (aisle-bay-subbay)
        String[] input = selectedItem.split("-");

        //assign split values
        int aisle = Integer.parseInt(input[0]);
        int bay = Integer.parseInt(input[1]);
        int subBay = Integer.parseInt(input[2]);

        //if the location is occupied, throw an error
        if (dbm.locationOccupied(aisle, bay, subBay)) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, location is occupied by a pallet.\neither delete the pallet or reassign it's location.");
            a.showAndWait();

        } else {//otherwise attempt to remove location from the database

            //if the remove succeeds, throw an alert
            if (dbm.removeLocation(aisle, bay, subBay)) {

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Successfully removed location");
                a.showAndWait();

                //then refresh the tableview
                arr = dbm.loadLocations();

                ObservableList<String> allLocs = FXCollections.observableArrayList(arr);

                allLocsTable.setItems(allLocs);

            } else {//otherwise throw an alert saying the location insert failed

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error removing location, either it does not exist or an unknown error occured");
                a.showAndWait();

            }

        }

    }

    /**
     * getClickedLocation will allow the user to click on the tableview to select a location to be removed
     * @param event 
     */
    @FXML
    private void getClickedLocation(MouseEvent event) {

        //double click required for selection
        if (event.getClickCount() == 2) {

            selectedItem = allLocsTable.getSelectionModel().getSelectedItem();//get the item

            selectedLocation.setText(selectedItem);//show it to the user

        }

    }

}
