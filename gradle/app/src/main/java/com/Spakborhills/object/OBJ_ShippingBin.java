package com.Spakborhills.object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.Spakborhills.main.GamePanel;

public class OBJ_ShippingBin extends SuperObject {
    GamePanel gp;

    public OBJ_ShippingBin(GamePanel gp) {
        super(); 
        this.gp = gp; 

        name = "Shipping Bin";
        int tileSpanWidth = 3;  // Lebar objek ini adalah 6 tile
        int tileSpanHeight = 2; // Tinggi objek ini adalah 6 tile
        this.width = gp.tileSize * tileSpanWidth;
        this.height = gp.tileSize * tileSpanHeight;

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/objects/shippingbin.png"));
            if (originalImage != null) {
                this.image = uTool.scaleImage(originalImage, gp.tileSize, gp.tileSize);
            } else {
                System.err.println("Error: Gambar shippingbin.png tidak ditemukan.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;

        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = this.width;   // Lebar area solid sama dengan lebar objek
        solidArea.height = this.height;  // Tinggi area solid sama dengan tinggi objek

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        // Hitung posisi di layar relatif terhadap pemain/kamera
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + this.width > gp.player.worldX - gp.player.screenX &&
                worldX - this.width < gp.player.worldX + gp.player.screenX + gp.screenWidth && // Tambah gp.screenWidth untuk margin
                worldY + this.height > gp.player.worldY - gp.player.screenY &&
                worldY - this.height < gp.player.worldY + gp.player.screenY + gp.screenHeight) { // Tambah gp.screenHeight

            g2.drawImage(image, screenX, screenY, this.width, this.height, null);
        }
    }
}