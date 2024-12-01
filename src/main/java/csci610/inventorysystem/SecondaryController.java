package csci610.inventorysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
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

/**
 * secondary controller is the dashboard, the dashboard is the core page of the
 * application, allowing users to do tasks
 *
 * admins have a higher level of access
 *
 * @author nicka
 */
public class SecondaryController {

    //FXML controls
    private SessionManager sm = SessionManager.getInstance();//session manager for accessing data across all classes
    @FXML
    private Button createUser;
    private Button viewUser;
    @FXML
    private Button resetPassword;
    @FXML
    private Label welcomeLabel;

    private DatabaseManager dbm = new DatabaseManager();//database instance
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
    @FXML
    private TableView<Map.Entry<String, String>> departmentsTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> deparetmentIDcol;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> departmentNamecol;

    private Map<String, String> allDepts = new HashMap<>();

    /**
     * initialize will run opening the dashboard, all piecharts, tableviews, and
     * other charts are initialized and filled with data gathered from the
     * database.
     */
    public void initialize() {

        //initialize the search function
        palletIDCol.setCellValueFactory(new PropertyValueFactory<>("PalletID"));
        QuantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        LocationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));

        //initialize the order table
        orderCol.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
        assignedCol.setCellValueFactory(new PropertyValueFactory<>("Username"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("OrderDate"));

        //initialize the department side table
        deparetmentIDcol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        departmentNamecol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        //get and fill the department sub table
        allDepts = dbm.getAllDepts();
        ObservableList<Map.Entry<String, String>> allDeptartments = FXCollections.observableArrayList(allDepts.entrySet());
        departmentsTable.setItems(allDeptartments);

        welcomeLabel.setText("Welcome, " + dbm.getDisplayName(sm.getUser()));//get the display name for the user logging in
        loadPalletPie();//load pie chart
        loadDeptBar();//load bar chart
        loadOrdersTable();//load orders table

        //if the user logging is in an administrator, then enable all admin level functions
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

    private List<Orders> allOrders = new ArrayList<>();//list to hold all orders

    /**
     * loadOrdersTable will load the contents of orders from the database and
     * then add it to the orders tableview.
     */
    private void loadOrdersTable() {

        //load orders data into the orders tableview
        allOrders = dbm.loadOrders();

        ObservableList<Orders> allOrders = FXCollections.observableArrayList(this.allOrders);

        OrdersChart.setItems(allOrders);

    }

    /**
     * loadPalletPie will load the available and used rack space (used spaces /
     * total spaces).
     */
    private void loadPalletPie() {

        int truecount;
        int falsecount;

        ArrayList<Integer> arr;

        arr = dbm.returnTFPallets();//get all used and free spots from the database

        truecount = arr.get(0);//0 are spaces used

        falsecount = arr.get(1);//1 are spaces free

        //create and set the Pie chart
        ObservableList<PieChart.Data> pcd = FXCollections.observableArrayList(
                new PieChart.Data("Spaces Used: " + truecount, truecount),
                new PieChart.Data("Spaces Free: " + falsecount, falsecount)
        );

        rackSpacePie.setData(pcd);//apply the pie chart and set it to be viewed

        //old CSS used for testing
        // pcd.get(0).getNode().setStyle("-fx-pie-color: #ff6347;");
        //pcd.get(1).getNode().setStyle("-fx-pie-color: #4682b4;");
        //used to ensure the legend matches the colors on the pie chart
        for (int i = 0; i < pcd.size(); i++) {
            PieChart.Data d = pcd.get(i);

            int j = i;
            //go through each node (2) and apply css coloring to the different pie slices
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

    /**
     * logout will simply log the user out of the system
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void logout(ActionEvent event) throws IOException {

        String username = sm.getUser();//get the user attempting to logout

        //throw a choice alert, prompting the user if they want to sign out
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Logout Confirmation");
        a.setHeaderText("Logout?");
        a.setContentText("Are you sure you want to logout?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        //if yes, remove the user from the user manager (online users) and then move them back to the login screen
        if (result.isPresent() && result.get() == byes) {

            UserManager.getInstance().removeUser(username);
            App.setRoot("primary");

            //if no, just close the alert
        } else if (result.get() == bno) {

            a.close();

        }

    }

    private Map<Integer, Integer> SKUMap = new HashMap<>();//SKU map for bar chart

    /**
     * loadDeptBar will take all of the SKU and department ID and load them into a barchart.
     */
    private void loadDeptBar() {

        SKUMap = dbm.getSKUsByDept();//get all SKUs along with their department

        //set axis labels
        xAxis.setLabel("Department");
        yAxis.setLabel("Number of SKUs");

        //create the series to be added to the barchart
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("SKUs Per Department");

        //for each entry in the sku map, get and set the data into the bar chart
        for (Map.Entry<Integer, Integer> entry : SKUMap.entrySet()) {

            Integer deptID = entry.getKey();
            Integer SKUCount = entry.getValue();

            dataSeries.getData().add(new XYChart.Data<>("Dept. " + deptID, SKUCount));

        }

        SKUbyDeptBar.getData().add(dataSeries);//then make the barchart visible

    }

    /**
     * moves to create new user page (admin only)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void createNewUser(ActionEvent event) throws IOException {

        App.setRoot("CreateUser");

    }

    /**
     * moves to reset user password page (admin only)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void resetUserPassword(ActionEvent event) throws IOException {

        App.setRoot("ResetPassword");

    }

    /**
     * move to remove user from system page (admin only)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void removeUserFromSystem(ActionEvent event) throws IOException {

        App.setRoot("RemoveUser");
    }

    /**
     * moves to add SKU page (admin only)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void addSKU(ActionEvent event) throws IOException {

        App.setRoot("AddSKU");

    }
    
    /**
     * moves to remove SKU page (admin only)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void removeSKU(ActionEvent event) throws IOException {

        App.setRoot("DeleteSKU");

    }

    /**
     * moves to create pallet screen (all users)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void openCreatePallet(ActionEvent event) throws IOException {

        App.setRoot("CreatePallet");

    }

    /**
     * moves to add location page (admin only)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void addLocation(ActionEvent event) throws IOException {

        App.setRoot("AddLocation");

    }

    /**
     * move to remove location page (admin only)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void removeLocation(ActionEvent event) throws IOException {

        App.setRoot("RemoveLocation");

    }
    
    /**
     * move to update pallet page (all users)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void openUpdatePallet(ActionEvent event) throws IOException {

        App.setRoot("UpdateRemovePallet");

    }

    private List<Pallet> allPalletsWithSKU = new ArrayList<>();//list contating all pallets that have a specified SKU on it

    /**
     * search for pallet will search for all pallets that contain a specified sku and display them in a tableview
     * @param event 
     */
    @FXML
    private void searchForPallet(ActionEvent event) {

        String SKU = SearchSKU.getText();//get the SKU to search

        //if there is nothing in the sku search field, throw an error
        if (SKU.equalsIgnoreCase("")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error, please enter a SKU to search");
            a.showAndWait();
            
        } else {//otherwise continue with the search

            if (dbm.findSKU(SKU)) {//attempt to locate the SKU
                
                //load the allPalletsWithSKU table with the results
                allPalletsWithSKU = dbm.loadPalletsBySKU(SKU);

                ObservableList<Pallet> palletList = FXCollections.observableArrayList(allPalletsWithSKU);

                searchPalletTable.setItems(palletList);

            } else {//if the SKU does not exist, throw an error

                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error, SKU does not exist or is not on any pallets");
                a.showAndWait();

            }
        }

    }

    /**
     * legacy
     * @param event 
     */
    @FXML
    private void viewTrucks(ActionEvent event) {
    }

    /**
     * move to order manager page (all users)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void addOrder(ActionEvent event) throws IOException {

        App.setRoot("OrderManager");

    }

    /**
     * move to truck manager (all users)
     * @param event
     * @throws IOException 
     */
    @FXML
    private void createTruck(ActionEvent event) throws IOException {

        App.setRoot("TruckManager");

    }

}
