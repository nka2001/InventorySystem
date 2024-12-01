package csci610.inventorysystem;

/**
 * user class used to hold data retrieved from database, similar to Orders, or Pallet
 * @author nicka
 */
public class User {

    //fields to be set by database retrieval
    private String fName;
    private String lName;
    private String DOB;
    private float payRate;
    private String position;
    private String gender;

    /**
     * constructor for setting all fields from data retrival
     * @param fname
     * @param lname
     * @param dob
     * @param pay
     * @param position
     * @param gender 
     */
    public User(String fname, String lname, String dob, float pay, String position, String gender) {

        this.fName = fname;
        this.lName = lname;
        this.DOB = dob;
        this.payRate = pay;
        this.position = position;
        this.gender = gender;

    }
    
    /**
     * get first name
     * @return 
     */
    public String getFName(){
        return fName;
    }
    
    /**
     * get last name
     * @return 
     */
    public String getLName(){
        return lName;
    }
    
    /**
     * get date of birth
     * @return 
     */
    public String getDOB(){
        return DOB;
    }
    
    /**
     * get pay rate
     * @return 
     */
    public float getPay(){
        return payRate;
    }
    
    /**
     * get position
     * @return 
     */
    public String getPos(){
        return position;
    }

    /**
     * get gender
     * @return 
     */
    public String getGender(){
        return gender;
    }
    
    /**
     * set gender
     * @param gender 
     */
    public void setGender(String gender){
        this.gender = gender;
    }
    
    
    /**
     * set position
     * @param position 
     */
    public void setPos(String position){
        this.position = position;
    }
    
    /**
     * set pay
     * @param pay 
     */
    public void setPay(float pay){
        this.payRate = pay;
    }
    
    /**
     * set date of birth
     * @param dob 
     */
    public void setDOB(String dob){
        this.DOB = dob;
    }
    
    /**
     * set last name
     * @param lName 
     */
    public void setLName(String lName){
        this.lName = lName;
    }
    
    /**
     * set first name
     * @param fName 
     */
    public void setFName(String fName){
        this.fName = fName;
    }
}
