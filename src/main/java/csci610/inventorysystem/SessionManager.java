package csci610.inventorysystem;

/**
 *
 * @author nicka
 */
public class SessionManager {

    private static SessionManager inst;
    
    private String username;
    
    private int palletID;
    
    private String locationSelected;

    private SessionManager() {
    }

    public static SessionManager getInstance() {

        if (inst == null) {
            inst = new SessionManager();
        }

        return inst;

    }
    
    public String getUser(){
        return username;
    }
    
    public void setUser(String username){
        this.username = username.toLowerCase();
    }
    
    public int getPalletID(){
        return palletID;
    } 
    
    public void setPalletID(int palletID){
        this.palletID = palletID;
    }
    
    public String getLocationID(){
        return locationSelected;
    }
    
    public void setLocationID(String location){
        this.locationSelected = location;
    }

}
