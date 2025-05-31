package com.Spakborhills.object;

import com.Spakborhills.main.GamePanel;
import com.Spakborhills.main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OBJ_Pond extends SuperObject {
    GamePanel gp;
    // UtilityTool uTool = new UtilityTool(); // Tidak perlu inisialisasi di sini jika hanya untuk penskalaan sekali.
    // Panggil langsung jika Anda memiliki metode statis,
    // atau teruskan instance ke konstruktor UtilityTool jika ada state.

    public OBJ_Pond(GamePanel gp) {
        super(); // Penting: Panggil konstruktor SuperObject dengan GamePanel jika SuperObject membutuhkannya
        // Asumsi SuperObject punya konstruktor SuperObject(GamePanel gp)
        this.gp = gp; // Simpan referensi GamePanel jika diperlukan untuk operasi lain di OBJ_House

        name = "Pond";

        // --- PENTING: Tentukan dimensi objek dalam jumlah tile ---
        int tileSpanWidth = 4;  // Lebar objek ini adalah 6 tile
        int tileSpanHeight = 3; 

        this.width = gp.tileSize * tileSpanWidth;
        this.height = gp.tileSize * tileSpanHeight;

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/objects/pond.png")); // Pastikan path benar

            if (originalImage != null) {
                // Jika UtilityTool.scaleImage adalah static:
                this.image = UtilityTool.scaleImage(originalImage, this.width, this.height);
                // Jika uTool adalah instance member dari SuperObject (dan diinisialisasi):
                // this.image = uTool.scaleImage(originalImage, this.width, this.height);
            } else {
                System.err.println("OBJ_Pond: Gambar asli untuk Pond null. Path mungkin salah: /objects/pond.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("OBJ_Pond: Gagal memuat atau menskalakan gambar Pond.");
        }

        collision = true; 

        this.solidArea.width = this.width;
        this.solidArea.height = this.height;
        this.solidArea.x = 0; // Offset X area solid relatif terhadap worldX objek
        this.solidArea.y = 0; // Offset Y area solid relatif terhadap worldY objek
        this.solidAreaDefaultX = 0; // Untuk reset di CollisionChecker
        this.solidAreaDefaultY = 0;
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        // Hitung posisi di layar relatif terhadap pemain/kamera
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Optimasi: Hanya gambar jika objek ada di dalam atau dekat viewport kamera
        // Menggunakan 'this.width' dan 'this.height' (yang sudah dalam piksel)
        if (worldX + this.width > gp.player.worldX - gp.player.screenX &&
                worldX - this.width < gp.player.worldX + gp.player.screenX + gp.screenWidth && // Tambah gp.screenWidth untuk margin
                worldY + this.height > gp.player.worldY - gp.player.screenY &&
                worldY - this.height < gp.player.worldY + gp.player.screenY + gp.screenHeight) { // Tambah gp.screenHeight

            // INI ADALAH PERBAIKAN PENTING: Gunakan this.width dan this.height
            // yang sudah dihitung sebagai dimensi piksel objek
            g2.drawImage(image, screenX, screenY, this.width, this.height, null);
        }
    }
}