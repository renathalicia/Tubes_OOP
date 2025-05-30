package item; // Pastikan ini sesuai dengan struktur package Anda

import main.GamePanel;
// import main.Player; // Anda mungkin perlu mengimpor Player jika belum

public class Crop extends Item {

    private int jumlahPerPanen;
    private int energiYangDiberikan;

    /**
     * Konstruktor untuk Crop.
     *
     * @param name Nama crop.
     * @param buyPrice Harga beli crop di toko (jika ada).
     * @param sellPrice Harga jual crop.
     * @param gp GamePanel instance.
     * @param description Deskripsi crop.
     * @param jumlahPerPanen Jumlah yang didapat saat panen dari satu tanaman.
     * @param imagePath Path ke gambar crop (misalnya, "/items/crops/parsnip").
     */
    public Crop(String name, int buyPrice, int sellPrice, GamePanel gp, String description, int jumlahPerPanen, String imagePath) {
        super(name, buyPrice, sellPrice, gp); // Memanggil konstruktor kelas Item
        this.description = description;
        this.jumlahPerPanen = jumlahPerPanen;
        this.energiYangDiberikan = 3; // Semua crop memberikan 3 energi [cite: 137]
        this.stackable = true; // Crop biasanya bisa ditumpuk
        this.image = setUpImage(imagePath); // Memanggil metode dari kelas Item untuk memuat gambar
    }

    @Override
    public int getEnergyValue() {
        return 3; // Sesuai spesifikasi, semua crop +3 energi
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
        return "Crops"; // Implementasi metode abstrak dari Item
    }

    @Override
    public void use() {
        if (gp.player != null) {
            gp.player.gainEnergy(this.energiYangDiberikan); // PERBAIKAN 1
            gp.ui.showMessage("Anda memakan " + getName() + " dan mendapatkan " + this.energiYangDiberikan + " energi.");
            System.out.println(gp.player.name + " memakan " + getName() + ". Energi +" + this.energiYangDiberikan); // PERBAIKAN 2

            boolean itemRemoved = gp.player.removeItem(this.getName(), 1); // PERBAIKAN 3
            if (!itemRemoved) {
                System.out.println("Peringatan: Gagal mengurangi " + getName() + " dari inventory setelah digunakan.");
            }
        } else {
            System.out.println("Objek Player tidak ditemukan di GamePanel. Crop tidak bisa digunakan.");
            gp.ui.showMessage("Error: Tidak bisa menggunakan " + getName() + ".");
        }
    }
}