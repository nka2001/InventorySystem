package csci610.inventorysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class SecondaryController {

    private SessionManager sm = SessionManager.getInstance();
    @FXML
    private Button createUser;
    private Button viewUser;
    @FXML
    private Button resetPassword;
    @FXML
    private Label welcomeLabel;

    private DatabaseManager dbm = new DatabaseManager();
    @FXML
    private Button logoutButton;
    @FXML
    private PieChart rackSpacePie;
    @FXML
    private BarChart<String, Number> SKUbyDeptBar;
    @FXML
    private TableView<Orders> OrdersChart;
    @FXML
    private Button removeUser;
    @FXML
    private Button addSKUButton;
    @FXML
    private Button removeSKUButton;
    @FXML
    private Button createPalletbutton;
    @FXML
    private Button addLocationButton;
    @FXML
    private Button removeLocationButton;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private Button updatePallet;
    @FXML
    private Button performSearch;
    @FXML
    private TableView<Pallet> searchPalletTable;
    @FXML
    private TableColumn<Pallet, Integer> palletIDCol;
    @FXML
    private TableColumn<Pallet, Integer> QuantityCol;
    @FXML
    private TableColumn<Pallet, String> LocationCol;
    @FXML
    private TextField SearchSKU;
    @FXML
    private Button viewTrucks;
    @FXML
    private Button addOrder;
    @FXML
    private Button createTruck;
    @FXML
    private TableColumn<Orders, String> orderCol;
    @FXML
    private TableColumn<Orders, String> assignedCol;
    @FXML
    private TableColumn<Orders, String> orderDateCol;

    public void initialize() {

        palletIDCol.setCellValueFactory(new PropertyValueFactory<>("PalletID"));
        QuantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        LocationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));

        orderCol.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
        assignedCol.setCellValueFactory(new PropertyValueFactory<>("Username"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("OrderDate"));

        welcomeLabel.setText("Welcome, " + sm.getUser());
        loadPalletPie();
        loadDeptBar();
        loadOrdersTable();
        if (dbm.isAdministrator(sm.getUser())) {

            createUser.setDisable(false);
            resetPassword.setDisable(false);
            removeUser.setDisable(false);
            addSKUButton.setDisable(false);
            removeSKUButton.setDisable(false);
            addLocationButton.setDisable(false);
            removeLocationButton.setDisable(false);
            addOrder.setDisable(false);
            createTruck.setDisable(false);
        }

    }

    private List<Orders> allOrders = new ArrayList<>();

    private void loadOrdersTable() {

        allOrders = dbm.loadOrders();

        ObservableList<Orders> allOrders = FXCollections.observableArrayList(this.allOrders);

        OrdersChart.setItems(allOrders);

    }

    private void loadPalletPie() {

        int truecount;
        int falsecount;

        ArrayList<Integer> arr;

        arr = dbm.returnTFPallets();

        truecount = arr.get(0);

        falsecount = arr.get(1);

        ObservableList<PieChart.Data> pcd = FXCollections.observableArrayList(
                new PieChart.Data("Spaces Used: " + truecount, truecount),
                new PieChart.Data("Spaces Free: " + falsecount, falsecount)
        );

        rackSpacePie.setData(pcd);

       // pcd.get(0).getNode().setStyle("-fx-pie-color: #ff6347;");
        //pcd.get(1).getNode().setStyle("-fx-pie-color: #4682b4;");

        for (int i = 0; i < pcd.size(); i++) {
            PieChart.Data d = pcd.get(i);

            int j = i;

            d.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    if (j == 0) {
                        newNode.setStyle("-fx-pie-color: #ff6347;");

                    } else if (j == 1) {
                        newNode.setStyle("-fx-pie-color: #4682b4;");
                    }
                }

            });
        }

    }

    @FXML
    private void logout(ActionEvent event) throws IOException {

        String username = sm.getUser();
        System.out.println(username);

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Logout Confirmation");
        a.setHeaderText("Logout?");
        a.setContentText("Are you sure you want to logout?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            UserManager.getInstance().removeUser(username);
            App.setRoot("primary");

        } else if (result.get() == bno) {

            a.close();

        }

    }

    private Map<Integer, Integer> SKUMap = new HashMap<>();

    private void loadDeptBar() {

        SKUMap = dbm.getSKUsByDept();

        xAxis.setLabel("Department");
        yAxis.setLabel("Number of SKUs");

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("SKUs Per Department");

        for (Map.Entry<Integer, Integer> entry : SKUMap.entrySet()) {

            Integer deptID = entry.getKey();
            Integer SKUCount = entry.getValue();

            dataSeries.getData().add(new XYChart.Data<>("Dept. " + deptID, SKUCount));

        }

        SKUbyDeptBar.getData().add(dataSeries);

    }

    @FXML
    private void createNewUser(ActionEvent event) throws IOException {

        App.setRoot("CreateUser");

    }

    @FXML
    private void resetUserPassword(ActionEvent event) throws IOException {

        App.setRoot("ResetPassword");

    }

    @FXML
    private void removeUserFromSystem(ActionEvent event) throws IOException {

        App.setRoot("RemoveUser");
    }

    @FXML
    private void addSKU(ActionEvent event) throws IOException {

        App.setRoot("AddSKU");

    }

    @FXML
    private void removeSKU(ActionEvent event) throws IOException {

        App.setRoot("DeleteSKU");

    }

    @FXML
    private void openCreatePallet(ActionEvent event) throws IOException {

        App.setRoot("CreatePallet");

    }

    @FXML
    private void addLocation(ActionEvent event) throws IOException {

        App.setRoot("AddLocation");

    }

    @FXML
    private void removeLocation(ActionEvent event) throws IOException {

        App.setRoot("RemoveLocation");

    }

    @FXML
    private void openUpdatePallet(ActionEvent event) throws IOException {

        App.setRoot("UpdateRemovePallet");

    }

    private List<Pallet> allPalletsWithSKU = new ArrayList<>();

    @FXML
    private void searchForPallet(ActionEvent event) {

        String SKU = SearchSKU.getText();

        if (SKU.equalsIgnoreCase("")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a SKU to search");
            a.showAndWait();
        } else {

            if (dbm.findSKU(SKU)) {

                allPalletsWithSKU = dbm.loadPalletsBySKU(SKU);

                ObservableList<Pallet> palletList = FXCollections.observableArrayList(allPalletsWithSKU);

                searchPalletTable.setItems(palletList);

            } else {

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error, SKU does not exist");
                a.showAndWait();

            }
        }

    }

    @FXML
    private void viewTrucks(ActionEvent event) {
    }

    @FXML
    private void addOrder(ActionEvent event) throws IOException {

        App.setRoot("OrderManager");

    }

    @FXML
    private void createTruck(ActionEvent event) throws IOException {

        App.setRoot("TruckManager");

    }

}
