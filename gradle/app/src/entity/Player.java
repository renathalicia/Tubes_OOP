package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }
    public void setDefaultValues(){
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage(){
        try{
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_2.png"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void update(){
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed){
            if (keyH.upPressed){
                direction = "up";
                y = y - speed;
            }else if (keyH.downPressed){
                direction = "down";
                y = y + speed;
            }else if (keyH.leftPressed){
                direction = "left";
                x = x - speed;
            }else {
                direction = "right";
                x = x + speed;
            }
            spriteCounter++;
            if (spriteCounter > 10){
                if (spriteNum == 1){
                    spriteNum = 2;
                }else if (spriteNum == 2){
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }

    }
    public void draw(Graphics2D g2){
        BufferedImage image = null;
         if (direction.equals("up")){
            if (spriteNum == 1){
                image = up1;
            }else if (spriteNum == 2){
                image = up2;
            }
         }else if (direction.equals("down")){
             if (spriteNum == 1){
                 image = down1;
             }else if (spriteNum == 2){
                 image = down2;
             }
         }else if (direction.equals("left")){
             if (spriteNum == 1){
                 image = left1;
             }else if (spriteNum == 2){
                 image = left2;
             }
         }else if (direction.equals("right")){
             if (spriteNum == 1){
                 image = right1;
             }else if (spriteNum == 2){
                 image = right2;
             }
         }
         g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
