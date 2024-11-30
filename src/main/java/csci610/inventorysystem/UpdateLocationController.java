package csci610.inventorysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * update location controller controls the update location GUI, will allow a
 * user to update the location of a pallet
 *
 * @author nicka
 */
public class UpdateLocationController {

    //FXML controls
    @FXML
    private TextField subBayTF;
    @FXML
    private TextField bayNumTF;
    @FXML
    private TextField aisleNumTF;
    @FXML
    private Label selectedPalletID;

    private SessionManager sm = SessionManager.getInstance();//session manager

    private DatabaseManager dbm = new DatabaseManager();//database manager

    //old pallet location
    private int oldAisle;
    private int oldBay;
    private int oldSubBay;

    //get and split the old location
    private String oldLocation = sm.getLocationID();
    private String[] oldLocs = oldLocation.split("-");

    /**
     * initialize will run when the update location page is opened, initializes
     * the old location, and sets up the pallet to be updated.
     */
    public void initialize() {

        //get the selected pallet from the session manager then set the old location of that pallet
        selectedPalletID.setText(String.valueOf(sm.getPalletID()));
        oldAisle = Integer.parseInt(oldLocs[0]);
        oldBay = Integer.parseInt(oldLocs[1]);
        oldSubBay = Integer.parseInt(oldLocs[2]);

    }

    /**
     * update location in DB will attempt to update the location of the specified pallet, provided the pallet is in a space that is not occupied
     * @param event 
     */
    @FXML
    private void updateLocationinDB(ActionEvent event) {

        //get the new location from the user
        int Aisle = Integer.parseInt(aisleNumTF.getText());
        int Bay = Integer.parseInt(bayNumTF.getText());
        int subBay = Integer.parseInt(subBayTF.getText());

        //first check if the location exists, if it does, good, otherwise throw an error
        if (dbm.locationExists(Aisle, Bay, subBay)) {

            //check if the location is occupied, if yes, throw an error
            if (!dbm.locationOccupied(Aisle, Bay, subBay)) {

                //attempt to update the location, if it works, throw a success alert
                if (dbm.updateLocation(sm.getPalletID(), Aisle, Bay, subBay)) {

                    dbm.setLocationOccupied(Aisle, Bay, subBay);//set the new location to occupied
                    dbm.setLocationFree(oldAisle, oldBay, oldSubBay);//free the old location for future use

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Location updated");
                    a.showAndWait();

                } else {//alert is thrown if there is an error 

                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error");
                    a.setHeaderText("Error, location not updated");
                    a.showAndWait();

                }

            } else {//error for occupied location
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error, location is occupied");
                a.showAndWait();
            }

        } else {//error for non-real location

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, location does not exist");
            a.showAndWait();
        }

    }

}
