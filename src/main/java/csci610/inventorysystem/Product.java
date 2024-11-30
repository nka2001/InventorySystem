package csci610.inventorysystem;

/**
 * product class used by the products table, used for tableview when there is
 * more than two columns
 *
 * @author nicka
 */
public class Product {

    //fields to be set by database manager
    private String SKU;
    private String ProductTitle;
    private int Department;
    private int Stock;

    /**
     * overloaded constructor used to assign all values
     *
     * @param sku
     * @param prodTitle
     * @param dept
     * @param stock
     */
    public Product(String sku, String prodTitle, int dept, int stock) {
        this.SKU = sku;
        this.ProductTitle = prodTitle;
        this.Department = dept;
        this.Stock = stock;
    }

    /**
     * sets SKU
     *
     * @param sku
     */
    public void setSKU(String sku) {
        this.SKU = sku;
    }

    /**
     * sets product title
     *
     * @param prodTitle
     */
    public void setProdTitle(String prodTitle) {
        this.ProductTitle = prodTitle;
    }

    /**
     * sets department
     *
     * @param dept
     */
    public void setDepartment(int dept) {
        this.Department = dept;
    }

    /**
     * sets stock
     *
     * @param stock
     */
    public void setStock(int stock) {
        this.Stock = stock;
    }

    /**
     * gets SKU
     *
     * @return
     */
    public String getSKU() {
        return SKU;
    }

    /**
     * gets product title
     *
     * @return
     */
    public String getProductTitle() {
        return ProductTitle;
    }

    /**
     * gets department
     *
     * @return
     */
    public int getDepartment() {
        return Department;
    }

    /**
     * gets current stock
     *
     * @return
     */
    public int getStock() {
        return Stock;
    }

}
