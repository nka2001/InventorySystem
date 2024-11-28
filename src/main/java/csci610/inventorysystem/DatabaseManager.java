package csci610.inventorysystem;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicka
 */
public class DatabaseManager {

    public static Connection c;

    public static void connect() {
        try {
            //Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/InventoryManagement", "root", "Som3timesth1ngsn$$dtobeChanged");

        } catch (Exception e) {
            System.out.println("error in static void connect");
            e.printStackTrace();
        }
    }

    public DatabaseManager() {
        connect();
    }

    public boolean init_ConnectionToDB(String username, String password) {
        return init_ConnectToDB(username, password);
    }

    private boolean init_ConnectToDB(String username, String password) {

        try {
            PreparedStatement ps = c.prepareStatement("Select username, User_password, Disabled from users where username = ?");
            ps.setString(1, username.trim());

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return false;
            } else {

                String encryptedPass = rs.getString("User_password");
                boolean isDisabled = rs.getBoolean("Disabled");

                System.out.println("Encrypted Password: " + encryptedPass);
                System.out.println(isDisabled);

                String encryptUserInput = EncryptionManager.encrypt(password);

                if (encryptUserInput.equals(encryptedPass) && !isDisabled) {
                    System.out.println("good password");
                    return true;
                }

            }

        } catch (SQLException e) {
            System.out.println("error in initconnecttoDB");
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(String username, String password, String displayName, String fName, String lName, String DateOfBirth, String Gender, float payRate, boolean isAdmin, String position, boolean isDisabled) throws ParseException {

        String SQL = "insert into Users (username, User_password, Display_Name, First_Name, Last_Name, DateOfBirth, Gender, PayRate, Administrator, Position, Disabled) values (?,?,?,?,?,?,?,?,?,?,?)";
        String encryptedPassword = EncryptionManager.encrypt(password);

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date utilDate = f.parse(DateOfBirth);

        Date SQLDOB = new Date(utilDate.getTime());

        try {
            PreparedStatement ps = c.prepareStatement(SQL);
            ps.setString(1, username);
            ps.setString(2, encryptedPassword);
            ps.setString(3, displayName);
            ps.setString(4, fName);
            ps.setString(5, lName);
            ps.setDate(6, SQLDOB);
            ps.setString(7, Gender);
            ps.setFloat(8, payRate);
            ps.setBoolean(9, isAdmin);
            ps.setString(10, position);
            ps.setBoolean(11, isDisabled);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException ex) {
            System.out.println("error in inserting new user");
            ex.printStackTrace();

        }
        return false;
    }

    public boolean findUser(String username) {

        try {

            PreparedStatement ps = c.prepareStatement("Select username from users where username = ?");
            ps.setString(1, username.trim());

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("error in findUser");
            e.printStackTrace();
        }

        return false;
    }

    public boolean isAdministrator(String username) {

        try {

            PreparedStatement ps = c.prepareStatement("Select Administrator from users where username = ?");
            ps.setString(1, username.trim());

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return false;
            } else {
                System.out.println(rs.getBoolean("Administrator"));
                return rs.getBoolean("Administrator");

            }

        } catch (SQLException e) {
            System.out.println("error in isAdministrator");
            e.printStackTrace();
        }

        return false;

    }

    public ArrayList<Integer> returnTFPallets() {

        ArrayList<Integer> arr = new ArrayList<>();

        String sql = "select sum(case when occupied = true then 1 else 0 end) as  trueCount,"
                + "sum(case when occupied = false then 1 else 0 end) as falseCount from aislebaysubbay";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int trueCount = rs.getInt("trueCount");
                int falseCount = rs.getInt("falseCount");

                arr.add(trueCount);
                arr.add(falseCount);

            }
        } catch (SQLException e) {
            System.out.println("error in returnTFPallets, SQL error");
            e.printStackTrace();
        }
        return arr;

    }

    public Map<String, String> loadUsers() {

        Map<String, String> userMap = new HashMap<>();

        String sql = "select username, Display_Name from users";

        try {

            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                userMap.put(rs.getString("username"), rs.getString("Display_Name"));

            }

        } catch (SQLException e) {
            System.out.println("error in loadUsers");
            e.printStackTrace();
        }

        return userMap;
    }

    public Map<String, String> getAllSKUs() {

        Map<String, String> SKUMap = new HashMap<>();

        String sql = "Select SKU, ProductTitle from Product order by department";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SKUMap.put(rs.getString("SKU"), rs.getString("ProductTitle"));
            }

        } catch (SQLException e) {

            System.out.println("error in getAllSKUs");
            e.printStackTrace();
        }
        return SKUMap;
    }

    public Map<String, String> getAllPalletTagSKUs(int palletID) {

        Map<String, String> palletSKUMap = new HashMap<>();

        String sql = "select SKU, QuantityOfSKU from SKUsOnPallets where PalletID = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, palletID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int quantityAsString = rs.getInt("QuantityOfSKU");

                palletSKUMap.put(rs.getString("SKU"), String.valueOf(quantityAsString));

            }

        } catch (SQLException e) {
            System.out.println("error in getAllPalletTagSKUs");
            e.printStackTrace();
        }

        return palletSKUMap;

    }

    public void removeSKUFromPallet(String SKU) {

        String sql = "delete from SKUsOnPallets where SKU = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in removeSKUfromPallet");
            e.printStackTrace();
        }

    }

    public List<String> loadLocations() {

        List<String> locs = new ArrayList<>();

        String sql = "select AisleNumber, AisleBay, SubBay from AisleBaySubBay";

        String addToList = "";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String aisleNum = String.valueOf(rs.getInt("AisleNumber"));

                String bayNum = String.valueOf(rs.getInt("AisleBay"));

                String SubBay = String.valueOf(rs.getInt("SubBay"));

                addToList = aisleNum + "-" + bayNum + "-" + SubBay;
                locs.add(addToList);
            }

        } catch (SQLException e) {
            System.out.println("error in loadlocations");
            e.printStackTrace();
        }
        return locs;
    }

    public List<Pallet> loadPalletsBySKU(String SKU) {

        List<Pallet> pallets = new ArrayList<>();

        String sql = "select Pallet.PalletID, AisleNumber, AisleBay, SubBay, QuantityOfSKU from Pallet inner join AisleBaySubBay on Pallet.LocationID = AisleBaySubBay.LocationID inner join skusonpallets on Pallet.palletID = skusonpallets.PalletID where SKU = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            ResultSet rs = ps.executeQuery();

            String location;

            while (rs.next()) {

                int palID = rs.getInt("PalletID");
                location = String.valueOf(rs.getInt("AisleNumber")) + "-" + String.valueOf(rs.getInt("AisleBay")) + "-" + String.valueOf(rs.getInt("SubBay"));
                int Quan = rs.getInt("QuantityOfSKU");

                pallets.add(new Pallet(palID, Quan, location));

            }

        } catch (SQLException e) {
            System.out.println("error in loadPalletsBySKU");
            e.printStackTrace();
        }

        return pallets;
    }

    public List<Product> loadProducts() {

        List<Product> prods = new ArrayList<>();

        String sql = "select SKU, ProductTitle, Department, Stock from Product";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String SKU = rs.getString("SKU");
                String ProdTitle = rs.getString("ProductTitle");
                int dept = rs.getInt("Department");
                int stock = rs.getInt("Stock");

                prods.add(new Product(SKU, ProdTitle, dept, stock));
            }

        } catch (SQLException e) {
            System.out.println("error in loadProducts");
            e.printStackTrace();
        }
        return prods;
    }

    public boolean resetPassword(String username, String newPassword) {

        String sql = "update users set User_Password = ? where username = ?";

        String newEncryptedPassword = EncryptionManager.encrypt(newPassword);

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, newEncryptedPassword);
            ps.setString(2, username);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in resetPassword");
            e.printStackTrace();
        }

        return false;
    }

    public boolean makeAdministrator(String username, boolean isAdmin) {

        String sql = "update users set Administrator = ? where username = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setBoolean(1, isAdmin);
            ps.setString(2, username);

            int rows = ps.executeUpdate();
            System.out.println(rows);
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in make administrator");
            e.printStackTrace();
        }

        return false;
    }

    public boolean disableUser(String username, boolean isDisabled) {

        String sql = "update users set Disabled = ? where username = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setBoolean(1, isDisabled);
            ps.setString(2, username);

            int rows = ps.executeUpdate();
            System.out.println(rows);
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in disable user");
            e.printStackTrace();
        }

        return false;
    }

    public boolean getIsAdmin(String username) {

        String sql = "Select Administrator from users where username = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Administrator");
            }

        } catch (SQLException e) {
            System.out.println("error in getIsAdmin");
            e.printStackTrace();
        }

        return false;
    }

    public boolean getIsDisabled(String username) {

        String sql = "select Disabled from users where username = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Disabled");
            }

        } catch (SQLException e) {
            System.out.println("error in getIsDisabled");
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeUser(String username) {

        String sql = "delete from users where username = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in remove user");
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeSKU(String SKU) {

        String sql = "delete from Product where SKU = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in removeSKU");
            e.printStackTrace();

        }
        return false;
    }

    public boolean findSKU(String SKU) {

        String sql = "select SKU from Product where SKU = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("error in find SKU");
            e.printStackTrace();
        }

        return false;
    }

    public boolean addSKU(String newSKU, String newProdTitle, String newProdDesc, float newPri, int stock, int dept) {

        String sql = "insert into Product values (?,?,?,?,?,?)";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, newSKU);
            ps.setString(2, newProdTitle);
            ps.setString(3, newProdDesc);
            ps.setFloat(4, newPri);
            ps.setInt(5, stock);
            ps.setInt(6, dept);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in ADDSKU");
            e.printStackTrace();
        }

        return false;
    }

    public boolean locationExists(int aisle, int bay, int subBay) {

        String sql = "select * from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("error in locationExists");
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertIntoAisleBaySubBay(int aisle, int bay, int subBay) {

        String sql = "insert into AisleBaySubBay (AisleNumber, AisleBay, SubBay, occupied) values (?,?,?,?)";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);
            ps.setBoolean(4, false);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error inserting into asile bay subbay");
            e.printStackTrace();
        }

        return false;
    }

    public boolean locationOccupied(int aisle, int bay, int subBay) {

        String sql = "select occupied from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                return rs.getBoolean("occupied");

            }

        } catch (SQLException e) {
            System.out.println("error in location occupied");
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeLocation(int aisle, int bay, int subBay) {

        String sql = "delete from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in removeLocation");
            e.printStackTrace();
        }

        return false;
    }

    private int getLocationID(int aisle, int bay, int subBay) {

        int locationID = -1;

        String sql = "select LocationID from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                locationID = rs.getInt("LocationID");

            }

        } catch (SQLException e) {
            System.out.println("error in get location ID");
            e.printStackTrace();
        }

        return locationID;
    }

    private int palletID;

    private int getAutoPalletID() {

        palletID = -1;

        String sql = "select LAST_INSERT_ID() as Pallet";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                palletID = rs.getInt("Pallet");
            }

        } catch (SQLException e) {
            System.out.println("error in getAutoPalletID");
            e.printStackTrace();
        }

        return palletID;
    }

    public int insertPalletIntoPallet(int aisle, int bay, int subBay) {

        String sql = "insert into Pallet (DateCreated, LocationID) values (CURRENT_DATE, ?)";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, getLocationID(aisle, bay, subBay));

            int rows = ps.executeUpdate();

            System.out.println(rows);

            return getAutoPalletID();

        } catch (SQLException e) {
            System.out.println("error in insert into pallet table");
            e.printStackTrace();
        }

        return -1;
    }

    public boolean insertPalletIntoSKUsOnPallets(Map<String, String> productsOnPallet) {

        String sql = "insert into SKUsOnPallets values (?,?,?)";
        int rows = 0;
        try {

            PreparedStatement ps = c.prepareStatement(sql);

            for (Map.Entry<String, String> entry : productsOnPallet.entrySet()) {

                ps.setString(1, entry.getKey());
                ps.setInt(2, palletID);
                ps.setInt(3, Integer.parseInt(entry.getValue()));

                rows += ps.executeUpdate();

            }

        } catch (SQLException e) {
            System.out.println("error in insertIntoPalletIntoSKUsOnPallets");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public boolean updatePalletSKUs(Map<String, String> newSKUMap, int palletID) {

        String deleteFirstSQL = "delete from skusonpallets where PalletID = ?";

        String reInsertSQL = "insert into SKUsOnPallets values (?,?,?)";
        int rows = 0;
        try {
            PreparedStatement ps = c.prepareStatement(deleteFirstSQL);
            ps.setInt(1, palletID);

            ps.executeUpdate();

            PreparedStatement ps2 = c.prepareStatement(reInsertSQL);

            for (Map.Entry<String, String> entry : newSKUMap.entrySet()) {

                ps2.setString(1, entry.getKey());
                ps2.setInt(2, palletID);
                ps2.setInt(3, Integer.parseInt(entry.getValue()));

                rows = ps2.executeUpdate();

            }

        } catch (SQLException e) {
            System.out.println("error in updatePalletSKUs");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public void setLocationOccupied(int aisle, int bay, int subBay) {

        String sql = "update AisleBaySubBay set occupied = true where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in setLocationOccupied");
            e.printStackTrace();
        }

    }

    public void setLocationFree(int aisle, int bay, int subBay) {

        String sql = "update AisleBaySubBay set occupied = false where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in setLocationOccupied");
            e.printStackTrace();
        }

    }

    public Map<Integer, Integer> getSKUsByDept() {

        Map<Integer, Integer> SKUMap = new HashMap<>();

        String sql = "select department, count(SKU) as SKUs from Product group by department";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int department = rs.getInt("department");
                int SKUsPerDept = rs.getInt("SKUs");

                SKUMap.put(department, SKUsPerDept);

            }

        } catch (SQLException e) {
            System.out.println("error in getSKUsByDept");
            e.printStackTrace();
        }

        return SKUMap;
    }

    public Map<String, String> getAllPallets() {

        Map<String, String> allPallets = new HashMap<>();

        String sql = "select PalletID, AisleNumber, AisleBay, SubBay from pallet inner join AisleBaySubBay on pallet.LocationID = aislebaysubbay.LocationID";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String location = "";

            while (rs.next()) {

                int palletID = rs.getInt("PalletID");
                int AisleNum = rs.getInt("AisleNumber");
                int AisleBay = rs.getInt("AisleBay");
                int subBay = rs.getInt("SubBay");

                location = String.valueOf(AisleNum) + "-" + String.valueOf(AisleBay) + "-" + String.valueOf(subBay);

                allPallets.put(String.valueOf(palletID), location);

            }

        } catch (SQLException e) {
            System.out.println("error in getallpallets");
            e.printStackTrace();
        }

        return allPallets;
    }

    public boolean updateLocation(int palletID, int Aisle, int Bay, int subBay) {

        int locationID = getLocationID(Aisle, Bay, subBay);

        String sql = "update pallet set LocationID = ? where PalletID = ?";

        try {

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, locationID);
            ps.setInt(2, palletID);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in update location");
            e.printStackTrace();
        }

        return false;
    }

    public boolean deletePallet(int palletID) {

        String del1 = "delete from SKUsOnPallets where PalletID = ?";
        String del2 = "delete from Pallet where PalletID = ?";

        try {
            PreparedStatement ps1 = c.prepareStatement(del1);
            PreparedStatement ps2 = c.prepareStatement(del2);

            ps1.setInt(1, palletID);
            ps2.setInt(1, palletID);

            int rows1 = ps1.executeUpdate();
            int rows2 = ps2.executeUpdate();

            return (rows1 > 0 && rows2 > 0);

        } catch (SQLException ex) {
            System.out.println("error in delete pallet");
            ex.printStackTrace();
        }

        return false;
    }

    public boolean orderExists(String orderID) {

        String sql = "select Order_ID from Orders where Order_ID = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, orderID);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("error in orderExists");
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertIntoOrdersTable(String OID, String ONotes, String deliverDate) throws ParseException {

        int rows = 0;

        LocalDate ODate = LocalDate.now();

        String shipStat = "Pending";

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date utilDate = f.parse(deliverDate);

        Date SQLDD = new Date(utilDate.getTime());

        String sql = "insert into Orders values (?,?,?,?,?,?)";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, OID);
            ps.setString(2, ONotes);
            ps.setDate(3, SQLDD);
            ps.setDate(4, java.sql.Date.valueOf(ODate));
            ps.setBoolean(5, false);
            ps.setString(6, shipStat);

            rows += ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in insertintoOrdersTable");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public boolean insertSKUsIntoSKUsOnOrdersTable(Map<String, String> SKUsToAddToOrder, String orderID) {

        String sql = "insert into ProductsOnOrders values (?,?,?)";

        int rows = 0;

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            for (Map.Entry<String, String> entry : SKUsToAddToOrder.entrySet()) {

                ps.setString(1, entry.getKey());
                ps.setString(2, orderID);
                ps.setInt(3, Integer.parseInt(entry.getValue()));

                rows += ps.executeUpdate();

            }

        } catch (SQLException e) {
            System.out.println("error in insertSKUs into its order-SKU table");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public Map<String, String> getAllOrders() {

        Map<String, String> allOrders = new HashMap<>();

        String sql = "Select Order_ID, Notes from Orders";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String OrderID = rs.getString("Order_ID");
                String Notes = rs.getString("Notes");

                allOrders.put(OrderID, Notes);
            }

        } catch (SQLException e) {
            System.out.println("error in getAllOrders");
            e.printStackTrace();
        }

        return allOrders;
    }

    public boolean removeOrder(String orderID) {

        String sqldelete1 = "delete from ProductsOnOrders where Order_ID = ?";//delete from skus on orders first otherwise deletion anomoly
        String sqldelete2 = "delete from Orders where Order_ID = ?";//then delete from orders

        int rows = 0;

        try {
            PreparedStatement ps = c.prepareStatement(sqldelete1);
            ps.setString(1, orderID);

            PreparedStatement ps2 = c.prepareStatement(sqldelete2);
            ps2.setString(1, orderID);

            rows += ps.executeUpdate();

            rows += ps2.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in remove order");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public int getUserID(String userID) {

        String sql = "select User_ID from Users where Username = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return rs.getInt("User_ID");
            }

        } catch (SQLException e) {
            System.out.println("error in getUserID");
            e.printStackTrace();
        }

        return -1;
    }

    public boolean insertIntoOrdersPickedByUsers(String OrderID, String userID) {

        int rows = 0;

        String sql = "insert into OrdersPickedByUsers values (?,?,?,?)";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, OrderID);
            ps.setInt(2, getUserID(userID));
            ps.setDate(3, null);
            ps.setBoolean(4, false);

            rows += ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in insert into orders picked by users");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public List<Orders> loadOrders() {

        List<Orders> orders = new ArrayList<>();

        String sql = "select O.Order_ID, U.username, O.OrderDate from Orders as O inner join OrdersPickedByUsers as OP on OP.Order_ID = O.Order_ID inner join Users as U on OP.User_ID = U.User_ID";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String OrderID = rs.getString("Order_ID");
                String AssignedTo = rs.getString("username");
                String OrderDate = rs.getString("OrderDate");

                orders.add(new Orders(OrderID, AssignedTo, OrderDate));

            }

        } catch (SQLException e) {
            System.out.println("error in loadOrders");
            e.printStackTrace();
        }

        return orders;
    }

    public Map<String, String> getAllTrucks() {

        Map<String, String> allpackedTrucks = new HashMap<>();

        String sql = "select TruckID, DateReceived, Unpacked from truck";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String TruckID;
                String DateReceived;

                if (!rs.getBoolean("Unpacked")) {

                    TruckID = String.valueOf(rs.getInt("TruckID"));
                    DateReceived = rs.getString("DateReceived");

                    allpackedTrucks.put(TruckID, DateReceived);

                }

            }

        } catch (SQLException e) {
            System.out.println("error in get all trucks");
            e.printStackTrace();
        }

        return allpackedTrucks;
    }

    public Map<String, String> getSKUsonTruck(int truckID) {

        Map<String, String> SKUsonTrucks = new HashMap<>();

        String sql = "select SKU, QuantityOfSKU from QuantityOfSKUOnTruck where TruckID = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, truckID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String SKU = rs.getString("SKU");
                String Quantity = String.valueOf(rs.getInt("QuantityOfSKU"));

                SKUsonTrucks.put(SKU, Quantity);
            }

        } catch (SQLException e) {
            System.out.println("error in get skus on truck");
            e.printStackTrace();
        }

        return SKUsonTrucks;
    }

    public boolean processTruck(int truckID) {

        int rows = 0;

        String sql = "update Truck set Unpacked = true where TruckID = ?";

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, truckID);

            rows += ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in processtruck");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public Map<String, String> getAllunPackedTrucks() {

        Map<String, String> allUnpackedTrucks = new HashMap<>();

        String sql = "select TruckID, DateReceived, Unpacked from truck";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String TruckID;
                String DateReceived;

                if (rs.getBoolean("Unpacked")) {

                    TruckID = String.valueOf(rs.getInt("TruckID"));
                    DateReceived = rs.getString("DateReceived");

                    allUnpackedTrucks.put(TruckID, DateReceived);

                }

            }

        } catch (SQLException e) {
            System.out.println("error in get all trucks");
            e.printStackTrace();
        }

        return allUnpackedTrucks;
    }

    public boolean insertNewTruck(int TruckID, String DeliveryDate) throws ParseException {

        int rows = 0;

        String sql = "insert into Truck values (?,?,false)";

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date utilDate = f.parse(DeliveryDate);

        Date SQLDOB = new Date(utilDate.getTime());

        try {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, TruckID);
            ps.setDate(2, SQLDOB);

            rows += ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in createnewtruck");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public boolean insertIntoSKUsOnTrucks(Map<String, String> SKUsToAddToTruck, int truckID) {

        int rows = 0;

        String sql = "insert into QuantityOfSKUOnTruck values (?,?,?)";

        try {
            PreparedStatement ps = c.prepareStatement(sql);

            for (Map.Entry<String, String> entry : SKUsToAddToTruck.entrySet()) {

                ps.setInt(1, truckID);
                ps.setString(2, entry.getKey());
                ps.setInt(3, Integer.parseInt(entry.getValue()));
                
                rows += ps.executeUpdate();
                
            }

        } catch (SQLException e) {
            System.out.println("error in insertSKUsOnTrucks");
            e.printStackTrace();
        }

        return rows > 0;
    }
    
    public boolean getTruckExists(int TruckID){
        
        String sql = "select * from Truck where TruckID = ?";
        
        try{
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, TruckID);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                return true;
            }
            
            
        } catch(SQLException e){
            System.out.println("error in get truck exists");
            e.printStackTrace();
        }
        
        
        return false;
    }

    
}
