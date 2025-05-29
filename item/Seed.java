package item; // Pastikan ini sesuai dengan struktur package Anda

import main.GamePanel;

public class Seed extends Item {

    private int daysToHarvest;
    private String cropResultName; 
    private String plantingSeason; // ("Spring", "Summer", "Fall", "Any")

    /**
     * Konstruktor untuk Seed.
     *
     * @param name Nama bibit (e.g., "Parsnip Seeds").
     * @param buyPrice Harga beli bibit di toko.
     * @param sellPrice Harga jual bibit (biasanya setengah harga beli).
     * @param gp GamePanel instance.
     * @param description Deskripsi bibit.
     * @param daysToHarvest Jumlah hari hingga bibit siap panen.
     * @param cropResultName Nama Crop yang dihasilkan (e.g., "Parsnip").
     * @param plantingSeason Musim tanam yang sesuai untuk bibit ini.
     * @param imagePath Path ke gambar bibit (misalnya, "/items/seeds/parsnip_seeds").
     */
    public Seed(String name, int buyPrice, int sellPrice, GamePanel gp, String description,
                int daysToHarvest, String cropResultName, String plantingSeason, String imagePath) {
        super(name, buyPrice, sellPrice, gp); 
        this.description = description;
        this.daysToHarvest = daysToHarvest;
        this.cropResultName = cropResultName;
        this.plantingSeason = plantingSeason; 
        this.stackable = true; 
        this.image = setUpImage(imagePath); 
    }

    // Getter
    public int getDaysToHarvest() {
        return daysToHarvest;
    }

    public String getCropResultName() {
        return cropResultName;
    }

    public String getPlantingSeason() {
        return plantingSeason;
    }

    @Override
    public String getCategory() {
        return "Seeds"; // Implementasi metode abstrak dari Item
    }

    @Override
    public void use() {
        // "Menggunakan" bibit biasanya berarti mencoba menanamnya.
        // Logika penanaman aktual lebih cocok ditangani oleh aksi pemain (Player.plantSeed())
        // yang akan memeriksa tile, musim, dll.
        // Metode use() ini bisa memberikan pesan informatif.
        gp.ui.showMessage("Pilih tile yang sudah dibajak untuk menanam " + getName() + ".");
        System.out.println("Mencoba menggunakan bibit: " + getName() + ". Proses penanaman dilakukan melalui aksi pemain.");
    }
}