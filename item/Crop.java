package item;

public class Crop extends Item {
    int quantity; //hasil didapat setiap kali harvest
    //konstruktor
    public Crop(String name, int buyPrice, int sellPrice, int quantity){
        super(name, buyPrice, sellPrice);
        this.quantity = quantity;
    }

    @Override
    public String getCategory() {
        return "Crop";
    }
    public int getQuantity() {
        return quantity;
    }
}
