package object;

import main.GamePanel;
import main.UtilityTool;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;

public class OBJ_ShippingBin extends SuperObject{
    GamePanel gp;
    public OBJ_ShippingBin(GamePanel gp){
        this.gp=gp;
        name = "Shipping Bin";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/objects/shippingbin.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        collision = true;
    }
}
 
