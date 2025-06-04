package com.Spakborhills.object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.Spakborhills.main.GamePanel;
import com.Spakborhills.main.UtilityTool;

public class OBJ_HouseOcean extends SuperObject {
    GamePanel gp;

    public OBJ_HouseOcean(GamePanel gp) {
        super(); 
        this.gp = gp; 

        name = "OceanHouse";
        int tileSpanWidth = 4;  
        int tileSpanHeight = 4; 

        this.width = gp.tileSize * tileSpanWidth;
        this.height = gp.tileSize * tileSpanHeight;

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/objects/beachhouse.png"));

            if (originalImage != null) {
                this.image = UtilityTool.scaleImage(originalImage, this.width, this.height);

                collision = false; 

            } else {
                System.err.println("OBJ_House: Gambar asli untuk FarmHouse null. Path mungkin salah: /objects/beachhouse.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("OBJ_House: Gagal memuat atau menskalakan gambar beachhouse.");
        }
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