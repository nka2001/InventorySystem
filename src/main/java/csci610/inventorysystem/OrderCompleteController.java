package csci610.inventorysystem;

import java.io.IOException;
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
import javafx.stage.Stage;

/**
 *
 * @author nicka
 */
public class OrderCompleteController {

    private Stage s;
    @FXML
    private Button backButton;
    @FXML
    private TableView<Map.Entry<String, String>> SKUsOnOrdersTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> SKUCol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> QuantityCol;
    @FXML
    private Label orderIDLabel;
    @FXML
    private Button completeOrder;

    public void setStage(Stage s) {
        this.s = s;
    }

    private DatabaseManager dbm = new DatabaseManager();
    private SessionManager sm = SessionManager.getInstance();

    private Map<String, String> allSKUsOnOrder = new HashMap<>();

    public void initialize() {

        SKUCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        QuantityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        orderIDLabel.setText(sm.getOrder());

        allSKUsOnOrder = dbm.getAllSKUsOnOrders(sm.getOrder());
        ObservableList<Map.Entry<String, String>> order = FXCollections.observableArrayList(allSKUsOnOrder.entrySet());
        SKUsOnOrdersTable.setItems(order);

    }

    /**
     * goback method will allow a user to return to the previous page
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void goBack(ActionEvent event) throws IOException {
        if(s != null){
            s.close();
        }

    }

    @FXML
    private void completeSelectedOrder(ActionEvent event) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm Action");
        a.setHeaderText("Are you sure you want to Complete this order?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        //if yes, complete the order
        if (result.isPresent() && result.get() == byes) {

            if (dbm.completeOrder(sm.getOrder())) {

                Alert a1 = new Alert(Alert.AlertType.INFORMATION);
                a1.setTitle("Success!");
                a1.setHeaderText("Success! Order completed");
                a1.showAndWait();
                
                if(s != null){
                    s.close();
                }

            }

            //if no, just close the alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

}
