package com.Spakborhills.object;

import com.Spakborhills.main.GamePanel;

import java.io.IOException;

import javax.imageio.ImageIO;

public class OBJ_Television extends SuperObject{
    GamePanel gp;
    public OBJ_Television(GamePanel gp){
        this.gp=gp;
        name = "Television";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/television.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        collision = true;
    }
}
 
