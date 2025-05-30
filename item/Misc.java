package item; 

import main.GamePanel;

public class Misc extends Item {

    /**
     * Konstruktor untuk item Miscellaneous (Lain-lain).
     *
     * @param name Nama item.
     * @param buyPrice Harga beli item di toko (jika bisa dibeli).
     * @param sellPrice Harga jual item.
     * @param gp GamePanel instance.
     * @param description Deskripsi item.
     * @param imagePath Path ke gambar item (misalnya, "/items/misc/coal").
     */
    public Misc(String name, int buyPrice, int sellPrice, GamePanel gp, String description, String imagePath) {
        super(name, buyPrice, sellPrice, gp); // Memanggil konstruktor kelas Item
        this.description = description;
        this.stackable = true;
        this.image = setUpImage(imagePath); 
    }

    @Override
    public String getCategory() {
        return "Misc"; 
    }

    @Override
    public void use() {
        gp.ui.showMessage(getName() + " adalah item lain-lain.");
        System.out.println("Menggunakan item Misc: " + getName() + ". Kegunaan spesifik mungkin terkait dengan sistem lain (misalnya, crafting, fuel).");
    }
}