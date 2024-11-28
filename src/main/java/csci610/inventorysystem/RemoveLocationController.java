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
 *
 * @author nicka
 */
public class RemoveLocationController {

    @FXML
    private Button backButton;
    @FXML
    private Label selectedLocation;
    @FXML
    private TableView<String> allLocsTable;
    @FXML
    private TableColumn<String, String> existingLocationsCol;

    private DatabaseManager dbm = new DatabaseManager();

    private List<String> arr = new ArrayList<>();

    private String selectedItem;

    public void initialize() {

        existingLocationsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        arr = dbm.loadLocations();

        ObservableList<String> allLocs = FXCollections.observableArrayList(arr);

        allLocsTable.setItems(allLocs);

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
    private void removeLocationFromDB(ActionEvent event) {

        String[] input = selectedItem.split("-");

        int aisle = Integer.parseInt(input[0]);
        int bay = Integer.parseInt(input[1]);
        int subBay = Integer.parseInt(input[2]);

        if (dbm.locationOccupied(aisle, bay, subBay)) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, location is occupied by a pallet.\neither delete the pallet or reassign it's location.");
            a.showAndWait();

        } else {

            if (dbm.removeLocation(aisle, bay, subBay)) {

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success!");
                a.setHeaderText("Successfully removed location");
                a.showAndWait();

                arr = dbm.loadLocations();

                ObservableList<String> allLocs = FXCollections.observableArrayList(arr);

                allLocsTable.setItems(allLocs);

            } else {

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error removing location, either it does not exist or an unknown error occured");
                a.showAndWait();

            }

        }

    }

    @FXML
    private void getClickedLocation(MouseEvent event) {

        if (event.getClickCount() == 2) {

            selectedItem = allLocsTable.getSelectionModel().getSelectedItem();

            selectedLocation.setText(selectedItem);

        }

    }

}
