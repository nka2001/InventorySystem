package csci610.inventorysystem;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * ordermanagercontroller controls the order manager page, creating, removing,
 * and assigning orders
 *
 * @author nicka
 */
public class OrderManagerController {

    //FXML controls
    @FXML
    private Button backButton;
    @FXML
    private Button addItem;
    @FXML
    private Button removeItem;
    @FXML
    private TableView<Map.Entry<String, String>> OrderTagTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> OrderSKU;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> QuantityOfSKU;
    @FXML
    private TableView<Map.Entry<String, String>> availableSKUTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> availableSKU;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> prodTitle;
    @FXML
    private Button CreateOrder;
    @FXML
    private TextField quantityTextField;
    @FXML
    private TableView<Map.Entry<String, String>> RemoveOrdersTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> orderIDCol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> NotesCol;
    @FXML
    private Label selectedOrder;
    @FXML
    private Button removeOrderButton;
    @FXML
    private TextField OrderNumberTF;

    private DatabaseManager dbm = new DatabaseManager();//database access instance

    private Map<String, String> allSKUs;//all SKUs in the database

    private Map<String, String> orderSKUs = new HashMap<>();//selected SKUs to be apart of the order

    private Map<String, String> allOrders;//lists all orders for assigning the order

    private String selectedSKUs;
    @FXML
    private TextField DeliveryDateTF;
    @FXML
    private TextArea orderNotes;

    private String selectedToRemove;
    @FXML
    private Button assignOrderButton;

    /**
     * initialize will run on opening the order manager page, will load tables
     * and initialize columns in the various tables.
     */
    public void initialize() {

        //initialize columns in the allSKUs table
        availableSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        prodTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //initialize columns in the orderSKUs table
        OrderSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityOfSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //initialize columns in the orders table
        orderIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        NotesCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allSKUs = dbm.getAllSKUs();//get all SKUs from the databasse

        ObservableList<Map.Entry<String, String>> SKUList = FXCollections.observableArrayList(allSKUs.entrySet());

        availableSKUTable.setItems(SKUList);//load the observable arraylist with the data

        allOrders = dbm.getAllOrders();//get all the orders from the database

        ObservableList<Map.Entry<String, String>> OrderList = FXCollections.observableArrayList(allOrders.entrySet());

        RemoveOrdersTable.setItems(OrderList);//load the observable arraylist with the data

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
     * addItemToPallet will add an item to the order being created
     *
     * @param event
     */
    @FXML
    private void addItemToPallet(ActionEvent event) {

        //first check if the quantity text field is empty, if it is, throw an alert
        if (quantityTextField.getText().equals("")) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a quantity for the SKU being added");
            a.showAndWait();

        } else {//otherwise add the selected SKU into the orderSKUs map, 

            orderSKUs.put(selectedSKUs, quantityTextField.getText());

            //refresh the list
            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(orderSKUs.entrySet());

            OrderTagTable.setItems(addedList);

        }
    }

    /**
     * removeItemFromPallet will remove a selected SKU from the order
     *
     * @param event
     */
    @FXML
    private void removeItemFromPallet(ActionEvent event) {

        //remove the order from the order table, then refresh the order table
        orderSKUs.remove(selectedSKUs);

        ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(orderSKUs.entrySet());

        OrderTagTable.setItems(addedList);

    }

    /**
     * available SKUs table will get the value from the allSKU table the user
     * clicks on
     *
     * @param event
     */
    @FXML
    private void availableSKUsTable(MouseEvent event) {

        //single click required to select a SKU
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = availableSKUTable.getSelectionModel().getSelectedItem();//get the actual selected item

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//then set the sku for later insertion/deletion

            }

        }

    }

    /**
     * create order will attempt to insert the created order into the database
     *
     * @param event
     * @throws ParseException
     */
    @FXML
    private void CreateOrder(ActionEvent event) throws ParseException {

        //first, check if the order SKU table is empty, if it is, throw an error
        if (OrderTagTable.getItems().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, no products selected for order");
            a.showAndWait();
        } else {//otherwise begin the insert

            //if the ordernumber text field is empty, then throw an error
            if (OrderNumberTF.getText().equalsIgnoreCase("")) {

                Alert a1 = new Alert(Alert.AlertType.ERROR);
                a1.setTitle("Error");
                a1.setHeaderText("Error, no order number found, please enter an order number");
                a1.showAndWait();

            } else {//otherwise continue the insert

                //prepend a W to the order, all orders start with W
                String ActualOrderNumber = "W" + OrderNumberTF.getText();

                //check if the order exists, if it does, throw an error
                if (dbm.orderExists(ActualOrderNumber)) {

                    Alert a2 = new Alert(Alert.AlertType.ERROR);
                    a2.setTitle("Error");
                    a2.setHeaderText("Error, order number exists in database, please enter a valid order number");
                    a2.showAndWait();

                    OrderNumberTF.clear();//clear the order number for re-entry

                } else {

                    //first insert is Orders table
                    //ActualOrderNumber for OrderID
                    String InOrderNotes = orderNotes.getText();
                    String deliveryDate = DeliveryDateTF.getText();
                    //order date will be current system date, handled in DBM
                    //packed is always false, handled in DBM
                    //SHipping status is always pending, handled in DBM

                    //if the insert into the orders succeds proceed to insert into the products on orders table, throw an error otherwise
                    if (dbm.insertIntoOrdersTable(ActualOrderNumber, InOrderNotes, deliveryDate)) {

                        //attempt to insert into the SKUs on orders table, throw alert if successful
                        if (dbm.insertSKUsIntoSKUsOnOrdersTable(orderSKUs, ActualOrderNumber)) {

                            Alert a = new Alert(Alert.AlertType.INFORMATION);
                            a.setTitle("Success!");
                            a.setHeaderText("Order Added Successfully!");
                            a.showAndWait();

                            //clear all fields and refresh orders table(s)
                            orderNotes.clear();
                            DeliveryDateTF.clear();
                            OrderNumberTF.clear();

                            allOrders = dbm.getAllOrders();

                            ObservableList<Map.Entry<String, String>> OrderList = FXCollections.observableArrayList(allOrders.entrySet());

                            RemoveOrdersTable.setItems(OrderList);

                        }

                    } else {//throw an alert on unsucessful insertion
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Error");
                        a.setHeaderText("Error inserting into database please contact admin");
                        a.showAndWait();

                    }

                }

            }

        }

    }

    /**
     * removeOrder will remove a selected order from the database
     *
     * deletion anomoly is handled by database manager
     *
     * @param event
     */
    @FXML
    private void removeOrder(ActionEvent event) {

        //attempt to remove the order based on the selected order, throw alert if successful
        if (dbm.removeOrder(selectedToRemove)) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success!");
            a.setHeaderText("Order Removed Successfully!");
            a.showAndWait();

            //then refresh the all orders table
            allOrders = dbm.getAllOrders();

            ObservableList<Map.Entry<String, String>> OrderList = FXCollections.observableArrayList(allOrders.entrySet());

            RemoveOrdersTable.setItems(OrderList);
        }
    }

    /**
     * orderstable will get the order the user clicks on and store it for future
     * use
     *
     * @param event
     */
    @FXML
    private void OrderTable(MouseEvent event) {

        //single click required to set the selected order
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = OrderTagTable.getSelectionModel().getSelectedItem();//get the selected item

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();//set the selectedSKU for future insertion/deletion

            }

        }

    }

    /**
     * removeOrderTableClick will get the order the user clicks on and store it
     * for future use
     *
     * @param event
     */
    @FXML
    private void removeOrderTableClick(MouseEvent event) {

        //single click required to select the order
        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = RemoveOrdersTable.getSelectionModel().getSelectedItem();//get the selected item

            if (selectedSKU != null) {

                selectedToRemove = selectedSKU.getKey();//store the selected order for future insertion/deletion
                selectedOrder.setText(selectedToRemove);//set the label so the user sees the selected order

            }

        }

    }

    /**
     * assignOrder will assign an order to a selected user
     * @param event
     * @throws IOException
     */
    @FXML
    private void assignOrder(ActionEvent event) throws IOException {

        //throw a confirmation alert prompting the user to confirm their choice
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Cancel Action");
        a.setHeaderText("Are you sure you want to switch to assign orders?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        //if the user selects yes, then open the assign orders page 
        if (result.isPresent() && result.get() == byes) {

            App.setRoot("AssignOrders");

        //if the user selects no, then close the alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

}
