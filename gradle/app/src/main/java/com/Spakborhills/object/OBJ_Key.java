package com.Spakborhills.object;

import com.Spakborhills.main.GamePanel;
import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_Key extends SuperObject{
    GamePanel gp;

    public OBJ_Key(GamePanel gp){
        this.gp=gp;
        name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
