package item;

import java.awt.image.BufferedImage;
public abstract class Item {
    public String name;
    public int buyPrice;
    public int sellPrice;
    public BufferedImage image;
    public String description;
    //konstruktor
    public Item(String name, int buyPrice, int sellPrice) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.description = description;
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
     public BufferedImage getImage() {
        return image;
    }
    public String getDescription() {
        return description;
    }
    //method abstak untuk mengenali item termasuk ke kategori apa saja, wajib diimplementasikan di setiap inheritance dari kelas Item.java
    public abstract String getCategory();
}
