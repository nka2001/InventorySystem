package csci610.inventorysystem;

import java.util.ArrayList;

/**
 * userManager controller is used for keeping track of the current user through all pages moved between
 * @author nicka
 */
public class UserManager {

    //user array used for storing all logged in users (does not work in this inst since this is not a server-client model
    private static ArrayList<String> userArr;

    //singleton instance of userManager
    private static UserManager inst;

    //default constructor
    private UserManager() {
        userArr = new ArrayList<>();
    }

    /**
     * usermanager is singleton since all of the classes may need to refer back to the list of online users
     * @return 
     */
    public static UserManager getInstance() {
        if (inst == null) {
            inst = new UserManager();//setup the instance for all classes to use
        }

        return inst;//return the same instance for all classes that call it
    }

    /**
     * get user
     * @param user
     * @return 
     */
    public String getUser(String user) {

        String LowerUser = user.toLowerCase();

        for (String userArr1 : userArr) {

            if (userArr1.equals(LowerUser)) {
                return userArr1;
            }

        }

        return null;
    }

    /**
     * find user, returns true if the user is already online
     * @param user
     * @return 
     */
    public boolean findUser(String user) {

        String LowerUser = user.toLowerCase();

        for (String userArr1 : userArr) {

            if (userArr1.equals(LowerUser)) {
                return true;
            }

        }

        return false;
    }

    /**
     * add user to online list
     * @param user 
     */
    public void addUser(String user) {

        String LowerUser = user.toLowerCase();

        userArr.add(LowerUser);

    }

    /**
     * remove user from online list
     * @param user
     * @return 
     */
    public boolean removeUser(String user) {

        String LowerUser = user.toLowerCase();

        return userArr.remove(LowerUser);

    }

}
