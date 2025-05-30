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
        if (this.gp != null && this.gp.player != null) {
            this.gp.player.gainEnergy(this.energiYangDiberikan);
            
            // Ini akan menggunakan drawDialogueScreen() untuk menampilkan pesan
            this.gp.ui.currentDialogue = "Anda memakan " + getName() + ".\nEnergi pulih +" + this.energiYangDiberikan + "!";
            // Atur mode dialog jika Anda punya, agar GamePanel tahu cara menutupnya
            // this.gp.ui.setDialogueMode("SYSTEM_MESSAGE"); // atau "ITEM_USE_RESULT"
            this.gp.gameState = gp.dialogueState; // Pindah ke dialogueState untuk menampilkan pesan

            if (this.gp.player.removeItem(this.getName(), 1)) {
                this.gp.gameStateSystem.advanceTimeByMinutes(5); // Biaya waktu makan
            } else {
                // Handle jika item gagal dihapus (seharusnya tidak terjadi jika logika benar)
            }
        }
    }
}