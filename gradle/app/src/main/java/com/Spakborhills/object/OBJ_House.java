package com.Spakborhills.object;

import com.Spakborhills.main.GamePanel;
import com.Spakborhills.main.UtilityTool; // Pastikan UtilityTool ada di package main

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_House extends SuperObject {
    GamePanel gp;
    // UtilityTool uTool = new UtilityTool(); // Tidak perlu inisialisasi di sini jika hanya untuk penskalaan sekali.
    // Panggil langsung jika Anda memiliki metode statis,
    // atau teruskan instance ke konstruktor UtilityTool jika ada state.

    public OBJ_House(GamePanel gp) {
        super(); // Penting: Panggil konstruktor SuperObject dengan GamePanel jika SuperObject membutuhkannya
        // Asumsi SuperObject punya konstruktor SuperObject(GamePanel gp)
        this.gp = gp; // Simpan referensi GamePanel jika diperlukan untuk operasi lain di OBJ_House

        name = "FarmHouse";

        // --- PENTING: Tentukan dimensi objek dalam jumlah tile ---
        int tileSpanWidth = 6;  // Lebar objek ini adalah 6 tile
        int tileSpanHeight = 6; // Tinggi objek ini adalah 6 tile

        // Hitung total dimensi piksel objek berdasarkan ukuran tile di GamePanel
        // Properti 'width' dan 'height' di SuperObject harus diatur di sini
        this.width = gp.tileSize * tileSpanWidth;
        this.height = gp.tileSize * tileSpanHeight;

        try {
            // Muat gambar asli terlebih dahulu
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/objects/farmhouse.png"));

            if (originalImage != null) {
                // Skalakan gambar menggunakan UtilityTool atau secara manual
                // Jika UtilityTool memiliki metode scaleImage(BufferedImage original, int width, int height)
                this.image = UtilityTool.scaleImage(originalImage, this.width, this.height);

                collision = true; // Objek ini bisa bertabrakan

            } else {
                System.err.println("OBJ_House: Gambar asli untuk FarmHouse null. Path mungkin salah: /objects/farmhouse.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("OBJ_House: Gagal memuat atau menskalakan gambar FarmHouse.");
        }
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