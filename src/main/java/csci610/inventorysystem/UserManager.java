package csci610.inventorysystem;

import java.util.ArrayList;

/**
 *
 * @author nicka
 */
public class UserManager {

    private static ArrayList<String> userArr;

    private static UserManager inst;

    private UserManager() {
        userArr = new ArrayList<>();
    }

    public static UserManager getInstance() {
        if (inst == null) {
            inst = new UserManager();
        }

        return inst;
    }

    public String getUser(String user) {

        String LowerUser = user.toLowerCase();

        for (String userArr1 : userArr) {

            if (userArr1.equals(LowerUser)) {
                return userArr1;
            }

        }

        return null;
    }

    public boolean findUser(String user) {

        String LowerUser = user.toLowerCase();

        for (String userArr1 : userArr) {

            if (userArr1.equals(LowerUser)) {
                return true;
            }

        }

        return false;
    }

    public void addUser(String user) {

        String LowerUser = user.toLowerCase();

        userArr.add(LowerUser);

    }

    public boolean removeUser(String user) {

        String LowerUser = user.toLowerCase();

        return userArr.remove(LowerUser);

    }

}
