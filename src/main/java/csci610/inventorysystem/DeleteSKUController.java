package csci610.inventorysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author nicka
 */
public class DeleteSKUController {

    @FXML
    private Button backButton;
    @FXML
    private TableView<Product> SKUView;
    @FXML
    private TableColumn<Product, String> SKUCol;
    @FXML
    private TableColumn<Product, String> ProdCol;
    @FXML
    private TableColumn<Product, Integer> DeptCol;
    @FXML
    private TableColumn<Product, Integer> StockCol;
    @FXML
    private Label selectedSKUField;
    @FXML
    private Button removeButton;

    private DatabaseManager dbm = new DatabaseManager();

    private List<Product> allProds = new ArrayList<>();

    private String SKUSelected;

    public void initialize() {

        SKUCol.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        ProdCol.setCellValueFactory(new PropertyValueFactory<>("ProductTitle"));
        DeptCol.setCellValueFactory(new PropertyValueFactory<>("Department"));
        StockCol.setCellValueFactory(new PropertyValueFactory<>("Stock"));

        allProds = dbm.loadProducts();

        ObservableList<Product> prodList = FXCollections.observableArrayList(allProds);

        SKUView.setItems(prodList);

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
    private void removeSKU(ActionEvent event) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Remove Action");
        a.setHeaderText("Are you sure you want to remove this SKU?");
        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            if (dbm.removeSKU(SKUSelected)) {
                Alert removed = new Alert(Alert.AlertType.INFORMATION);
                removed.setTitle("Success");
                removed.setHeaderText("SKU Removed Successfully!");
                removed.showAndWait();

                allProds = dbm.loadProducts();
                ObservableList<Product> prodList = FXCollections.observableArrayList(allProds);

                SKUView.setItems(prodList);

            }

        } else if (result.get() == bno) {

            a.close();

        }

    }

    @FXML
    private void getClickedSKU(MouseEvent event) {

        if (event.getClickCount() == 2) {

            Product selected = SKUView.getSelectionModel().getSelectedItem();

            SKUSelected = selected.getSKU();
            selectedSKUField.setText(SKUSelected);

        }

    }

}
