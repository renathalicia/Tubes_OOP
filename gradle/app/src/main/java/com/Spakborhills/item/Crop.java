package com.Spakborhills.item; // Pastikan ini sesuai dengan struktur package Anda
import com.Spakborhills.main.GamePanel;

public class Crop extends Item {

    private int jumlahPerPanen;
    private int energiYangDiberikan;

    /**
     * Konstruktor untuk Crop.
     *
     * @param name 
     * @param buyPrice 
     * @param sellPrice 
     * @param gp 
     * @param description 
     * @param jumlahPerPanen
     * @param imagePath 
     */
    public Crop(String name, int buyPrice, int sellPrice, GamePanel gp, String description, int jumlahPerPanen, String imagePath) {
        super(name, buyPrice, sellPrice, gp); 
        this.description = description;
        this.jumlahPerPanen = jumlahPerPanen;
        this.energiYangDiberikan = 3; 
        this.stackable = true; 
        this.image = setUpImage(imagePath); 
    }

    @Override
    public int getEnergyValue() {
        return 3; 
    }

    // Getter
    public int getJumlahPerPanen() {
        return jumlahPerPanen;
    }

    public int getEnergiYangDiberikan() {
        return energiYangDiberikan;
    }

    @Override
    public String getCategory() {
        return "Crop";
    }

    @Override
    public void use() {
        if (gp.player != null) {
            gp.player.gainEnergy(this.energiYangDiberikan); 
            gp.ui.showMessage("Anda memakan " + getName() + " dan mendapatkan " + this.energiYangDiberikan + " energi.");
            System.out.println(gp.player.name + " memakan " + getName() + ". Energi +" + this.energiYangDiberikan); 

            boolean itemRemoved = gp.player.removeItem(this.getName(), 1); 
            if (!itemRemoved) {
                System.out.println("Peringatan: Gagal mengurangi " + getName() + " dari inventory setelah digunakan.");
            }
        } else {
            System.out.println("Objek Player tidak ditemukan di GamePanel. Crop tidak bisa digunakan.");
            gp.ui.showMessage("Error: Tidak bisa menggunakan " + getName() + ".");
        }
    }
}