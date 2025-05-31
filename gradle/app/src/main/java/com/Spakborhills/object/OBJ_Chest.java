package com.Spakborhills.object;

import com.Spakborhills.main.GamePanel;

import java.io.IOException;

import javax.imageio.ImageIO;

public class OBJ_Chest extends SuperObject{
    GamePanel gp;
    public OBJ_Chest(GamePanel gp){
        this.gp=gp;
        name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 