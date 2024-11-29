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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * AddLocationController controls the add Location GUI along with inserting a new location into the database
 * @author nicka
 */
public class AddLocationController {

    //FXML controllers
    @FXML
    private Button backButton;
    @FXML
    private TableView<String> allLocsTable;
    @FXML
    private TableColumn<String, String> existingLocationsCol;
    @FXML
    private TextField subBayTF;
    @FXML
    private TextField bayNumTF;
    @FXML
    private TextField aisleNumTF;

    private DatabaseManager dbm = new DatabaseManager();//database access instance

    private List<String> arr = new ArrayList<>();//list for locations tableview

    
    /**
     * initialize will run upon opening the GUI, fills the allLocations table with data for later use.
     * 
     */
    public void initialize() {

        existingLocationsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));//initialize the column in the tableview

        arr = dbm.loadLocations();//get all the locations from the database

        ObservableList<String> allLocs = FXCollections.observableArrayList(arr);//create an observable array list

        allLocsTable.setItems(allLocs);//then set the tableview with the observable array list

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

    //three fields, representing the aisle, bay, and sub-bay, will be set based on user input
    private String aisle;
    private String bay;
    private String subBay;

    /**
     * addLocationToDB will insert the specified location into the database
     * @param event 
     */
    @FXML
    private void addLocationToDB(ActionEvent event) {

        
        //gather the new location data from the user fields on the GUI
        aisle = aisleNumTF.getText();
        bay = bayNumTF.getText();
        subBay = subBayTF.getText();

        //first, check if the location exists, if it does, throw an error alert
        if (dbm.locationExists(Integer.parseInt(aisle), Integer.parseInt(bay), Integer.parseInt(subBay))) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, location exists in database");
            a.showAndWait();

        } else {//if the location does not exist in the database

            
            //if the new location is inserted into the database, throw a success alert
            if (dbm.insertIntoAisleBaySubBay(Integer.parseInt(aisle), Integer.parseInt(bay), Integer.parseInt(subBay))) {

                Alert a1 = new Alert(Alert.AlertType.INFORMATION);
                a1.setTitle("Success!");
                a1.setHeaderText("Location Added Successfully");
                a1.showAndWait();

                //then reload all of the locations from the database
                arr = dbm.loadLocations();

                ObservableList<String> allLocs = FXCollections.observableArrayList(arr);

                allLocsTable.setItems(allLocs);

            } else {//if the insertion fails, throw an error

                Alert a2 = new Alert(Alert.AlertType.ERROR);
                a2.setTitle("Error");
                a2.setHeaderText("Error Inserting into Database");
                a2.showAndWait();

            }

        }

    }

}
