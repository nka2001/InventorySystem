/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author nicka
 */
public class AddLocationController {

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

    private DatabaseManager dbm = new DatabaseManager();

    private List<String> arr = new ArrayList<>();

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

    private String aisle;
    private String bay;
    private String subBay;

    @FXML
    private void addLocationToDB(ActionEvent event) {

        aisle = aisleNumTF.getText();
        bay = bayNumTF.getText();
        subBay = subBayTF.getText();

        if (dbm.locationExists(Integer.parseInt(aisle), Integer.parseInt(bay), Integer.parseInt(subBay))) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, location exists in database");
            a.showAndWait();

        } else {

            if (dbm.insertIntoAisleBaySubBay(Integer.parseInt(aisle), Integer.parseInt(bay), Integer.parseInt(subBay))) {

                Alert a1 = new Alert(Alert.AlertType.INFORMATION);
                a1.setTitle("Success!");
                a1.setHeaderText("Location Added Successfully");
                a1.showAndWait();

                arr = dbm.loadLocations();

                ObservableList<String> allLocs = FXCollections.observableArrayList(arr);

                allLocsTable.setItems(allLocs);

            } else {

                Alert a2 = new Alert(Alert.AlertType.ERROR);
                a2.setTitle("Error");
                a2.setHeaderText("Error Inserting into Database");
                a2.showAndWait();

            }

        }

    }

}
