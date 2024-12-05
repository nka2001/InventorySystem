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
 * database manager class, controls all access and data retrieval to/from the
 * database
 *
 * @author nicka
 */
public class DatabaseManager {

    public static Connection c;//database connection instance, used to access the database and send SQL queries to DB

    /**
     * only one connection is needed for the database, once the connection is
     * created, it can be used by all database methods needing to send queries.
     */
    public static void connect() {
        try {
            //Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/InventoryManagement", "invadmin", "Jo3Lik3stv2024!");//create the database connection using NOT root creds

        } catch (Exception e) {
            System.out.println("error in static void connect");
            e.printStackTrace();
        }
    }

    /**
     * on database instance creation, create a connection for the database.
     *
     */
    public DatabaseManager() {
        connect();
    }

    /**
     * used by login page to authenticate a given user and their password
     *
     * returns true on successful authentication
     *
     * @param username
     * @param password
     * @return
     */
    public boolean init_ConnectionToDB(String username, String password) {
        return init_ConnectToDB(username, password);
    }

    /**
     * attempts to authenticate a user based on their given username and
     * password
     *
     * returns true if authentication is good
     *
     * @param username
     * @param password
     * @return
     */
    private boolean init_ConnectToDB(String username, String password) {

        try {
            //first create the connection to the database, sending the get query to compare passwords, and see if the user is disabled or not
            PreparedStatement ps = c.prepareStatement("Select username, User_password, Disabled from users where username = ?");
            ps.setString(1, username.trim());

            //execute the query
            ResultSet rs = ps.executeQuery();

            //if the query does not have anything in the executation, the authentication fails (usually this means the user does not exist
            if (!rs.next()) {
                return false;

            } else {//if there is a result returned from the query, continue with authentication

                //password comparison is done by encrypting the given password first, then comparing it later to what is stored in the DB
                String encryptedPass = rs.getString("User_password");
                boolean isDisabled = rs.getBoolean("Disabled");//also get if the user is disabled or not (true means disabled)

                //encrypt the given password for later comparison
                String encryptUserInput = EncryptionManager.encrypt(password);

                //check if the encrypted password matches the already encrypted password in the database, also, check if the user is NOT disabled
                if (encryptUserInput.equals(encryptedPass) && !isDisabled) {

                    return true;//return true if authentication is good
                }

            }

        } catch (SQLException e) {
            System.out.println("error in initconnecttoDB");
            e.printStackTrace();
        }
        return false;//return false by default
    }

    /**
     * add user will attempt to insert a new user into the database based on the
     * data collected from the user
     *
     * data by this point has been validated and is considered good to add
     *
     * @param username
     * @param password
     * @param displayName
     * @param fName
     * @param lName
     * @param DateOfBirth
     * @param Gender
     * @param payRate
     * @param isAdmin
     * @param position
     * @param isDisabled
     * @return
     * @throws ParseException
     */
    public boolean addUser(String username, String password, String displayName, String fName, String lName, String DateOfBirth, String Gender, float payRate, boolean isAdmin, String position, boolean isDisabled) throws ParseException {

        String SQL = "insert into Users (username, User_password, Display_Name, First_Name, Last_Name, DateOfBirth, Gender, PayRate, Administrator, Position, Disabled, requiresPWChange) values (?,?,?,?,?,?,?,?,?,?,?,true)";
        String encryptedPassword = EncryptionManager.encrypt(password);//encrypt the plaintext password

        //convert the passed date of birth to the date data type for SQL insertion
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date utilDate = f.parse(DateOfBirth);

        Date SQLDOB = new Date(utilDate.getTime());

        //attempt to add the user into the database
        try {
            //set up the sql query to be used
            PreparedStatement ps = c.prepareStatement(SQL);
            //set all aspects of the insert
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

            int rows = ps.executeUpdate();//attempt to insert, returning more than 0 rows indicates a successful insert

            return rows > 0;//returns true if there is more than 0 rows inserted

        } catch (SQLException ex) {
            System.out.println("error in inserting new user");
            ex.printStackTrace();

        }
        return false;//returns false by default
    }

    /**
     * findUser will attempt to find a user based on the given user
     *
     * used to ensure no duplicate users are added
     *
     * @param username
     * @return
     */
    public boolean findUser(String username) {

        try {

            //create a statement for execution
            PreparedStatement ps = c.prepareStatement("Select username from users where username = ?");
            ps.setString(1, username.trim());

            //execute the query
            ResultSet rs = ps.executeQuery();

            //if there is something returned by the query, the user exists
            return rs.next();

        } catch (SQLException e) {
            System.out.println("error in findUser");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * isAdministrator will determine if the selected user is an administrator
     * or not
     *
     * affects GUI
     *
     * @param username
     * @return
     */
    public boolean isAdministrator(String username) {

        try {

            //create statement for execution
            PreparedStatement ps = c.prepareStatement("Select Administrator from users where username = ?");
            ps.setString(1, username.trim());

            //execute the query
            ResultSet rs = ps.executeQuery();

            //if there is nothing returned just return false
            if (!rs.next()) {
                return false;
            } else {
                //if the query returns the admin field, return its value
                return rs.getBoolean("Administrator");

            }

        } catch (SQLException e) {
            System.out.println("error in isAdministrator");
            e.printStackTrace();
        }

        return false;//return false by default

    }

    /**
     * returnTFPallets will return the number of spaces that are occupied, and
     * the number of spaces that are free
     *
     * @return
     */
    public ArrayList<Integer> returnTFPallets() {
        //list to be returned
        ArrayList<Integer> arr = new ArrayList<>();

        //SQL query to get the occupied spaces and free spaces
        String sql = "select sum(case when occupied = true then 1 else 0 end) as  trueCount,"
                + "sum(case when occupied = false then 1 else 0 end) as falseCount from aislebaysubbay";

        try {
            //create statement for execution
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                //get counts from executed query
                int trueCount = rs.getInt("trueCount");
                int falseCount = rs.getInt("falseCount");

                arr.add(trueCount);//add count to returning list
                arr.add(falseCount);//add count to returning list

            }
        } catch (SQLException e) {
            System.out.println("error in returnTFPallets, SQL error");
            e.printStackTrace();
        }
        return arr;//return the list containing the counts of free spaces and used spaces

    }

    /**
     * loadUsers will return a map of all the users currently in the database
     *
     * returns username and display name
     *
     * @return
     */
    public Map<String, String> loadUsers() {

        Map<String, String> userMap = new HashMap<>();//map to be returned

        //SQL query to get all usernames and display names
        String sql = "select username, Display_Name from users";

        try {

            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the returned table
            while (rs.next()) {

                //add each iteration into the map to be returned
                userMap.put(rs.getString("username"), rs.getString("Display_Name"));

            }

        } catch (SQLException e) {
            System.out.println("error in loadUsers");
            e.printStackTrace();
        }

        return userMap;//return the map to the dashboard
    }

    /**
     * getAllSKUs will return a map containing all of the SKUs currently in the
     * database
     *
     * @return
     */
    public Map<String, String> getAllSKUs() {

        Map<String, String> SKUMap = new HashMap<>();//map to be returned

        String sql = "Select SKU, ProductTitle from Product order by department";

        try {
            //set up the statement
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the statement
            ResultSet rs = ps.executeQuery();

            //iterate through the returned table, adding it to the map to be returned
            while (rs.next()) {
                SKUMap.put(rs.getString("SKU"), rs.getString("ProductTitle"));
            }

        } catch (SQLException e) {

            System.out.println("error in getAllSKUs");
            e.printStackTrace();
        }
        return SKUMap;//return the map
    }

    /**
     * getAllPalletTagSKUs will return the SKUs on a given pallet ID
     *
     * @param palletID
     * @return
     */
    public Map<String, String> getAllPalletTagSKUs(int palletID) {

        //map containing all SKUs on given pallet ID
        Map<String, String> palletSKUMap = new HashMap<>();

        //SQL query
        String sql = "select SKU, QuantityOfSKU from SKUsOnPallets where PalletID = ?";

        try {
            //create the statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, palletID);

            //execute the statement
            ResultSet rs = ps.executeQuery();

            //iterate through the statement, adding each line to the map containing all skus on a given pallet
            while (rs.next()) {

                int quantityAsString = rs.getInt("QuantityOfSKU");

                palletSKUMap.put(rs.getString("SKU"), String.valueOf(quantityAsString));

            }

        } catch (SQLException e) {
            System.out.println("error in getAllPalletTagSKUs");
            e.printStackTrace();
        }

        return palletSKUMap;//return the map containing all the skus on a given pallet ID

    }

    /**
     * removeSKUFromPallet will remove a given SKU from all pallets
     *
     * this is used for deletion anomaly avoiding
     *
     * @param SKU
     */
    public void removeSKUFromPallet(String SKU) {

        //SQL query
        String sql = "delete from SKUsOnPallets where SKU = ?";

        try {

            //create the statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            //execute the query
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in removeSKUfromPallet");
            e.printStackTrace();
        }

    }

    /**
     * loadLocations will return a list of all of the locations in the database,
     * regardless of occupied or not
     *
     * @return
     */
    public List<String> loadLocations() {

        List<String> locs = new ArrayList<>();//list to be returned

        String sql = "select AisleNumber, AisleBay, SubBay from AisleBaySubBay";//query

        String addToList = "";//location is three separate int values, all get cast to a string and concatenated to one big string

        try {

            //setup query to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute query
            ResultSet rs = ps.executeQuery();

            //iterate through the returned table
            while (rs.next()) {

                //cast returned values to strings for later assembly and adding to a list
                String aisleNum = String.valueOf(rs.getInt("AisleNumber"));

                String bayNum = String.valueOf(rs.getInt("AisleBay"));

                String SubBay = String.valueOf(rs.getInt("SubBay"));

                //add returned values into a string
                addToList = aisleNum + "-" + bayNum + "-" + SubBay;
                locs.add(addToList);//add that string to a list to be returned
            }

        } catch (SQLException e) {
            System.out.println("error in loadlocations");
            e.printStackTrace();
        }
        return locs;//return the list containing all of the locations in the database
    }

    /**
     * loadPalletBySKU will search for all pallets that contain the given SKU
     * and return a list of those pallets that have the given sku
     *
     * @param SKU
     * @return
     */
    public List<Pallet> loadPalletsBySKU(String SKU) {

        List<Pallet> pallets = new ArrayList<>();//list to be returned

        String sql = "select Pallet.PalletID, AisleNumber, AisleBay, SubBay, QuantityOfSKU from Pallet inner join AisleBaySubBay on Pallet.LocationID = AisleBaySubBay.LocationID inner join skusonpallets on Pallet.palletID = skusonpallets.PalletID where SKU = ?";

        try {
            //prepare query to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //used for getting the location (three ints combined into one string
            String location;

            //iterate through the returned table, adding line by line to the list to be returned
            while (rs.next()) {

                //palletID
                int palID = rs.getInt("PalletID");
                //location (three ints cast to string and added)
                location = String.valueOf(rs.getInt("AisleNumber")) + "-" + String.valueOf(rs.getInt("AisleBay")) + "-" + String.valueOf(rs.getInt("SubBay"));
                //quantity
                int Quan = rs.getInt("QuantityOfSKU");

                //add the three values into the pallet list to be returned
                pallets.add(new Pallet(palID, Quan, location));

            }

        } catch (SQLException e) {
            System.out.println("error in loadPalletsBySKU");
            e.printStackTrace();
        }

        return pallets;//return the list
    }

    /**
     * loadProducs will load a list of all products, their quantitys, stock, and
     * what department they belong to
     *
     * @return
     */
    public List<Product> loadProducts() {

        List<Product> prods = new ArrayList<>();//list to be returned

        //query
        String sql = "select SKU, ProductTitle, Department, Stock from Product";

        try {

            //prepare and execute the query
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            //iterate through the returned table, adding a new product to the product list each iteration
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
        return prods;//return the list
    }

    /**
     * resetPassword will reset a given users password with a provided password
     * to be set
     *
     * @param username
     * @param newPassword
     * @return
     */
    public boolean resetPassword(String username, String newPassword) {

        //query
        String sql = "update users set User_Password = ?, requiresPWChange = true where username = ?";

        //encrypt the new password
        String newEncryptedPassword = EncryptionManager.encrypt(newPassword);

        try {
            //create query to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, newEncryptedPassword);
            ps.setString(2, username);

            //execute the query
            int rows = ps.executeUpdate();

            return rows > 0;//if the update is successful, 1 row will be updated, returns true

        } catch (SQLException e) {
            System.out.println("error in resetPassword");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * makeAdministrator will update a user's permissions
     *
     * @param username
     * @param isAdmin
     * @return
     */
    public boolean makeAdministrator(String username, boolean isAdmin) {

        //query
        String sql = "update users set Administrator = ? where username = ?";

        try {
            //create a query
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setBoolean(1, isAdmin);
            ps.setString(2, username);

            //execute the query
            int rows = ps.executeUpdate();

            return rows > 0;//returns true if a row is updated

        } catch (SQLException e) {
            System.out.println("error in make administrator");
            e.printStackTrace();
        }

        return false;//return false
    }

    /**
     * disableUser will disable a given user account
     *
     * @param username
     * @param isDisabled
     * @return
     */
    public boolean disableUser(String username, boolean isDisabled) {

        //query
        String sql = "update users set Disabled = ? where username = ?";

        try {
            //create a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setBoolean(1, isDisabled);
            ps.setString(2, username);

            //execute the query
            int rows = ps.executeUpdate();

            //returns true if at least 1 row is updated
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in disable user");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * getIsAdmin will look for if a given username is an admin or not
     *
     * @param username
     * @return
     */
    public boolean getIsAdmin(String username) {

        //query
        String sql = "Select Administrator from users where username = ?";

        try {

            //create a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            //execute the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Administrator");//return the value of the field returned (since its boolean)
            }

        } catch (SQLException e) {
            System.out.println("error in getIsAdmin");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * getIsDisabled will return whether or not a user is disabled
     *
     * @param username
     * @return
     */
    public boolean getIsDisabled(String username) {

        //query
        String sql = "select Disabled from users where username = ?";

        try {

            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            //execute the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {//if the query has a result
                return rs.getBoolean("Disabled");//return the value if the given field
            }

        } catch (SQLException e) {
            System.out.println("error in getIsDisabled");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * removeUser will remove a user based on the given username
     *
     * @param username
     * @return
     */
    public boolean removeUser(String username) {

        String sql2 = "update orderspickedbyusers set user_ID = 6 where user_ID = ?";//instead of deleting, just assign order to admin so no orders are lost

        String sql3 = "update usersunpackingtruck set user_ID = 6 where user_ID = ?";//instead of deleting just assing the truck to admin so no trucks are lost

        String sql = "delete from users where username = ?";

        int rows2 = 0;
        int rows3 = 0;
        
        int userID = getUserID(username);
        
        try {
            //create query to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            PreparedStatement ps2 = c.prepareStatement(sql2);
            ps2.setInt(1, userID);
            PreparedStatement ps3 = c.prepareStatement(sql3);
            ps3.setInt(1, userID);
            
            rows2 += ps2.executeUpdate();
            rows3 += ps3.executeUpdate();

            //execute the query
            int rows = ps.executeUpdate();

            return rows > 0 || (rows2 > 0 || rows3 > 0);//return true if there is at least 1 row removed

        } catch (SQLException e) {
            System.out.println("error in remove user");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * removeSKU will remove a given SKU
     *
     * @param SKU
     * @return
     */
    public boolean removeSKU(String SKU) {

        //query
        String sql = "delete from Product where SKU = ?";
        String sql1 = "delete from skusonpallets where sku = ?";
        String sql2 = "delete from productsonorders where sku = ?";
        String sql3 = "delete from quantityofSKUontruck where sku = ?";

        int rows = 0;
        int rows2 = 0;
        int rows3 = 0;
        int rows4 = 0;

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            PreparedStatement ps2 = c.prepareStatement(sql1);
            ps2.setString(1, SKU);

            PreparedStatement ps4 = c.prepareStatement(sql3);
            ps4.setString(1, SKU);

            PreparedStatement ps3 = c.prepareStatement(sql2);
            ps3.setString(1, SKU);

            //execute the query
            rows2 += ps2.executeUpdate();
            rows3 += ps3.executeUpdate();
            rows4 += ps4.executeUpdate();
            rows += ps.executeUpdate();

            return rows > 0 || (rows2 > 0 || rows3 > 0 || rows4 > 0);//return true if at least 1 row is removed

        } catch (SQLException e) {
            System.out.println("error in removeSKU");
            e.printStackTrace();

        }
        return false;//return false by default
    }

    /**
     * findSKU will locate a given SKU and return if it exists
     *
     * @param SKU
     * @return
     */
    public boolean findSKU(String SKU) {

        //query
        String sql = "select SKU from Product where SKU = ?";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, SKU);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //return if there is a row in the result set
            return rs.next();

        } catch (SQLException e) {
            System.out.println("error in find SKU");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * addSKU will add a SKU and its given attributes like price, stock, etc.
     *
     * @param newSKU
     * @param newProdTitle
     * @param newProdDesc
     * @param newPri
     * @param stock
     * @param dept
     * @return
     */
    public boolean addSKU(String newSKU, String newProdTitle, String newProdDesc, float newPri, int stock, int dept) {

        //query
        String sql = "insert into Product values (?,?,?,?,?,?)";

        try {
            //create a statement to be executed and set the values
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, newSKU);
            ps.setString(2, newProdTitle);
            ps.setString(3, newProdDesc);
            ps.setFloat(4, newPri);
            ps.setInt(5, stock);
            ps.setInt(6, dept);

            //execute the query
            int rows = ps.executeUpdate();

            return rows > 0;//return true if the sku was added and the returned result of the query is 1 row inserted

        } catch (SQLException e) {
            System.out.println("error in ADDSKU");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * locationExists will check if a given location exists or not in the
     * database
     *
     * @param aisle
     * @param bay
     * @param subBay
     * @return
     */
    public boolean locationExists(int aisle, int bay, int subBay) {

        //query
        String sql = "select * from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            //create a statement to be executed and set the data
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            //execute the query
            ResultSet rs = ps.executeQuery();

            return rs.next();//return true if there is a returned row in the table

        } catch (SQLException e) {
            System.out.println("error in locationExists");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * insertIntoAisleBaySubBay will insert a new location (aisle, Bay, Sub Bay)
     * into the database
     *
     * @param aisle
     * @param bay
     * @param subBay
     * @return
     */
    public boolean insertIntoAisleBaySubBay(int aisle, int bay, int subBay) {

        //query
        String sql = "insert into AisleBaySubBay (AisleNumber, AisleBay, SubBay, occupied) values (?,?,?,?)";

        try {

            //create a statement to be executed and set the values
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);
            ps.setBoolean(4, false);

            int rows = ps.executeUpdate();//execute the query

            return rows > 0;//return true if the query inserts a row

        } catch (SQLException e) {
            System.out.println("error inserting into asile bay subbay");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * locationOccupied will check if a given location is occupied or not
     *
     * @param aisle
     * @param bay
     * @param subBay
     * @return
     */
    public boolean locationOccupied(int aisle, int bay, int subBay) {

        //query
        String sql = "select occupied from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {
            //create a query to be executed and set the values
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //if the resultset has a row, go to it
            while (rs.next()) {

                //return the value of occupied
                return rs.getBoolean("occupied");

            }

        } catch (SQLException e) {
            System.out.println("error in location occupied");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * removeLocation will remove a given location from the database
     *
     * @param aisle
     * @param bay
     * @param subBay
     * @return
     */
    public boolean removeLocation(int aisle, int bay, int subBay) {

        //query
        String sql = "delete from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {
            //create statement to be executed and set the value
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            //execute query
            int rows = ps.executeUpdate();

            //return true if at least 1 row was removed
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("error in removeLocation");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * getLocationID will get an ID for a given aisle, bay, and subbay
     *
     * @param aisle
     * @param bay
     * @param subBay
     * @return
     */
    private int getLocationID(int aisle, int bay, int subBay) {

        //location is set to -1 by default if locationID is not found
        int locationID = -1;

        //query
        String sql = "select LocationID from AisleBaySubBay where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            //create a statement to be executed and set the values
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //enter the result table
            while (rs.next()) {

                //get and set the value in the locationID column
                locationID = rs.getInt("LocationID");

            }

        } catch (SQLException e) {
            System.out.println("error in get location ID");
            e.printStackTrace();
        }

        return locationID;//return the found location ID
    }

    private int palletID;//pallet ID to be used by other methods below

    /**
     * getAutoPalletID will get the last inserted pallet ID
     *
     * used by createPallet
     *
     * @return
     */
    private int getAutoPalletID() {

        palletID = -1;

        //query
        String sql = "select LAST_INSERT_ID() as Pallet";

        try {
            //prepare the statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the returned table
            while (rs.next()) {
                palletID = rs.getInt("Pallet");//set the pallet ID to the last inserted pallet ID
            }

        } catch (SQLException e) {
            System.out.println("error in getAutoPalletID");
            e.printStackTrace();
        }

        return palletID;//return the pallet ID
    }

    /**
     * insertPalletIntoPallet will insert a new pallet into the pallet table
     * (only the date created, and location ID, pallet ID is an auto increment).
     *
     * @param aisle
     * @param bay
     * @param subBay
     * @return
     */
    public int insertPalletIntoPallet(int aisle, int bay, int subBay) {

        //query
        String sql = "insert into Pallet (DateCreated, LocationID) values (CURRENT_DATE, ?)";

        try {
            //prepare a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, getLocationID(aisle, bay, subBay));

            //execute the query
            int rows = ps.executeUpdate();

            //return the last inserted pallet ID
            return getAutoPalletID();

        } catch (SQLException e) {
            System.out.println("error in insert into pallet table");
            e.printStackTrace();
        }

        return -1;//return -1 by default
    }

    /**
     * insertPalletIntoSKUsOnPallets will insert a map containing SKUs and
     * quantites into the SKUs on pallets table
     *
     * @param productsOnPallet
     * @return
     */
    public boolean insertPalletIntoSKUsOnPallets(Map<String, String> productsOnPallet) {

        //query
        String sql = "insert into SKUsOnPallets values (?,?,?)";
        int rows = 0;
        try {
            //prepare a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);

            //iterate through the given map, going entry by entry adding the SKUs for a given pallet ID to the table
            for (Map.Entry<String, String> entry : productsOnPallet.entrySet()) {

                //set and execute the query
                ps.setString(1, entry.getKey());
                ps.setInt(2, palletID);
                ps.setInt(3, Integer.parseInt(entry.getValue()));

                //execute the query
                rows += ps.executeUpdate();

            }

        } catch (SQLException e) {
            System.out.println("error in insertIntoPalletIntoSKUsOnPallets");
            e.printStackTrace();
        }

        return rows > 0;//return true if at least 1 row is inserted
    }

    /**
     *
     * @param newSKUMap
     * @param palletID
     * @return
     */
    public boolean updatePalletSKUs(Map<String, String> newSKUMap, int palletID) {

        //query 1, delete the contents of a given pallet ID
        String deleteFirstSQL = "delete from skusonpallets where PalletID = ?";

        //query 2, re-insert the sku list containing added or removed skus into the pallet 
        String reInsertSQL = "insert into SKUsOnPallets values (?,?,?)";
        int rows = 0;
        try {
            //create statement to be executed
            PreparedStatement ps = c.prepareStatement(deleteFirstSQL);
            ps.setInt(1, palletID);

            //execute query 1
            ps.executeUpdate();

            //create statement 2 to be executed
            PreparedStatement ps2 = c.prepareStatement(reInsertSQL);

            //iterate through the contents of the newSKUMap, adding each entry into the database
            for (Map.Entry<String, String> entry : newSKUMap.entrySet()) {

                //set and execute query 
                ps2.setString(1, entry.getKey());
                ps2.setInt(2, palletID);
                ps2.setInt(3, Integer.parseInt(entry.getValue()));

                rows += ps2.executeUpdate();

            }

        } catch (SQLException e) {
            System.out.println("error in updatePalletSKUs");
            e.printStackTrace();
        }

        return rows > 0;//return true if at least 1 row is inserted
    }

    /**
     * setLocationOccupied will set a given location to occupied
     *
     * @param aisle
     * @param bay
     * @param subBay
     */
    public void setLocationOccupied(int aisle, int bay, int subBay) {

        //query
        String sql = "update AisleBaySubBay set occupied = true where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            //create and set a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            ps.executeUpdate();//execute query

        } catch (SQLException e) {
            System.out.println("error in setLocationOccupied");
            e.printStackTrace();
        }

    }

    /**
     * setLocationFree will set a given location to free
     *
     * @param aisle
     * @param bay
     * @param subBay
     */
    public void setLocationFree(int aisle, int bay, int subBay) {

        //query
        String sql = "update AisleBaySubBay set occupied = false where AisleNumber = ? and AisleBay = ? and SubBay = ?";

        try {

            //create and set a statement
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, aisle);
            ps.setInt(2, bay);
            ps.setInt(3, subBay);

            ps.executeUpdate();//execute the query

        } catch (SQLException e) {
            System.out.println("error in setLocationOccupied");
            e.printStackTrace();
        }

    }

    /**
     * getSKUsByDept will group all SKUs by their departent ID, and then return
     * the counts to be loaded into charts
     *
     * @return
     */
    public Map<Integer, Integer> getSKUsByDept() {

        //Map containing the SKU count and department ID
        Map<Integer, Integer> SKUMap = new HashMap<>();

        //query
        String sql = "select department, count(SKU) as SKUs from Product group by department";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the query adding each row into the SKU count map
            while (rs.next()) {

                int department = rs.getInt("department");
                int SKUsPerDept = rs.getInt("SKUs");

                SKUMap.put(department, SKUsPerDept);

            }

        } catch (SQLException e) {
            System.out.println("error in getSKUsByDept");
            e.printStackTrace();
        }

        return SKUMap;//return the SKU map
    }

    /**
     * getAllPallets will get a list of all the pallets in the database
     *
     * @return
     */
    public Map<String, String> getAllPallets() {

        Map<String, String> allPallets = new HashMap<>();//map containing all of the pallets

        //query
        String sql = "select PalletID, AisleNumber, AisleBay, SubBay from pallet inner join AisleBaySubBay on pallet.LocationID = aislebaysubbay.LocationID";

        try {
            //create a statement to be executed and execute it
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String location = "";//location (since three ints get converted and added to a string)

            //iterate through the result table and add each entry into the allPallets map
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

        return allPallets;//return the allPallet list
    }

    /**
     * updateLocation will take a given pallet ID and new location, and update
     * it with a new location
     *
     * @param palletID
     * @param Aisle
     * @param Bay
     * @param subBay
     * @return
     */
    public boolean updateLocation(int palletID, int Aisle, int Bay, int subBay) {

        //first get the new location's location ID
        int locationID = getLocationID(Aisle, Bay, subBay);

        //query
        String sql = "update pallet set LocationID = ? where PalletID = ?";

        try {
            //create and set a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, locationID);
            ps.setInt(2, palletID);

            //execute the query
            int rows = ps.executeUpdate();

            return rows > 0;//return true if at least 1 row is updated

        } catch (SQLException e) {
            System.out.println("error in update location");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * deletePallet will delete a pallet given a pallet ID
     *
     * deletion anomaly is handled here
     *
     * @param palletID
     * @return
     */
    public boolean deletePallet(int palletID) {

        //queries
        String del1 = "delete from SKUsOnPallets where PalletID = ?";
        String del2 = "delete from Pallet where PalletID = ?";

        try {
            //create two statements to be executed
            PreparedStatement ps1 = c.prepareStatement(del1);
            PreparedStatement ps2 = c.prepareStatement(del2);

            //set the values
            ps1.setInt(1, palletID);
            ps2.setInt(1, palletID);

            //execute the queries
            int rows1 = ps1.executeUpdate();
            int rows2 = ps2.executeUpdate();

            //return true if rows were removed from both queries
            return (rows1 > 0 && rows2 > 0);

        } catch (SQLException ex) {
            System.out.println("error in delete pallet");
            ex.printStackTrace();
        }

        return false;//return false
    }

    /**
     * orderExists will return if an orderID exists in the database or not
     *
     *
     * @param orderID
     * @return
     */
    public boolean orderExists(String orderID) {

        //query
        String sql = "select Order_ID from Orders where Order_ID = ?";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, orderID);

            //execute the query
            ResultSet rs = ps.executeQuery();

            return rs.next();//return true if there is a result in the result set

        } catch (SQLException e) {
            System.out.println("error in orderExists");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * insertIntoOrdersTable will insert a new order into the DB
     *
     *
     * @param OID
     * @param ONotes
     * @param deliverDate
     * @return
     * @throws ParseException
     */
    public boolean insertIntoOrdersTable(String OID, String ONotes, String deliverDate) throws ParseException {

        //user generated input
        int rows = 0;

        LocalDate ODate = LocalDate.now();

        String shipStat = "Pending";

        //convert the deliverDate to the date type for SQL insertion
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date utilDate = f.parse(deliverDate);

        Date SQLDD = new Date(utilDate.getTime());

        //query
        String sql = "insert into Orders values (?,?,?,?,?,?)";

        try {
            //create and set a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, OID);
            ps.setString(2, ONotes);
            ps.setDate(3, SQLDD);
            ps.setDate(4, java.sql.Date.valueOf(ODate));
            ps.setBoolean(5, false);
            ps.setString(6, shipStat);

            rows += ps.executeUpdate();//execute the query

        } catch (SQLException e) {
            System.out.println("error in insertintoOrdersTable");
            e.printStackTrace();
        }

        return rows > 0;//returns true if at least 1 row is inserted
    }

    /**
     * insertSKUsIntoSKUsOnOrdersTable will insert all SKUs apart of a given
     * order ID
     *
     * @param SKUsToAddToOrder
     * @param orderID
     * @return
     */
    public boolean insertSKUsIntoSKUsOnOrdersTable(Map<String, String> SKUsToAddToOrder, String orderID) {

        //query
        String sql = "insert into ProductsOnOrders values (?,?,?)";

        int rows = 0;

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //iterate through the given SKU map, adding each entry into the DB
            for (Map.Entry<String, String> entry : SKUsToAddToOrder.entrySet()) {

                ps.setString(1, entry.getKey());
                ps.setString(2, orderID);
                ps.setInt(3, Integer.parseInt(entry.getValue()));

                rows += ps.executeUpdate();//execute the query

            }

        } catch (SQLException e) {
            System.out.println("error in insertSKUs into its order-SKU table");
            e.printStackTrace();
        }

        return rows > 0;//return true if at least 1 row is inserted
    }

    /**
     * getAllOrders will retrieve all orders in the database and send them back
     * as a map
     *
     * @return
     */
    public Map<String, String> getAllOrders() {

        //map holding all of the orders in the DB
        Map<String, String> allOrders = new HashMap<>();

        //query
        String sql = "Select Order_ID, Notes from Orders";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the result getting the order and notes
            while (rs.next()) {
                String OrderID = rs.getString("Order_ID");
                String Notes = rs.getString("Notes");

                allOrders.put(OrderID, Notes);//add the row to the allOrders table
            }

        } catch (SQLException e) {
            System.out.println("error in getAllOrders");
            e.printStackTrace();
        }

        return allOrders;//return the all orders map
    }

    /**
     * removeOrder will remove an order from the database
     *
     * deletion anomaly handled here
     *
     * @param orderID
     * @return
     */
    public boolean removeOrder(String orderID) {

        //queries 
        String sqldelete1 = "delete from ProductsOnOrders where Order_ID = ?";//delete from skus on orders first otherwise deletion anomoly
        String sqldelete2 = "delete from Orders where Order_ID = ?";//then delete from orders
        String sqldelete3 = "delete from orderspickedbyusers where Order_ID = ?";

        int rows = 0;
        int rows2 = 0;
        int rows3 = 0;

        try {
            //create and set statement 1 (delete all skus associated with an order)
            PreparedStatement ps = c.prepareStatement(sqldelete1);
            ps.setString(1, orderID);

            PreparedStatement ps3 = c.prepareStatement(sqldelete3);
            ps3.setString(1, orderID);

            //create and set statement 2 (delete the actual order)
            PreparedStatement ps2 = c.prepareStatement(sqldelete2);
            ps2.setString(1, orderID);

            //execute both queries
            rows += ps.executeUpdate();
            rows3 += ps3.executeUpdate();
            rows2 += ps2.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in remove order");
            e.printStackTrace();
        }

        return rows2 > 0 && (rows > 0 || rows3 > 0);//return true if rows were removed
    }

    /**
     * getUserID will get a user ID based on the username
     *
     *
     * @param userID
     * @return
     */
    public int getUserID(String userID) {

        //query
        String sql = "select User_ID from Users where Username = ?";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userID);

            //execute the quert
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return rs.getInt("User_ID");//return the returned user ID
            }

        } catch (SQLException e) {
            System.out.println("error in getUserID");
            e.printStackTrace();
        }

        return -1;//return -1 by default
    }

    /**
     * insertIntoOrdersPickedByUsers will assign a given order to a user
     *
     * @param OrderID
     * @param userID
     * @return
     */
    public boolean insertIntoOrdersPickedByUsers(String OrderID, String userID) {

        int rows = 0;

        //query
        String sql = "insert into OrdersPickedByUsers values (?,?,?,?)";

        try {

            //if the order is already assigned, re assign it instead of inserting a new row
            if (orderIsAssigned(OrderID)) {
                String sql2 = "update ordersPickedByUsers set user_ID = ? where Order_ID = ?";
                int rows2 = 0;

                PreparedStatement ps2 = c.prepareStatement(sql2);
                ps2.setInt(1, getUserID(userID));
                ps2.setString(2, OrderID);

                rows2 = ps2.executeUpdate();

                return rows2 > 0;

            } else {

//create and set a statement to be executed
                PreparedStatement ps = c.prepareStatement(sql);
                ps.setString(1, OrderID);
                ps.setInt(2, getUserID(userID));
                ps.setDate(3, null);
                ps.setBoolean(4, false);

                rows += ps.executeUpdate();//execute the query
            }
        } catch (SQLException e) {
            System.out.println("error in insert into orders picked by users");
            e.printStackTrace();
        }

        return rows > 0;//return true if a row is inserted
    }

    /**
     * loadOrders will return all orders in the database
     *
     * @return
     */
    public List<Orders> loadOrders() {

        //list to hold all orders in the DB
        List<Orders> orders = new ArrayList<>();

        //query
        String sql = "select O.Order_ID, U.username, O.OrderDate from Orders as O inner join OrdersPickedByUsers as OP on OP.Order_ID = O.Order_ID inner join Users as U on OP.User_ID = U.User_ID where packed = false";

        try {
            //prepare a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the result table, adding each entry into the orders list
            while (rs.next()) {

                String OrderID = rs.getString("Order_ID");
                String AssignedTo = rs.getString("username");
                String OrderDate = rs.getString("OrderDate");

                orders.add(new Orders(OrderID, AssignedTo, OrderDate));//add the returned value to the orders list

            }

        } catch (SQLException e) {
            System.out.println("error in loadOrders");
            e.printStackTrace();
        }

        return orders;//return the orders list
    }

    /**
     * getAllTrucks will get all trucks in the database and load them into a
     * list
     *
     * @return
     */
    public Map<String, String> getAllTrucks() {

        //map containing all trucks pakced
        Map<String, String> allpackedTrucks = new HashMap<>();

        //query
        String sql = "select TruckID, DateReceived, Unpacked from truck";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the result set, only adding rows if unpacked is set to false
            while (rs.next()) {

                String TruckID;
                String DateReceived;

                //if unpacked is false, add it to the trucks map
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

        return allpackedTrucks;//return map
    }

    /**
     * getSKUsOnTruck will get all of the SKUs on a given truck ID
     *
     * @param truckID
     * @return
     */
    public Map<String, String> getSKUsonTruck(int truckID) {

        //map containing all skus on the given truck ID
        Map<String, String> SKUsonTrucks = new HashMap<>();

        //query
        String sql = "select SKU, QuantityOfSKU from QuantityOfSKUOnTruck where TruckID = ?";

        try {
            //create a query to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, truckID);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the result set, adding each row to the skusontrucks map
            while (rs.next()) {

                String SKU = rs.getString("SKU");
                String Quantity = String.valueOf(rs.getInt("QuantityOfSKU"));

                SKUsonTrucks.put(SKU, Quantity);
            }

        } catch (SQLException e) {
            System.out.println("error in get skus on truck");
            e.printStackTrace();
        }

        return SKUsonTrucks;//return the allskusontrucks map for processing
    }

    /**
     * processTruck will process a truck and set it unpacked to true
     *
     * @param truckID
     * @return
     */
    public boolean processTruck(int truckID, String username, Map<String, String> allSKUsOnTruck) {

        int rows = 0;
        int rows2 = 0;
        int rows3 = 0;

        //query
        String sql = "update Truck set Unpacked = true where TruckID = ?";
        String sql2 = "insert into UsersUnpackingTruck values (?,?,CURRENT_DATE)";//used for assign a truck to a user
        String sql3 = "update product set Stock = Stock + ? where SKU = ?";

        try {
            //prepare a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, truckID);

            PreparedStatement ps2 = c.prepareStatement(sql2);
            ps2.setInt(1, getUserID(username));
            ps2.setInt(2, truckID);

            PreparedStatement ps3 = c.prepareCall(sql3);

            for (Map.Entry<String, String> entry : allSKUsOnTruck.entrySet()) {
                ps3.setInt(1, Integer.parseInt(entry.getValue()));
                ps3.setString(2, entry.getKey());

                rows3 += ps3.executeUpdate();
            }

            //execute the query
            rows += ps.executeUpdate();
            rows2 += ps2.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in processtruck");
            e.printStackTrace();
        }

        return rows > 0 && rows2 > 0 && rows3 > 0;//return true if at least 1 row is updated
    }

    /*
    for (Map.Entry<String, String> entry : SKUsToAddToOrder.entrySet()) {

                ps.setString(1, entry.getKey());
                ps.setString(2, orderID);
                ps.setInt(3, Integer.parseInt(entry.getValue()));

                rows += ps.executeUpdate();//execute the query

            }
     */
    /**
     * getAllUnpackedTrucks will get all of the unpacked trucks in the database
     *
     * @return
     */
    public Map<String, String> getAllunPackedTrucks() {

        //map containing all trucks that have been unpacked
        Map<String, String> allUnpackedTrucks = new HashMap<>();

        //query
        String sql = "select TruckID, DateReceived, Unpacked from truck";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //iterate through the result set, only adding rows that have unpacked set to true
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

        return allUnpackedTrucks;//return the map
    }

    /**
     * insertNewTruck will insert a new truck into the database
     *
     * @param TruckID
     * @param DeliveryDate
     * @return
     * @throws ParseException
     */
    public boolean insertNewTruck(int TruckID, String DeliveryDate) throws ParseException {

        int rows = 0;

        //query
        String sql = "insert into Truck values (?,?,false)";

        //convert the delivery date to a date type for SQL insertion
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date utilDate = f.parse(DeliveryDate);

        Date SQLDOB = new Date(utilDate.getTime());

        try {
            //prepare and set the statement for execution
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, TruckID);
            ps.setDate(2, SQLDOB);

            //execute the query
            rows += ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in createnewtruck");
            e.printStackTrace();
        }

        return rows > 0;//return true if at least 1 row is inserted
    }

    /**
     * insertIntoSKUsOnTrucks will insert all SKUs on a given truckID
     *
     * @param SKUsToAddToTruck
     * @param truckID
     * @return
     */
    public boolean insertIntoSKUsOnTrucks(Map<String, String> SKUsToAddToTruck, int truckID) {

        int rows = 0;

        //query
        String sql = "insert into QuantityOfSKUOnTruck values (?,?,?)";

        try {
            //create a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);

            //iterate through the given SKU map, adding each entry for the given truck ID into the database
            for (Map.Entry<String, String> entry : SKUsToAddToTruck.entrySet()) {

                ps.setInt(1, truckID);
                ps.setString(2, entry.getKey());
                ps.setInt(3, Integer.parseInt(entry.getValue()));

                rows += ps.executeUpdate();//execute the query

            }

        } catch (SQLException e) {
            System.out.println("error in insertSKUsOnTrucks");
            e.printStackTrace();
        }

        return rows > 0;//return true if the number of rows inserted is 1 or greater
    }

    /**
     * getTruckExists will return if a truck exists or not
     *
     * @param TruckID
     * @return
     */
    public boolean getTruckExists(int TruckID) {

        //query
        String sql = "select * from Truck where TruckID = ?";

        try {
            //create and prepare a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, TruckID);

            //execute the query
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;//return true if there is a result
            }

        } catch (SQLException e) {
            System.out.println("error in get truck exists");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * requiresChange will determine if a user requires a password change or not
     *
     * @param username
     * @return
     */
    public boolean reuqiresChange(String username) {

        int userID = getUserID(username);

        //query
        String sql = "select requiresPWChange from users where user_ID = ?";

        try {
            //prepare a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, userID);

            //execute the query
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return rs.getBoolean("requiresPWChange");//return true if the user requires a password change
            }

        } catch (SQLException e) {
            System.out.println("error in requires change");
            e.printStackTrace();
        }

        return false;//return false by default
    }

    /**
     * removePWChange will determine if a user has changed their password, and
     * set requires password change to false
     *
     * @param username
     */
    public void removePWChange(String username) {

        //query
        String sql = "update users set requiresPWChange = false where username = ?";

        try {
            //prepare a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            //execute the query
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in remove password change");
            e.printStackTrace();
        }

    }

    /**
     * loadFreeLocations will load all locations in the database that are NOT
     * occupied
     *
     * @return
     */
    public List<String> loadFreeLocations() {

        //list containing all locations
        List<String> locs = new ArrayList<>();

        //query
        String sql = "select AisleNumber, AisleBay, SubBay from AisleBaySubBay where occupied = false";

        String addToList = "";//location (three ints combined into one string)

        try {
            //create a statement
            PreparedStatement ps = c.prepareStatement(sql);

            //execute it
            ResultSet rs = ps.executeQuery();

            //iterate through it, adding each row to the locations list
            while (rs.next()) {

                String aisleNum = String.valueOf(rs.getInt("AisleNumber"));

                String bayNum = String.valueOf(rs.getInt("AisleBay"));

                String SubBay = String.valueOf(rs.getInt("SubBay"));

                addToList = aisleNum + "-" + bayNum + "-" + SubBay;

                locs.add(addToList);

            }

        } catch (SQLException e) {
            System.out.println("error in loadFreeLocations");
            e.printStackTrace();
        }
        return locs;//return the locs list
    }

    /**
     * get all departments in the database
     *
     * @return
     */
    public Map<String, String> getAllDepts() {

        //map containing the department ID and department name
        Map<String, String> allDepts = new HashMap<>();

        //query
        String sql = "select deptID, departmentName from departments";

        try {
            //create and execute the query
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            //iterate through the list, adding each row to the allDepts list
            while (rs.next()) {

                String deptID = String.valueOf(rs.getInt("deptID"));
                String deptName = rs.getString("departmentName");

                allDepts.put(deptID, deptName);

            }

        } catch (SQLException e) {
            System.out.println("error in getAllDepts");
            e.printStackTrace();

        }

        return allDepts;//return the alldepts list

    }

    /**
     * getDisplayName will get the display name for a given username
     *
     * @param username
     * @return
     */
    public String getDisplayName(String username) {

        String displayName = "";

        //query
        String sql = "select Display_Name from users where username = ?";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            //execute the query
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                displayName = rs.getString("Display_Name");//return the display name
            }

        } catch (SQLException e) {
            System.out.println("error in get display name");
            e.printStackTrace();
        }

        return displayName;//return to the display name
    }

    /**
     * loadUserData will retrieve all user data and put in a list to be returned
     *
     * @param username
     * @return
     */
    public List<User> loadUserData(String username) {

        //list containing all of user data
        List<User> userData = new ArrayList<>();

        //query
        String sql = "select First_Name, Last_Name, DateOfBirth, Gender, PayRate, Position from users where username = ?";

        try {
            //create a statement for execution
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);

            //execute the query
            ResultSet rs = ps.executeQuery();

            //get the results
            while (rs.next()) {
                //I use check for null here since some values pre-input validation can be null, this will catch these null values for proper processing
                String FName = rs.getString("First_Name");
                String LName = rs.getString("Last_Name");
                Date DOB = rs.getDate("DateOfBirth");
                String DOBAsString;
                if (DOB == null) {
                    DOBAsString = "Not Set";
                } else {
                    DOBAsString = DOB.toString();
                }

                String Gender = rs.getString("Gender");
                if (Gender == null) {
                    Gender = "Not Set";
                }
                float payRate = rs.getFloat("PayRate");

                String position = rs.getString("Position");
                if (position == null) {
                    position = "Not Set";
                }

                //add the retrieved values to the list
                userData.add(new User(FName, LName, DOBAsString, payRate, position, Gender));
            }

        } catch (SQLException e) {
            System.out.println("error in loadUserData");
            e.printStackTrace();
        }

        return userData;//return the list
    }

    /**
     * updateUserInfo will update of the passed in user information
     *
     * @param fName
     * @param lName
     * @param DOB
     * @param Gender
     * @param payRate
     * @param position
     * @param username
     * @return
     * @throws ParseException
     */
    public boolean updateUserInfo(String fName, String lName, String DOB, String Gender, float payRate, String position, String username) throws ParseException {

        int rows = 0;

        //set Display name
        String displayName = fName + " " + lName;

        //query
        String sql = "update users set Display_Name = ?, First_Name = ?, Last_Name = ?, DateOfBirth = ?, Gender = ?, PayRate = ?, Position = ? where username = ?";

        //convert string date to SQL date
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date utilDate = f.parse(DOB);

        Date SQLDOB = new Date(utilDate.getTime());

        try {
            //create and set statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, displayName);
            ps.setString(2, fName);
            ps.setString(3, lName);
            ps.setDate(4, SQLDOB);
            ps.setString(5, Gender);
            ps.setFloat(6, payRate);
            ps.setString(7, position);
            ps.setString(8, username);

            //execute query
            rows += ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in update user info");
            e.printStackTrace();
        }

        return rows > 0;//return true if the user info was updated
    }

    /**
     * getAllSKUsOnOrders will return a map of all skus on the order given
     * @param orderID
     * @return 
     */
    public Map<String, String> getAllSKUsOnOrders(String orderID) {

        //map containing all SKUs on a given order
        Map<String, String> AllSKUsOnOrders = new HashMap<>();
        
        //query
        String sql = "select SKU, SKUQuantity from ProductsOnOrders where Order_ID = ?";

        try {
            //create a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, orderID);

            //execute query
            ResultSet rs = ps.executeQuery();

            //iterate through the results, adding each result into the sku map
            while (rs.next()) {

                String SKU = rs.getString("SKU");
                String quan = String.valueOf(rs.getInt("SKUQuantity"));

                AllSKUsOnOrders.put(SKU, quan);
            }

        } catch (SQLException e) {
            System.out.println("error in getAllSKUsOnOrders");
            e.printStackTrace();
        }

        return AllSKUsOnOrders;//return the sku map to the controller
    }

    /**
     * complete order will mark a given order as complete
     * 
     * @param orderID
     * @return 
     */
    public boolean completeOrder(String orderID) {

        int rows = 0;
        int rows2 = 0;

        //two queries needed to update both orders table and orders
        String sql = "update orderspickedbyusers set DateCompleted = CURRENT_DATE, OrderPicked = true where order_ID = ?";
        String sql2 = "update orders set packed = true, ShippingStatus = ? where order_ID = ?";

        try {
            //creatw two statements to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            PreparedStatement ps2 = c.prepareStatement(sql2);

            //set order ID
            ps.setString(1, orderID);
            ps2.setString(1, "Awaiting Delivery");
            ps2.setString(2, orderID);

            //execute both queries
            rows += ps.executeUpdate();
            rows2 += ps2.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error in complete order");
            e.printStackTrace();
        }

        return rows > 0 && rows2 > 0;//return true if an update/insert inserts/updates
    }

    /**
     * orderisAssigned will return true if a given order is assigned already
     * 
     * used so no one order is assigned to two people 
     * 
     * (caused issues on user deletion).
     * @param orderID
     * @return 
     */
    public boolean orderIsAssigned(String orderID) {

        //query
        String sql = "select * from OrdersPickedByUsers where Order_ID = ?";

        try {
            //prepare a statement to be executed
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, orderID);

            //execute query
            ResultSet rs = ps.executeQuery();

            return rs.next();//return true if something is in the result set

        } catch (SQLException e) {
            System.out.println("error in orderIsAssigned");
            e.printStackTrace();
        }

        return false;//return false by default
    }

}
