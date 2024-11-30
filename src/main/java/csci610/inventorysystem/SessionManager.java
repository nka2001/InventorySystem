package csci610.inventorysystem;

/**
 * session manager class, this is used to access data shared across multiple
 * classes, such as keeping track of who the current logged in user is
 *
 * managed per session
 *
 * @author nicka
 */
public class SessionManager {

    //fields set and referenced by other classes
    private static SessionManager inst;//singleton

    private String username;

    private int palletID;

    private String locationSelected;

    private SessionManager() {
    }

    /**
     * this is a singleton class! it needs to be singleton because creating
     * multiple instances will not allow access to shared data
     *
     * singleton design ensures the same instance is referred to throughout
     * program execution
     *
     * @return
     */
    public static SessionManager getInstance() {

        if (inst == null) {
            inst = new SessionManager();//setup the current instance of session manager to whoever calls it(used by MANY classes)
        }

        return inst;//return the instance

    }

    /**
     * get user
     * @return 
     */
    public String getUser() {
        return username;
    }

    /**
     * set user
     * @param username 
     */
    public void setUser(String username) {
        this.username = username.toLowerCase();
    }

    /**
     * get pallet ID
     * @return 
     */
    public int getPalletID() {
        return palletID;
    }

    /**
     * set pallet ID
     * @param palletID 
     */
    public void setPalletID(int palletID) {
        this.palletID = palletID;
    }

    /**
     * get location ID
     * @return 
     */
    public String getLocationID() {
        return locationSelected;
    }

    /**
     * set location ID
     * @param location 
     */
    public void setLocationID(String location) {
        this.locationSelected = location;
    }

}
