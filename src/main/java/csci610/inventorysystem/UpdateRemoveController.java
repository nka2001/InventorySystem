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
 *
 * @author nicka
 */
public class UpdateRemoveController {

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

    private Map<String, String> allPallets = new HashMap<>();

    private DatabaseManager dbm = new DatabaseManager();

    private int selectedPID;
    @FXML
    private Button removeButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button addRemoveButton;

    private SessionManager sm = SessionManager.getInstance();

    public void initialize() {

        PalletIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allPallets = dbm.getAllPallets();

        ObservableList<Map.Entry<String, String>> palletList = FXCollections.observableArrayList(allPallets.entrySet());

        allPalletsTable.setItems(palletList);

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
    private void removeEntirePallet(ActionEvent event) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Delete Action");
        a.setHeaderText("Are you sure you want to delete this pallet?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            if (dbm.deletePallet(selectedPID)) {

                Alert a2 = new Alert(Alert.AlertType.INFORMATION);
                a2.setTitle("Success!");
                a2.setHeaderText("Successfully removed pallet");
                a2.showAndWait();

                String location = sm.getLocationID();
                String[] sLocs = location.split("-");

                int Aisle = Integer.parseInt(sLocs[0]);
                int Bay = Integer.parseInt(sLocs[1]);
                int subBay = Integer.parseInt(sLocs[2]);

                dbm.setLocationFree(Aisle, Bay, subBay);

                allPallets = dbm.getAllPallets();

                ObservableList<Map.Entry<String, String>> palletList = FXCollections.observableArrayList(allPallets.entrySet());

                allPalletsTable.setItems(palletList);

            }

        } else if (result.get() == bno) {

            a.close();

        }

    }

    @FXML
    private void updateLocation(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateLocation.fxml"));
            Parent root = loader.load();

            Stage s = new Stage();
            s.setTitle("Update Pallet");
            s.setScene(new Scene(root));

            removeButton.setDisable(true);
            updateButton.setDisable(true);
            addRemoveButton.setDisable(true);
            allPalletsTable.setDisable(true);
            backButton.setDisable(true);

            s.setOnHidden(e -> {
                removeButton.setDisable(false);
                updateButton.setDisable(false);
                addRemoveButton.setDisable(false);
                allPalletsTable.setDisable(false);
                backButton.setDisable(false);

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

    @FXML
    private void AddRemoveSKUs(ActionEvent event) {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdatePalletSKU.fxml"));
            Parent root = loader.load();

            Stage s = new Stage();
            s.setTitle("Update Pallet");
            s.setScene(new Scene(root));

            removeButton.setDisable(true);
            updateButton.setDisable(true);
            addRemoveButton.setDisable(true);
            allPalletsTable.setDisable(true);
            backButton.setDisable(true);

            s.setOnHidden(e -> {
                removeButton.setDisable(false);
                updateButton.setDisable(false);
                addRemoveButton.setDisable(false);
                allPalletsTable.setDisable(false);
                backButton.setDisable(false);

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

    @FXML
    private void getClickedPallet(MouseEvent event) {

        if (event.getClickCount() == 2) {

            Map.Entry<String, String> selectedPallet = allPalletsTable.getSelectionModel().getSelectedItem();

            if (selectedPallet != null) {

                removeButton.setDisable(false);
                updateButton.setDisable(false);
                addRemoveButton.setDisable(false);

                palledIDLabel.setText(selectedPallet.getKey());
                selectedPID = Integer.parseInt(selectedPallet.getKey());
                sm.setPalletID(selectedPID);
                sm.setLocationID(selectedPallet.getValue());

            }

        }

    }

}
