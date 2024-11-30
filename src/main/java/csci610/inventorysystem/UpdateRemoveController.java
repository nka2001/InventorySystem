package csci610.inventorysystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * updateremove controller controls the ability to update and or remove pallets
 * from the system
 *
 * @author nicka
 */
public class UpdateRemoveController {

    //FXML controls
    @FXML
    private Button backButton;
    @FXML
    private TableView<Map.Entry<String, String>> allPalletsTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> PalletIDCol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> locationCol;
    @FXML
    private Label palledIDLabel;

    private Map<String, String> allPallets = new HashMap<>();//contains all pallets in the system

    private DatabaseManager dbm = new DatabaseManager();//database instance

    private int selectedPID;
    @FXML
    private Button removeButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button addRemoveButton;

    private SessionManager sm = SessionManager.getInstance();//session manager instance

    /**
     * initialize will run on opening the update remove pallet page, fills the
     * pallet table with pallet ID and pallet Locations.
     */
    public void initialize() {

        //initialize the column
        PalletIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //fill the table
        allPallets = dbm.getAllPallets();

        ObservableList<Map.Entry<String, String>> palletList = FXCollections.observableArrayList(allPallets.entrySet());

        allPalletsTable.setItems(palletList);

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
     * removeEntirePallet will remove the entire pallet from the database
     *
     * @param event
     */
    @FXML
    private void removeEntirePallet(ActionEvent event) {

        //throw a confirmation alert asking the user if they want to proceed
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Delete Action");
        a.setHeaderText("Are you sure you want to delete this pallet?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        //if yes, begin deletion
        if (result.isPresent() && result.get() == byes) {

            //perform the deletion (deletion anomoly handled by database manager)
            if (dbm.deletePallet(selectedPID)) {

                //if delete successful throw alert
                Alert a2 = new Alert(Alert.AlertType.INFORMATION);
                a2.setTitle("Success!");
                a2.setHeaderText("Successfully removed pallet");
                a2.showAndWait();

                //then get the location
                String location = sm.getLocationID();
                String[] sLocs = location.split("-");

                //split it
                int Aisle = Integer.parseInt(sLocs[0]);
                int Bay = Integer.parseInt(sLocs[1]);
                int subBay = Integer.parseInt(sLocs[2]);

                //set that location free
                dbm.setLocationFree(Aisle, Bay, subBay);

                //refresh all pallets table
                allPallets = dbm.getAllPallets();

                ObservableList<Map.Entry<String, String>> palletList = FXCollections.observableArrayList(allPallets.entrySet());

                allPalletsTable.setItems(palletList);

            }

            //otherwise close alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

    /**
     * updateLocation is a breakout page that will open a new page prompting the user to update the pallet with the new location
     * @param event 
     */
    @FXML
    private void updateLocation(ActionEvent event) {

        try {
            //open updateLocation page 
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateLocation.fxml"));
            Parent root = loader.load();

            //create a new stage, using root and its own size
            Stage s = new Stage();
            s.setTitle("Update Pallet");
            s.setScene(new Scene(root));

            //disable all function in the parent page while the child page is open
            removeButton.setDisable(true);
            updateButton.setDisable(true);
            addRemoveButton.setDisable(true);
            allPalletsTable.setDisable(true);
            backButton.setDisable(true);

            //add a listener to see when the page is closed to re-enable the page function
            s.setOnHidden(e -> {
                removeButton.setDisable(false);
                updateButton.setDisable(false);
                addRemoveButton.setDisable(false);
                allPalletsTable.setDisable(false);
                backButton.setDisable(false);

                //refresh the locations table
                allPallets = dbm.getAllPallets();

                ObservableList<Map.Entry<String, String>> palletList = FXCollections.observableArrayList(allPallets.entrySet());

                allPalletsTable.setItems(palletList);
            });

            s.show();//show the breakout page

        } catch (IOException e) {
            System.out.println("error in updateLocation");
            e.printStackTrace();
        }

    }

    /**
     * addRemoveSKUs is a breakout page and will open a new page for the user to add or remove SKUs to a given pallet
     * @param event 
     */
    @FXML
    private void AddRemoveSKUs(ActionEvent event) {

        try {
            //open the updatePalletSKU page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdatePalletSKU.fxml"));
            Parent root = loader.load();

            //create the stage
            Stage s = new Stage();
            s.setTitle("Update Pallet");
            s.setScene(new Scene(root));

            //disable all function in the parent page while the child page is open
            removeButton.setDisable(true);
            updateButton.setDisable(true);
            addRemoveButton.setDisable(true);
            allPalletsTable.setDisable(true);
            backButton.setDisable(true);

            //add a listener that wait until the breakout page is closed, re-enable all function on close
            s.setOnHidden(e -> {
                removeButton.setDisable(false);
                updateButton.setDisable(false);
                addRemoveButton.setDisable(false);
                allPalletsTable.setDisable(false);
                backButton.setDisable(false);

                //refresh the all pallets table
                allPallets = dbm.getAllPallets();

                ObservableList<Map.Entry<String, String>> palletList = FXCollections.observableArrayList(allPallets.entrySet());

                allPalletsTable.setItems(palletList);
            });

            s.show();
        } catch (IOException e) {
            System.out.println("error in updateLocation");
            e.printStackTrace();
        }

    }

    /**
     * getClickedPallet is used to allow users to select a pallet to modify
     * @param event 
     */
    @FXML
    private void getClickedPallet(MouseEvent event) {

        //double click required to select pallet
        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedPallet = allPalletsTable.getSelectionModel().getSelectedItem();//get the selected pallet

            
            if (selectedPallet != null) {
                
                //enable function buttons
                
                removeButton.setDisable(false);
                updateButton.setDisable(false);
                addRemoveButton.setDisable(false);

                //set palletID in all areas, session manager, labels, and backend variables
                
                palledIDLabel.setText(selectedPallet.getKey());
                selectedPID = Integer.parseInt(selectedPallet.getKey());
                sm.setPalletID(selectedPID);
                sm.setLocationID(selectedPallet.getValue());

            }

        }

    }

}
