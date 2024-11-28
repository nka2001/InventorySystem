package csci610.inventorysystem;

/**
 *
 * @author nicka
 */
public class Pallet {

    private int palletID;
    private int quantity;
    private String location;

    public Pallet(int p, int q, String l) {
        this.palletID = p;
        this.quantity = q;
        this.location = l;
    }
    
    public void setQuantity(int q){
        this.quantity = q;
    }
    
    public void setPalletID(int p){
        this.palletID = p;
    }
    
    public void setLocation(String l){
        this.location = l;
    }
    
    public String getLocation(){
        return location;
    }
    
    public int getPalletID(){
        return palletID;
    }

    public int getQuantity(){
        return quantity;
    }
}
