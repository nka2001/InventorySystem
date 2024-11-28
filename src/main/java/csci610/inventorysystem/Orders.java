
package csci610.inventorysystem;

/**
 *
 * @author nicka
 */
public class Orders {

    private String OrderID;
    private String username;
    private String orderDate;
    
    public Orders(String OID, String UName, String ODate){
        this.OrderID = OID;
        this.orderDate = ODate;
        this.username = UName;
    }
    
    public String getOrderID(){
        return OrderID;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getOrderDate(){
        return orderDate;
    }

    public void setOrderID(String OID){
        this.OrderID = OID;
    }
    
    public void setUsername(String UName){
        this.username = UName;
    }
    
    public void setOrderDate(String ODate){
        this.orderDate = ODate;
    }
    
    
}
