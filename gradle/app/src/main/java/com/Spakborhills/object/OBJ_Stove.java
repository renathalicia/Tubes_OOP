package com.Spakborhills.object;

import com.Spakborhills.main.GamePanel;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_Stove extends SuperObject {
    GamePanel gp;
    
    public OBJ_Stove(GamePanel gp) {
        this.gp = gp;
        name = "Stove";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/stove.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback image if stove.png is missing
            try {
                image = ImageIO.read(getClass().getResourceAsStream("/objects/stove.png"));
                uTool.scaleImage(image, gp.tileSize, gp.tileSize);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        collision = true;
    }
}