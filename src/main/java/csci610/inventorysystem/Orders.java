package csci610.inventorysystem;

/**
 * Orders class, used by the orders tableview in the dashboard (since 4 item
 * maps do not exist or are harder to implement than this)
 *
 * @author nicka
 */
public class Orders {

    //values to be set by databasemanager
    private String OrderID;
    private String username;
    private String orderDate;

    /**
     * overloaded constructed, used by database manager to create a new order
     * with all the specified fields
     *
     * @param OID
     * @param UName
     * @param ODate
     */
    public Orders(String OID, String UName, String ODate) {
        this.OrderID = OID;
        this.orderDate = ODate;
        this.username = UName;
    }

    /**
     * returns order ID
     *
     * @return
     */
    public String getOrderID() {
        return OrderID;
    }

    /**
     * returns username
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * returns order date
     *
     * @return
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * sets order ID
     *
     * @param OID
     */
    public void setOrderID(String OID) {
        this.OrderID = OID;
    }

    /**
     * sets username
     *
     * @param UName
     */
    public void setUsername(String UName) {
        this.username = UName;
    }

    /**
     * sets order date
     *
     * @param ODate
     */
    public void setOrderDate(String ODate) {
        this.orderDate = ODate;
    }

}
