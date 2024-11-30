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
 * deleteSKU controllers controls the delete product page, will remove a given
 * SKU from the database and all associated tables
 *
 * @author nicka
 */
public class DeleteSKUController {

    //FXML controllers
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

    private DatabaseManager dbm = new DatabaseManager();//database access instance

    private List<Product> allProds = new ArrayList<>();//list for holding all products into the tableview

    private String SKUSelected;//used for input gathering

    /**
     * initialize will run on opening the delete sku controller, it will
     * initialize the table view containing all of the SKUs.
     */
    public void initialize() {

        //initialize the columns in the table view
        SKUCol.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        ProdCol.setCellValueFactory(new PropertyValueFactory<>("ProductTitle"));
        DeptCol.setCellValueFactory(new PropertyValueFactory<>("Department"));
        StockCol.setCellValueFactory(new PropertyValueFactory<>("Stock"));

        allProds = dbm.loadProducts();//get all the SKUs from the database

        ObservableList<Product> prodList = FXCollections.observableArrayList(allProds);//create an observable array list 

        SKUView.setItems(prodList);//load the observable array list into the tableview

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
     * removeSKU will remove the SKU from the database and associated tables 
     * @param event
     */
    @FXML
    private void removeSKU(ActionEvent event) {

        //first create an alert prompting the user if they want to delete the SKU
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Remove Action");
        a.setHeaderText("Are you sure you want to remove this SKU?");
        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        //if the user selects yes, delete the SKU
        if (result.isPresent() && result.get() == byes) {

            //attempt to remove the sku from the database, if successful, throw an alert
            if (dbm.removeSKU(SKUSelected)) {
                Alert removed = new Alert(Alert.AlertType.INFORMATION);
                removed.setTitle("Success");
                removed.setHeaderText("SKU Removed Successfully!");
                removed.showAndWait();

                //refresh the list upon deletion
                allProds = dbm.loadProducts();
                ObservableList<Product> prodList = FXCollections.observableArrayList(allProds);

                SKUView.setItems(prodList);

            }
            //if the user selects no, just close the alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

    /**
     * getClickedSKU will get and set the clicked on SKU from the SKU tableview
     * @param event 
     */
    @FXML
    private void getClickedSKU(MouseEvent event) {

        //double click required to select a SKU
        if (event.getClickCount() == 2) {

            Product selected = SKUView.getSelectionModel().getSelectedItem();//get the selected product

            SKUSelected = selected.getSKU();//set the selectedSKU for later use
            selectedSKUField.setText(SKUSelected);//set the label so the user sees the SKU they clicked on

        }

    }

}
