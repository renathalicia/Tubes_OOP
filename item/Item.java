package item;

public abstract class Item {
    public String name;
    public int buyPrice;
    public int sellPrice;
    //konstruktor
    public Item(String name, int buyPrice, int sellPrice) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
    //getter & setter
    public String getName(){
        return name;
    }
    public int getBuyPrice() {
        return buyPrice;
    }
    public int getSellPrice() {
        return sellPrice;
    }
    //method abstak untuk mengenali item termasuk ke kategori apa saja, wajib diimplementasikan di setiap inheritance dari kelas Item.java
    public abstract String getCategory();
}
