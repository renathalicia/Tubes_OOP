package com.Spakborhills.object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.Spakborhills.main.GamePanel;
import com.Spakborhills.main.UtilityTool;


public class OBJ_Stove extends SuperObject {
    GamePanel gp;
    // UtilityTool uTool = new UtilityTool(); 

    public OBJ_Stove(GamePanel gp) {
        this.gp = gp; 

        name = "Stove";
        collision = true;
        int tileSpanWidth = 2;  
        int tileSpanHeight = 3; 
        this.width = gp.tileSize * tileSpanWidth;
        this.height = gp.tileSize * tileSpanHeight;
 
        solidArea = new Rectangle(0, 0, this.width, this.height); // Seluruh 2x3 tile
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        try {
            // Muat gambar asli terlebih dahulu
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/objects/stove.png"));

            if (originalImage != null) {
                this.image = UtilityTool.scaleImage(originalImage, this.width, this.height);
                collision = true; 

            } else {
                System.err.println("OBJ_Stove: Gambar asli untuk FarmHouse null. Path mungkin salah: /objects/stove.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("OBJ_Stove: Gagal memuat atau menskalakan gambar stove.");
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        // Hitung posisi di layar relatif terhadap pemain/kamera
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;    

        if (worldX + this.width > gp.player.worldX - gp.player.screenX &&
                worldX - this.width < gp.player.worldX + gp.player.screenX + gp.screenWidth && 
                worldY + this.height > gp.player.worldY - gp.player.screenY &&
                worldY - this.height < gp.player.worldY + gp.player.screenY + gp.screenHeight) { 

            g2.drawImage(image, screenX, screenY, this.width, this.height, null);
        }
    }
}