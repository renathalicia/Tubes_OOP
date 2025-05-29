package item; // Pastikan ini sesuai dengan struktur package Anda

import main.GamePanel;
// import main.Player; // Anda mungkin perlu mengimpor Player jika belum

public class Food extends Item {

    private int energiYangDiberikan;

    /**
     * Konstruktor untuk Food.
     *
     * @param name Nama makanan.
     * @param buyPrice Harga beli makanan di toko (jika ada).
     * @param sellPrice Harga jual makanan.
     * @param gp GamePanel instance.
     * @param description Deskripsi makanan.
     * @param energiYangDiberikan Jumlah energi yang dipulihkan saat dikonsumsi.
     * @param imagePath Path ke gambar makanan (misalnya, "/items/food/fish_n_chips").
     */
    public Food(String name, int buyPrice, int sellPrice, GamePanel gp, String description, int energiYangDiberikan, String imagePath) {
        super(name, buyPrice, sellPrice, gp); // Memanggil konstruktor kelas Item
        this.description = description;
        this.energiYangDiberikan = energiYangDiberikan;
        this.stackable = true; // Makanan umumnya bisa ditumpuk, bisa disesuaikan jika perlu
        this.image = setUpImage(imagePath); // Memanggil metode dari kelas Item untuk memuat gambar
    }

    // Getter
    public int getEnergiYangDiberikan() {
        return energiYangDiberikan;
    }

    @Override
    public String getCategory() {
        return "Food"; // Implementasi metode abstrak dari Item
    }

    @Override
    public void use() {
        // Logika saat makanan dimakan oleh pemain
        if (gp.player != null) {
            gp.player.gainEnergy(this.energiYangDiberikan); // Menggunakan metode gainEnergy dari Player.java
            gp.ui.showMessage("Anda memakan " + getName() + " dan memulihkan " + this.energiYangDiberikan + " energi.");
            System.out.println(gp.player.name + " memakan " + getName() + ". Energi +" + this.energiYangDiberikan);

            // Menggunakan metode removeItem dari Player.java untuk mengurangi item dari inventory
            boolean itemRemoved = gp.player.removeItem(this.getName(), 1);
            if (!itemRemoved) {
                System.out.println("Peringatan: Gagal mengurangi " + getName() + " dari inventory setelah digunakan.");
                // Anda mungkin ingin menangani kasus ini lebih lanjut jika diperlukan
            }

        } else {
            System.out.println("Objek Player tidak ditemukan di GamePanel. Makanan tidak bisa digunakan.");
            gp.ui.showMessage("Error: Tidak bisa menggunakan " + getName() + ".");
        }
    }
}