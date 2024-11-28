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
 *
 * @author nicka
 */
public class UpdateLocationController {

    @FXML
    private TextField subBayTF;
    @FXML
    private TextField bayNumTF;
    @FXML
    private TextField aisleNumTF;
    @FXML
    private Label selectedPalletID;

    private SessionManager sm = SessionManager.getInstance();

    private DatabaseManager dbm = new DatabaseManager();

    private int oldAisle;
    private int oldBay;
    private int oldSubBay;
    
    private String oldLocation = sm.getLocationID();
    private String[] oldLocs = oldLocation.split("-");
    
    public void initialize() {

        selectedPalletID.setText(String.valueOf(sm.getPalletID()));
        oldAisle = Integer.parseInt(oldLocs[0]);
        oldBay = Integer.parseInt(oldLocs[1]);
        oldSubBay = Integer.parseInt(oldLocs[2]);
        

    }

    @FXML
    private void updateLocationinDB(ActionEvent event) {

        int Aisle = Integer.parseInt(aisleNumTF.getText());
        int Bay = Integer.parseInt(bayNumTF.getText());
        int subBay = Integer.parseInt(subBayTF.getText());

        if (dbm.locationExists(Aisle, Bay, subBay)) {

            if (!dbm.locationOccupied(Aisle, Bay, subBay)) {

                if (dbm.updateLocation(sm.getPalletID(), Aisle, Bay, subBay)) {

                    
                    
                    
                    dbm.setLocationOccupied(Aisle, Bay, subBay);
                    dbm.setLocationFree(oldAisle, oldBay, oldSubBay);

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Success!");
                    a.setHeaderText("Location updated");
                    a.showAndWait();

                } else {

                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error");
                    a.setHeaderText("Error, location not updated");
                    a.showAndWait();

                }

            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error, location is occupied");
                a.showAndWait();
            }

        } else {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, location does not exist");
            a.showAndWait();
        }

    }

}
