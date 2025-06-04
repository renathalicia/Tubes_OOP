package com.Spakborhills.object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.Spakborhills.main.GamePanel;
import com.Spakborhills.main.UtilityTool;

public class OBJ_Pond extends SuperObject {
    GamePanel gp;

    public OBJ_Pond(GamePanel gp) {
        super(); 
        this.gp = gp; 

        name = "Pond";

        int tileSpanWidth = 4;  // Lebar objek ini adalah 6 tile
        int tileSpanHeight = 3; 

        this.width = gp.tileSize * tileSpanWidth;
        this.height = gp.tileSize * tileSpanHeight;

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/objects/pond.png")); // Pastikan path benar

            if (originalImage != null) {
                // Jika UtilityTool.scaleImage adalah static:
                this.image = UtilityTool.scaleImage(originalImage, this.width, this.height);
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
        this.solidArea.x = 0; 
        this.solidArea.y = 0; 
        this.solidAreaDefaultX = 0;
        this.solidAreaDefaultY = 0;
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
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