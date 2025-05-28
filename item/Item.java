package item;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Item {
    public String name;
    public int buyPrice;
    public int sellPrice;
    public BufferedImage image;
    public String description;

    //konstruktor
    public Item(String name, int buyPrice, int sellPrice, String description) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.description = description;
    }

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

    public BufferedImage getImage() {

        return image;
    }

    protected void setImage(String imagePath) {
        try {
            // Menggunakan ClassLoader untuk mengakses resource dari JAR
            this.image = ImageIO.read(getClass().getResourceAsStream(imagePath));
            if (this.image == null) {
                System.err.println("Gagal memuat gambar: Gambar tidak ditemukan di " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Gagal memuat gambar untuk item: " + name + " dari path: " + imagePath);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Jalur sumber daya gambar tidak valid: " + imagePath + ". Error: " + e.getMessage());
        }
    }
    //default item bisa ditumpuk -> stacking
    public boolean isStackable() {
        return true;
    }

    public String getDescription() {
        return description;
    }

    //method abstak untuk mengenali item termasuk ke kategori apa saja, wajib diimplementasikan di setiap inheritance dari kelas Item.java
    public abstract String getCategory();
}
