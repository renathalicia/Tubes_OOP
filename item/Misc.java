package item; // Pastikan ini sesuai dengan struktur package Anda

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
        this.stackable = true; // Item misc seperti coal atau firewood biasanya stackable
        this.image = setUpImage(imagePath); // Memanggil metode dari kelas Item untuk memuat gambar
    }

    @Override
    public String getCategory() {
        return "Misc"; // Implementasi metode abstrak dari Item
    }

    @Override
    public void use() {
        // Item miscellaneous mungkin memiliki kegunaan spesifik atau pasif.
        // Misalnya, coal dan firewood digunakan sebagai bahan bakar untuk memasak[cite: 188].
        // Metode use() langsung dari inventory mungkin tidak melakukan banyak hal.
        // super.use(); // Memanggil use() dari Item, yang akan mencetak "Using item: [nama item]"
        gp.ui.showMessage(getName() + " adalah item lain-lain.");
        System.out.println("Menggunakan item Misc: " + getName() + ". Kegunaan spesifik mungkin terkait dengan sistem lain (misalnya, crafting, fuel).");
    }

    // Anda bisa menambahkan atribut atau metode spesifik untuk semua item Misc di sini jika ada.
    // Untuk saat ini, atribut dari Item sudah cukup.
}