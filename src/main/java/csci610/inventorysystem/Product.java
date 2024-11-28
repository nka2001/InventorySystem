
package csci610.inventorysystem;

/**
 *
 * @author nicka
 */
public class Product {
    
    private String SKU;
    private String ProductTitle;
    private int Department;
    private int Stock;
    
    public Product(String sku, String prodTitle, int dept, int stock){
        this.SKU = sku;
        this.ProductTitle = prodTitle;
        this.Department = dept;
        this.Stock = stock;
    }
    
    public void setSKU(String sku){
        this.SKU = sku;
    }
    
    public void setProdTitle(String prodTitle){
        this.ProductTitle = prodTitle;
    }
    
    public void setDepartment(int dept){
        this.Department = dept;
    }
    
    public void setStock(int stock){
        this.Stock = stock;
    }
    
    public String getSKU(){
        return SKU;
    }
    
    public String getProductTitle(){
        return ProductTitle;
    }
    
    public int getDepartment(){
        return Department;
    }
    
    public int getStock(){
        return Stock;
    }
    
}
