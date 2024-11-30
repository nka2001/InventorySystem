package csci610.inventorysystem;

/**
 * Pallet class, used by pallet / database manager to set up table view with
 * more than 2 columns
 *
 * @author nicka
 */
public class Pallet {

    //fields to be set by database manager
    private int palletID;
    private int quantity;
    private String location;

    /**
     * overloaded constructor will assign provided values without needing
     * get/set
     *
     * @param p
     * @param q
     * @param l
     */
    public Pallet(int p, int q, String l) {
        this.palletID = p;
        this.quantity = q;
        this.location = l;
    }

    /**
     * sets quantity
     *
     * @param q
     */
    public void setQuantity(int q) {
        this.quantity = q;
    }

    /**
     * sets pallet ID
     *
     * @param p
     */
    public void setPalletID(int p) {
        this.palletID = p;
    }

    /**
     * sets location
     *
     * @param l
     */
    public void setLocation(String l) {
        this.location = l;
    }

    /**
     * gets location
     *
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * gets pallet ID
     *
     * @return
     */
    public int getPalletID() {
        return palletID;
    }

    /**
     * gets quantity
     *
     * @return
     */
    public int getQuantity() {
        return quantity;
    }
}
