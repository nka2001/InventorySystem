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
 *
 * @author nicka
 */
public class OrderManagerController {

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

    private DatabaseManager dbm = new DatabaseManager();

    private Map<String, String> allSKUs;

    private Map<String, String> orderSKUs = new HashMap<>();

    private Map<String, String> allOrders;

    private String selectedSKUs;
    @FXML
    private TextField DeliveryDateTF;
    @FXML
    private TextArea orderNotes;

    private String selectedToRemove;
    @FXML
    private Button assignOrderButton;

    public void initialize() {

        availableSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        prodTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        OrderSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityOfSKU.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        orderIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        NotesCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        allSKUs = dbm.getAllSKUs();

        ObservableList<Map.Entry<String, String>> SKUList = FXCollections.observableArrayList(allSKUs.entrySet());

        availableSKUTable.setItems(SKUList);

        allOrders = dbm.getAllOrders();

        ObservableList<Map.Entry<String, String>> OrderList = FXCollections.observableArrayList(allOrders.entrySet());

        RemoveOrdersTable.setItems(OrderList);

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
    private void addItemToPallet(ActionEvent event) {

        if (quantityTextField.getText().equals("")) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a quantity for the SKU being added");
            a.showAndWait();

        } else {

            orderSKUs.put(selectedSKUs, quantityTextField.getText());

            System.out.println(orderSKUs.get(selectedSKUs));

            ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(orderSKUs.entrySet());

            OrderTagTable.setItems(addedList);

        }
    }

    @FXML
    private void removeItemFromPallet(ActionEvent event) {

        orderSKUs.remove(selectedSKUs);

        ObservableList<Map.Entry<String, String>> addedList = FXCollections.observableArrayList(orderSKUs.entrySet());

        OrderTagTable.setItems(addedList);

    }

    @FXML
    private void availableSKUsTable(MouseEvent event) {

        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = availableSKUTable.getSelectionModel().getSelectedItem();

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();

            }

        }

    }

    @FXML
    private void CreateOrder(ActionEvent event) throws ParseException {

        if (OrderTagTable.getItems().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, no products selected for order");
            a.showAndWait();
        } else {

            if (OrderNumberTF.getText().equalsIgnoreCase("")) {

                Alert a1 = new Alert(Alert.AlertType.ERROR);
                a1.setTitle("Error");
                a1.setHeaderText("Error, no order number found, please enter an order number");
                a1.showAndWait();

            } else {

                String ActualOrderNumber = "W" + OrderNumberTF.getText();

                if (dbm.orderExists(ActualOrderNumber)) {//write method that checks if order num exists

                    Alert a2 = new Alert(Alert.AlertType.ERROR);
                    a2.setTitle("Error");
                    a2.setHeaderText("Error, order number exists in database, please enter a valid order number");
                    a2.showAndWait();

                    OrderNumberTF.clear();

                } else {

                    //first insert is Orders table
                    //ActualOrderNumber for OrderID
                    String InOrderNotes = orderNotes.getText();
                    String deliveryDate = DeliveryDateTF.getText();
                    //order date will be current system date, handled in DBM
                    //packed is always false, handled in DBM
                    //SHipping status is always pending, handled in DBM

                    if (dbm.insertIntoOrdersTable(ActualOrderNumber, InOrderNotes, deliveryDate)) {

                        if (dbm.insertSKUsIntoSKUsOnOrdersTable(orderSKUs, ActualOrderNumber)) {

                            Alert a = new Alert(Alert.AlertType.INFORMATION);
                            a.setTitle("Success!");
                            a.setHeaderText("Order Added Successfully!");
                            a.showAndWait();

                            orderNotes.clear();
                            DeliveryDateTF.clear();
                            OrderNumberTF.clear();

                            allOrders = dbm.getAllOrders();

                            ObservableList<Map.Entry<String, String>> OrderList = FXCollections.observableArrayList(allOrders.entrySet());

                            RemoveOrdersTable.setItems(OrderList);

                        }

                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Error");
                        a.setHeaderText("Error inserting into database please contact admin");
                        a.showAndWait();

                    }

                }

            }

        }

    }

    @FXML
    private void removeOrder(ActionEvent event) {

        if (dbm.removeOrder(selectedToRemove)) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success!");
            a.setHeaderText("Order Removed Successfully!");
            a.showAndWait();

            allOrders = dbm.getAllOrders();

            ObservableList<Map.Entry<String, String>> OrderList = FXCollections.observableArrayList(allOrders.entrySet());

            RemoveOrdersTable.setItems(OrderList);
        }
    }

    @FXML
    private void OrderTable(MouseEvent event) {

        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = OrderTagTable.getSelectionModel().getSelectedItem();

            if (selectedSKU != null) {

                selectedSKUs = selectedSKU.getKey();

            }

        }

    }

    @FXML
    private void removeOrderTableClick(MouseEvent event) {

        if (event.getClickCount() == 1) {

            Map.Entry<String, String> selectedSKU = RemoveOrdersTable.getSelectionModel().getSelectedItem();

            if (selectedSKU != null) {

                selectedToRemove = selectedSKU.getKey();
                selectedOrder.setText(selectedToRemove);

            }

        }

    }

    @FXML
    private void assignOrder(ActionEvent event) throws IOException {
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Cancel Action");
        a.setHeaderText("Are you sure you want to switch to assign orders?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            App.setRoot("AssignOrders");

        } else if (result.get() == bno) {

            a.close();

        }
        
        
        
    }

}
