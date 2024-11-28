package csci610.inventorysystem;

import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author nicka
 */
public class AddSKUController {

    @FXML
    private Button backButton;
    @FXML
    private TextField SKUTF;
    @FXML
    private TextField ProdTitleTF;
    @FXML
    private TextField ProdDescTF;
    @FXML
    private TextField PriceTF;
    @FXML
    private TextField StockTF;
    @FXML
    private TextField DepartmentTF;
    @FXML
    private Button addProduct;
    
    private DatabaseManager dbm = new DatabaseManager();

    @FXML
    private void goBack(ActionEvent event) throws IOException {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Cancel Action");
        a.setHeaderText("Are you sure you want to cancel?");

        ButtonType byes = new ButtonType("Yes");
        ButtonType bno = new ButtonType("No");
        a.getButtonTypes().setAll(byes, bno);

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == byes) {

            App.setRoot("Dashboard");

        } else if (result.get() == bno) {

            a.close();

        }

    }

    private String newSKU;
    private String newProductTitle;
    private String newProductDesc;
    private float newPri;
    private int newStock;
    private int newDept;
    
    @FXML
    private void addProdToDB(ActionEvent event) {
        
        newSKU = SKUTF.getText();
        
        if(!dbm.findSKU(newSKU)){
            
            newProductTitle = ProdTitleTF.getText();
            newProductDesc = ProdDescTF.getText();
            String newPrice = PriceTF.getText();
            newPri = Float.parseFloat(newPrice);
            
            String stock = StockTF.getText();
            newStock = Integer.parseInt(stock);
            
            String dept = DepartmentTF.getText();
            newDept = Integer.parseInt(dept);
            
            if(validateInput()){
                
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Error adding product, one or many of the text fields are empty, please go back and fill it in");
                a.showAndWait();
                
                
            } else {
                
                if(dbm.addSKU(newSKU, newProductTitle, newProductDesc, newPri, newStock, newDept)){
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Product Added");
                    a.setHeaderText("Product Added Successfully!");
                    a.showAndWait();
                    
                    SKUTF.clear();
                    ProdTitleTF.clear();
                    ProdDescTF.clear();
                    PriceTF.clear();
                    StockTF.clear();
                    DepartmentTF.clear();
                    
                }
                
                
                  
            }
            
        } else {
            
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error SKU already in database");
            a.showAndWait();
            
        }
        

    }
    
    private boolean validateInput(){
        
        return newSKU.equalsIgnoreCase("") || newProductTitle.equalsIgnoreCase("") || newProductDesc.equalsIgnoreCase("") || newPri == 0 || newDept == 0;
    }

}
